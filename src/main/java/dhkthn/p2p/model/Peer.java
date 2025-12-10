package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Peer {
    private int port;
    private String username;
    private ExecutorService pool;
    private boolean running;
    private DatagramSocket datagramSocket;

    private PeerDiscover peerDiscover;
}
