package dhkthn.p2p.model;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class TcpFileTransferService {
    private final PeerInfo myInfo;
    private final ExecutorService threadPool;
    private final PeerListener listener;

    private ServerSocket serverSocket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    // --- SERVER SIDE (Nhận File) ---
    public void startServer() {
        if (isRunning.get()) return;
        isRunning.set(true);

        threadPool.execute(() -> {
            try {
                serverSocket = new ServerSocket(myInfo.getPort());
                listener.onInfo("File Server listening on port " + myInfo.getPort());

                while (isRunning.get()) {
                    Socket clientSocket = serverSocket.accept();
                    // Có kết nối đến -> Giao cho Worker Thread xử lý
                    threadPool.execute(() -> handleIncomingConnection(clientSocket));
                }
            } catch (IOException e) {
                if (isRunning.get()) listener.onError("TCP Server Error: " + e.getMessage());
            }
        });
    }

    private void handleIncomingConnection(Socket socket) {
        try (Socket s = socket;
             DataInputStream dis = new DataInputStream(s.getInputStream())) {

            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            listener.onInfo("Receiving file: " + fileName + " (" + fileSize + " bytes)");

            File dir = new File("received_files");
            if (!dir.exists()) dir.mkdirs();
            File destFile = new File(dir, System.currentTimeMillis() + "_" + fileName);

            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[65536];
                int read;
                long remaining = fileSize;
                while (remaining > 0) {
                    int toRead = (int) Math.min(buffer.length, remaining);
                    read = dis.read(buffer, 0, toRead);
                    if (read == -1) break;
                    fos.write(buffer, 0, read);
                    remaining -= read;
                }
            }

            // Bắn sự kiện nhận file thành công
            PeerInfo senderInfo = new PeerInfo("Unknown", s.getInetAddress().getHostAddress(), 0);
            listener.onFileReceived(destFile, senderInfo);

        } catch (IOException e) {
            listener.onError("Receive File Error: " + e.getMessage());
        }
    }

    // --- CLIENT SIDE (Gửi File) ---
    public void sendFile(PeerInfo targetPeer, File file) {
        threadPool.execute(() -> {
            try (Socket socket = new Socket(targetPeer.getHost(), targetPeer.getPort());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 FileInputStream fis = new FileInputStream(file)) {

                listener.onInfo("Sending file " + file.getName() + " to " + targetPeer.getUsername());

                dos.writeUTF(file.getName());
                dos.writeLong(file.length());

                byte[] buffer = new byte[65536];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, read);
                }

                listener.onInfo("File sent successfully to " + targetPeer.getUsername());

            } catch (IOException e) {
                listener.onError("Send File Error: " + e.getMessage());
            }
        });
    }

    public void stop() {
        isRunning.set(false);
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) { /* Ignored */ }
    }
}