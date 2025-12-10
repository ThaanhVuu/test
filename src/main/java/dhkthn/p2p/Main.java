package dhkthn.p2p;

import dhkthn.p2p.config.AppConfig;
import dhkthn.p2p.model.Peer;
import dhkthn.p2p.model.PeerDiscover;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {
//        Application.launch(App.class, args);
        Peer peer =Peer.builder()
                .username("vu")
                .datagramSocket(new DatagramSocket(AppConfig.getPort()))
                .pool(AppConfig.getPool())
                .port(AppConfig.getPort())
                .build();

        PeerDiscover peerDiscover = new PeerDiscover(peer);

        peer.setPeerDiscover(peerDiscover);

    }

}
