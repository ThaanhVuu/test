package dhkthn.p2p.model;

import java.io.File;

public interface PeerListener {
    void onPeerFound(PeerInfo peer);
    void onFileReceived(File file, PeerInfo sender);
    void onError(String message);
    void onInfo(String message);
}