//package dhkthn.p2p.model.storeTest;
//
//import dhkthn.p2p.config.AppConfig;
//import dhkthn.p2p.model.Peer;
//import lombok.*;
//
//import java.io.*;
//import java.net.*;
//import java.util.Enumeration;
//import java.util.concurrent.ExecutorService;
//
//@SuppressWarnings("ALL")
//@Builder @NoArgsConstructor @AllArgsConstructor @Getter @Setter
//public class PeerFileTransfer2 implements IPeerFileTransfer2 {
//    private ExecutorService pool;
//    @Builder.Default
//    private volatile boolean running = false;
//
//    private transient ServerSocket serverSocket;
//
//    @Override
//    public String getMyIp(){
//        try {
//            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
//            while (nics.hasMoreElements()) {
//                NetworkInterface nic = nics.nextElement();
//                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) {
//                    continue;
//                }
//                Enumeration<InetAddress> addrs = nic.getInetAddresses();
//                while (addrs.hasMoreElements()) {
//                    InetAddress addr = addrs.nextElement();
//                    if (addr instanceof Inet4Address) {
//                        return addr.getHostAddress();
//                    }
//                }
//            }
//        } catch (Exception ignored) {
//        }
//        return "127.0.0.1";
//    }
//
//    @Override
//    public void start() {
//        if (running) return;
//        running = true;
//
//        pool.execute(() -> {
//            try {
//                listening();
//            } catch (Exception e) {
//                e.printStackTrace();
//                running = false;
//            }
//        });
//    }
//
//    @Override
//    public void stop() {
//        if (!running) return;
//        running = false;
//
//        try {
//            if (serverSocket != null && !serverSocket.isClosed()) {
//                serverSocket.close(); // để unblock accept()
//            }
//        } catch (IOException ignored) {
//        }
//    }
//
//    @Override
//    public boolean isRunning() {
//        return running;
//    }
//
//    @Override
//    public void listening() {
//        try (ServerSocket ss = new ServerSocket(super.getPort())) {
//            this.serverSocket = ss;
//
//            while (running) {
//                try {
//                    Socket socket = ss.accept(); // sẽ bị ngắt khi stop() close ss
//                    pool.execute(() -> {
//                        try {
//                            receiveFile(socket);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                } catch (SocketException se) {
//                    // xảy ra khi stop() đóng ServerSocket
//                    if (running) se.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            if (running) throw new RuntimeException(e);
//        } finally {
//            running = false;
//        }
//    }
//
//    @Override
//    public void sendFile(File file, String host, int port) throws IOException {
//        if (file == null || !file.exists() || !file.isFile()) {
//            throw new FileNotFoundException("File not found: " + file);
//        }
//
//        try (Socket socket = new Socket(host, port);
//             DataOutputStream dos = new DataOutputStream(
//                     new BufferedOutputStream(socket.getOutputStream()));
//             FileInputStream fis = new FileInputStream(file)) {
//
//            dos.writeUTF(file.getName());
//            dos.writeLong(file.length());
//
//            byte[] buffer = new byte[1024 * 64];
//            long remaining = file.length();
//            int read;
//
//            while (remaining > 0 &&
//                    (read = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
//                dos.write(buffer, 0, read);
//                remaining -= read;
//            }
//
//            dos.flush();
//        }
//    }
//
//    @Override
//    public void receiveFile(Socket socket) throws IOException {
//        File dir = new File(AppConfig.getPATH_SAVE_FILE());
//        if (!dir.exists()) dir.mkdirs();
//
//        try (Socket s = socket;
//             DataInputStream dis = new DataInputStream(
//                     new BufferedInputStream(s.getInputStream()))) {
//
//            String fileName = dis.readUTF();
//            long fileSize = dis.readLong();
//
//            File file = new File(dir, fileName);
//
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                byte[] buffer = new byte[1024 * 64];
//                long total = 0;
//                int read;
//
//                while (total < fileSize &&
//                        (read = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - total))) != -1) {
//                    fos.write(buffer, 0, read);
//                    total += read;
//                }
//
//                fos.flush();
//
//                if (total != fileSize) {
//                    System.out.println("File corrupted! Deleting...");
//                    System.out.println(file.delete()
//                            ? "Deleted corrupt file success!"
//                            : "Deleted corrupt file fail!");
//                }
//            }
//        }
//    }
//}
