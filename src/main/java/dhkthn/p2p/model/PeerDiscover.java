package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerDiscover extends Peer{
    public void start() {
        super.setRunning(true);

        super.getPool().execute(() -> {
            try {
                this.listening();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        super.getPool().execute(() -> {
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
        super.setRunning(false);
        if (super.getDatagramSocket() != null && !super.getDatagramSocket().isClosed()) {
            super.getDatagramSocket().close();
        }
    }

    private void broadcast() throws IOException {
        super.getDatagramSocket().setBroadcast(true);
        String msg = "DISCOVER:" + super.getUsername() + ":" + super.getPort();
        byte[] buffer = msg.getBytes();
        InetAddress address = InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, super.getPort());

        getDatagramSocket().send(packet);
    }

    private void listening() throws IOException {
        System.out.println(super.getUsername() + " is listening on port: " + super.getPort());
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            super.getDatagramSocket().receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.startsWith("DISCOVER:")) {
                String[] parts = received.split(":");
                // parts[0] = DISCOVER
                // parts[1] = username
                // parts[2] = port

                String senderName = parts[1];

                // Nếu tên người gửi trùng với tên mình -> Bỏ qua
                if (senderName.equals(super.getUsername())) {
                    continue;
                }

                System.out.println(">> Found Peer: " + senderName + " at " + packet.getAddress().getHostAddress());
            }

            System.out.println("Received: " + received +
                    " from " + packet.getAddress().getHostAddress());
        }
    }
}
