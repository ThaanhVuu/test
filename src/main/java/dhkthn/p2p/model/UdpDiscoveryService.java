package dhkthn.p2p.model;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class UdpDiscoveryService {
    private final PeerInfo myInfo;
    private final ExecutorService threadPool;
    private final PeerListener listener; // Giao tiếp ngược ra ngoài qua Interface

    private DatagramSocket socket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void start() {
        if (isRunning.get()) return;
        isRunning.set(true);

        threadPool.execute(() -> {
            try {
                // Port UDP có thể fix cứng hoặc theo quy ước
                socket = new DatagramSocket(myInfo.getPort());
                socket.setBroadcast(true);

                listener.onInfo("Discovery Service started on port " + myInfo.getPort());

                // Chạy 2 task song song: Lắng nghe và Quảng bá
                threadPool.execute(this::listenLoop);
                threadPool.execute(this::broadcastLoop);
            } catch (SocketException e) {
                listener.onError("UDP Start Error: " + e.getMessage());
            }
        });
    }

    public void stop() {
        isRunning.set(false);
        if (socket != null && !socket.isClosed()) socket.close();
    }

    private void broadcastLoop() {
        while (isRunning.get()) {
            try {
                // Protocol: DISCOVER:USER:PORT
                String msg = String.format("DISCOVER:%s:%d", myInfo.getUsername(), myInfo.getPort());
                byte[] data = msg.getBytes();

                // Gửi tới Broadcast Address
                InetAddress address = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(data, data.length, address, myInfo.getPort());

                socket.send(packet);
                Thread.sleep(3000);
            } catch (Exception e) {
                if (isRunning.get()) listener.onError("Broadcast Error: " + e.getMessage());
            }
        }
    }

    private void listenLoop() {
        byte[] buffer = new byte[1024];
        while (isRunning.get()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                if (msg.startsWith("DISCOVER:")) {
                    String[] parts = msg.split(":");
                    String remoteUser = parts[1];
                    int remotePort = Integer.parseInt(parts[2]);

                    if (!remoteUser.equals(myInfo.getUsername())) {
                        // Tìm thấy Peer -> Bắn sự kiện ra ngoài
                        PeerInfo foundPeer = new PeerInfo(remoteUser, packet.getAddress().getHostAddress(), remotePort);
                        listener.onPeerFound(foundPeer);
                    }
                }
            } catch (IOException e) {
                if (isRunning.get()) listener.onError("Listen Error: " + e.getMessage());
            }
        }
    }
}