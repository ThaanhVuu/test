package dhkthn.p2p;

import dhkthn.p2p.config.AppConfig;
import dhkthn.p2p.model.PeerDiscovery;
import dhkthn.p2p.model.User;
import dhkthn.p2p.model.interfaces.IPeerDiscover;

import java.io.IOException;
import java.net.SocketException;
import java.util.Set;

public class Test {
    public static void main(String[] args) throws SocketException {

        User user = User.builder()
                .username("vu2")
                .port(5001)
                .host(User.getLocalIP())
                .build();

        AppConfig.setUser(user);

        IPeerDiscover discover = new PeerDiscovery();

        discover.start();

        System.out.println(
                "[DISCOVERY] Listening on "
                        + User.getLocalIP() + ":"
                        + AppConfig.getDISCOVERY_PORT()
        );

        //noinspection InfiniteLoopStatement
        while (true){
            try{
                discover.broadCast();

                Set<User> peers = discover.scan();

                System.out.println("["+user.getUsername()+"]" + " Found peers:");
                for (User u : peers) {
                    System.out.println(" - " + u.getUsername() + " @ " + u.getHost() + ":" + u.getPort());
                }

                //noinspection BusyWait
                Thread.sleep(5000);
            }catch (IOException | InterruptedException e){
                throw new RuntimeException(e.getMessage());
            }
        }

    }
}
