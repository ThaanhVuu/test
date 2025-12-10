package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerDiscover{
    private Peer peer;

    public void start() {
        peer.setRunning(true);

        peer.getPool().execute(() -> {
            try {
                this.listening();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        peer.getPool().execute(() -> {
            try {
                while (true) {
                    this.broadcast();
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stop(){
        peer.setRunning(false);
        if (peer.getDatagramSocket() != null && !peer.getDatagramSocket().isClosed()) {
            peer.getDatagramSocket().close();
        }
    }

    private void broadcast() throws IOException {
        peer.getDatagramSocket().setBroadcast(true);
        String msg = "DISCOVER:" + peer.getUsername() + ":" + peer.getPort();
        byte[] buffer = msg.getBytes();
        InetAddress address = InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, peer.getPort());

        peer.getDatagramSocket().send(packet);
    }

    private void listening() throws IOException {
        System.out.println(peer.getUsername() + " is listening on port: " + peer.getPort());
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            peer.getDatagramSocket().receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.startsWith("DISCOVER:")) {
                String[] parts = received.split(":");
                // parts[0] = DISCOVER
                // parts[1] = username
                // parts[2] = port

                String senderName = parts[1];

                // Nếu tên người gửi trùng với tên mình -> Bỏ qua
                if (senderName.equals(peer.getUsername())) {
                    continue;
                }

                System.out.println(">> Found Peer: " + senderName + " at " + packet.getAddress().getHostAddress());
            }

            System.out.println("Received: " + received +
                    " from " + packet.getAddress().getHostAddress());
        }
    }
}
