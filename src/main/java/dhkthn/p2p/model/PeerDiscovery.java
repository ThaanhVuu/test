package dhkthn.p2p.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PeerDiscovery {
    private final PeerInfo myInfo;
    private final ExecutorService threadPool;
    private final PeerListener listener; // Giao tiếp ngược ra ngoài qua Interface

    private DatagramSocket socket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Map<PeerInfo, Long> activePeers = new ConcurrentHashMap<>();
    @NonFinal
    private final int BROADCAST_INTERVAL = 3000;
    @NonFinal
    private final int TIME_OUT = 5000;

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
                threadPool.execute(this::checkPeersLoop);
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
                String msg = String.format("DISCOVER:%s:%d", myInfo.getUsername(), myInfo.getPort());
                byte[] data = msg.getBytes();

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
                        // Tìm thấy Peer -> them vao list
                        PeerInfo foundPeer = new PeerInfo(remoteUser, packet.getAddress().getHostAddress(), remotePort);
                        activePeers.put(foundPeer, System.currentTimeMillis());
                    }
                }
            } catch (IOException e) {
                if (isRunning.get()) listener.onError("Listen Error: " + e.getMessage());
            }
        }
    }

    private void checkPeersLoop() {
        while (isRunning.get()) {
            try {
                Thread.sleep(BROADCAST_INTERVAL); // Đợi 3s

                long now = System.currentTimeMillis();

                // 1. Xoá các peer đã quá hạn (không gửi tin trong 5s vừa qua)
                activePeers.entrySet().removeIf(
                        entry -> (now - entry.getValue()) > TIME_OUT
                );

                // 2. Gửi danh sách Peer (KeySet) hiện tại ra Listener
                Set<PeerInfo> currentPeers = activePeers.keySet().stream().collect(Collectors.toSet());

                listener.onPeersListUpdated(currentPeers);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                listener.onError("Check loop error: " + e.getMessage());
            }
        }
    }
}