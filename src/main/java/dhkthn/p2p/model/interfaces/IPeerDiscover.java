package dhkthn.p2p.model.interfaces;

import dhkthn.p2p.model.User;

import java.io.IOException;
import java.util.Set;

public interface IPeerDiscover extends IPeer{
    void listening() throws IOException;
    Set<User> scan() throws IOException;
    void broadCast() throws IOException;
}
