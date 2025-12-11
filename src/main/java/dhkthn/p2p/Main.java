package dhkthn.p2p;

import dhkthn.p2p.model.PeerInfo;
import dhkthn.p2p.model.PeerListener;
import dhkthn.p2p.model.TcpFileTransferService;
import dhkthn.p2p.model.UdpDiscoveryService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        // 1. Setup Dependency
        ExecutorService pool = Executors.newCachedThreadPool();
        PeerInfo myInfo = new PeerInfo("DevHelperUser", "localhost", 8888);

        // 2. Implement Listener (Xử lý Output tại đây)
        PeerListener listener = new PeerListener() {
            @Override
            public void onPeerFound(PeerInfo peer) {
                System.out.println(">> [UI] Tìm thấy bạn mới: " + peer.getUsername() + " tại " + peer.getHost());
                // Logic thêm vào JList/Table...
            }

            @Override
            public void onFileReceived(File file, PeerInfo sender) {
                System.out.println(">> [UI] Đã nhận file: " + file.getAbsolutePath());
            }

            @Override
            public void onError(String message) {
                System.err.println("!! [ERROR] " + message);
            }

            @Override
            public void onInfo(String message) {
                System.out.println(">> [INFO] " + message);
            }
        };

        // 3. Khởi tạo Services
        UdpDiscoveryService discovery = new UdpDiscoveryService(myInfo, pool, listener);
        TcpFileTransferService transfer = new TcpFileTransferService(myInfo, pool, listener);

        // 4. Chạy
        discovery.start();
        transfer.startServer();
    }
}