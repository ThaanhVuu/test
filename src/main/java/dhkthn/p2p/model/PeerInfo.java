package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerInfo implements Serializable {
    private String username;
    private String host; // IP Address
    private int port;    // Port dùng để truyền file (TCP)
}