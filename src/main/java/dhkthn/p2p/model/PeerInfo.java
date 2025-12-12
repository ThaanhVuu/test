package dhkthn.p2p.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerInfo implements Serializable {
    private String username;
    private String host; // IP Address
    private int port;    // Port dùng để truyền file (TCP)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerInfo peerInfo = (PeerInfo) o;
        // 2 Peer coi là giống nhau nếu cùng username và IP (hoặc tùy logic của bạn)
        return port == peerInfo.port &&
                Objects.equals(username, peerInfo.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, host, port);
    }

    @Override
    public String toString() {
        return this.username + " | " + "[" + this.getHost() + "]" + ":[" + this.getPort() + "]";
    }
}