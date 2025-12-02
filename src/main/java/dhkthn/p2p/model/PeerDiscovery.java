package dhkthn.p2p.model;

import dhkthn.p2p.config.AppConfig;
import dhkthn.p2p.model.interfaces.IPeerDiscover;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.net.StandardSocketOptions;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PeerDiscovery extends Peer implements IPeerDiscover {

    /**
     * Socket client dùng để:
     *  - gửi DISCOVER_REQ (broadcast)
     *  - nhận DISCOVER_RES (scan)
     * <p>
     * Mỗi instance có 1 socket riêng, port nguồn random nhưng cố định trong suốt vòng đời.
     */
    private final DatagramSocket clientSocket;

    public PeerDiscovery() throws SocketException{
            this.clientSocket = new DatagramSocket(); // bind vào port ngẫu nhiên, dùng cho broadcast + scan
            this.clientSocket.setBroadcast(true);
            this.clientSocket.setSoTimeout(AppConfig.getTIME_OUT());
    }

    /**
     * Mở socket UDP cho phép nhiều process cùng bind vào cùng 1 port (SO_REUSEADDR),
     * dùng làm socket listener trên DISCOVERY_PORT.
     */
    private DatagramSocket openSharedSocket(int port) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(port));
        return channel.socket();
    }

    /**
     * 1) LISTENING:
     *    - Lắng nghe DISCOVER_REQ trên DISCOVERY_PORT
     *    - Khi nhận được, trả lời DISCOVER_RES (uuid | username | ip | port chat)
     */
    @Override
    public void listening() throws IOException {
        DatagramSocket socket = openSharedSocket(AppConfig.getDISCOVERY_PORT());
        socket.setBroadcast(true);
        byte[] buffer = new byte[1024];

        while (super.isRunning()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String msg = new String(packet.getData(), 0, packet.getLength()).trim();
            if (!msg.startsWith("DISCOVER_REQ")) continue;

            // TODO: có thể bỏ qua gói đến từ chính mình (check IP/UUID nếu muốn)
            // if (packet.getAddress().getHostAddress().equals(User.getLocalIP())) continue;

            DatagramPacket reply = getDatagramPacket(packet);

            socket.send(reply);
        }

        socket.close(); // TODO: có thể catch IOException khi close nếu muốn log
    }

    private static DatagramPacket getDatagramPacket(DatagramPacket packet) {
        String response = "DISCOVER_RES | "
                + AppConfig.getUser().getUuid() + " | "
                + AppConfig.getUser().getUsername() + " | "
                + User.getLocalIP() + " | "
                + AppConfig.getUser().getPort();

        byte[] data = response.getBytes();

        return new DatagramPacket(
                data,
                data.length,
                packet.getAddress(), // gửi ngược về IP nguồn
                packet.getPort()     // gửi ngược về PORT nguồn (port clientSocket của thằng broadcast)
        );
    }

    /**
     * 2) BROADCAST:
     *    - Gửi DISCOVER_REQ ra toàn LAN từ clientSocket
     *    - Reply sẽ quay về chính clientSocket này (scan() sẽ nhận)
     */
    @Override
    public void broadCast() throws IOException {
        String msg = "DISCOVER_REQ";
        byte[] data = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                AppConfig.getBroadcastAddress(),   // vd: 255.255.255.255
                AppConfig.getDISCOVERY_PORT()      // tất cả peer đều listen ở đây
        );

        // Gửi từ clientSocket, port nguồn = clientSocket.getLocalPort()
        this.clientSocket.send(packet);

        // NOTE: không close clientSocket, vì còn dùng để scan()
    }

    /**
     * 3) SCAN:
     *    - Dùng clientSocket (cùng port với broadcast)
     *    - Nhận tất cả DISCOVER_RES trong TIME_OUT
     *    - Parse ra User và trả về Set
     */
    @Override
    public Set<User> scan() {
        long start = System.currentTimeMillis();
        byte[] buffer = new byte[1024];
        Set<User> result = new HashSet<>();

        while (System.currentTimeMillis() - start < AppConfig.getTIME_OUT()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                this.clientSocket.receive(packet); // nhận DISCOVER_RES gửi về port clientSocket
            } catch (SocketTimeoutException e) {
                // Hết timeout 1 lần receive, tiếp tục vòng while kiểm tra tổng thời gian
                continue;
            } catch (IOException e) {
                // TODO: tùy bạn: hoặc break, hoặc continue, hoặc log chi tiết
                // For now: continue để không kill discovery chỉ vì 1 lần lỗi
                continue;
            }

            String msg = new String(packet.getData(), 0, packet.getLength()).trim();

            if (!msg.startsWith("DISCOVER_RES")) continue;

            String[] p = msg.split("\\|");
            if (p.length < 5) {
                // TODO: log format sai nếu cần
                continue;
            }

            User user = User.builder()
                    .uuid(UUID.fromString(p[1].trim()))
                    .username(p[2].trim())
                    .host(p[3].trim())
                    .port(Integer.parseInt(p[4].trim()))
                    .build();

            // TODO: có thể bỏ qua chính mình (so sánh uuid) nếu muốn
            if (!user.getUuid().equals(AppConfig.getUser().getUuid())) {
                result.add(user);
            }
        }

        return result;
    }

    /**
     * 4) START:
     *    - Bật cờ running
     *    - Cho thread trong pool chạy listening()
     *    - BROADCAST + SCAN sẽ do main/UI gọi ngoài
     */
    @Override
    public void start() {
        super.setRunning(true);
        AppConfig.getExecutor().execute(() -> {
            try {
                this.listening();
            } catch (IOException e) {
                // TODO: log lỗi rõ hơn, tránh RuntimeException trần trụi
                throw new RuntimeException("Discovery listener error", e);
            }
        });
    }

    /**
     * 5) STOP:
     *    - Tắt cờ running, listener sẽ thoát vòng while
     *    - Optional: close clientSocket luôn
     */
    @Override
    public void stop() {
        super.setRunning(false);
        // TODO: Nếu app shutdown hẳn, có thể đóng luôn clientSocket:
        // clientSocket.close();
    }
}
