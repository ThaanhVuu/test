package dhkthn.p2p.model;

import dhkthn.p2p.config.AppConfig;
import lombok.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Peer {
    private UUID uuid;
    private String username;
    private String host;
    private boolean running;
    private int port;
    private int filePort;
    private long lastSeen;
    @Builder.Default
    private ExecutorService pool = AppConfig.getExecutor();

}
