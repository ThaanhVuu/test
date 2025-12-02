package dhkthn.p2p.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    private MessageType messageType;
    private LocalDateTime timestamp;
    private String content;
    @Builder.Default
    private boolean myMessage = true;
}
