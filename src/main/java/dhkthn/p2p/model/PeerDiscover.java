package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.*;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class PeerDiscover {
    private int port;
    private String username;

    public void broadcast(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, this.port);

            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listening() {
        try (DatagramSocket socket = new DatagramSocket(this.port)) {
            System.out.println(this.username + " is listening on port: " + this.port);
            byte[] buffer = new byte[1024];
            while (true){
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Received: " + received +
                        " from " + packet.getAddress().getHostAddress());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
