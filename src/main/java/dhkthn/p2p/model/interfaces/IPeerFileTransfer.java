package dhkthn.p2p.model.interfaces;

import dhkthn.p2p.model.User;

import java.io.File;

public interface IPeerFileTransfer extends IPeer{
    void sendFile(User to, File file);
}
