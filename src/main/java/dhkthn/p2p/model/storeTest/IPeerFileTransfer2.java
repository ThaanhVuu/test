package dhkthn.p2p.model.storeTest;

import dhkthn.p2p.model.User;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public interface IPeerFileTransfer2 {
    void start();
    void stop();
    boolean isRunning();

    void listening();
    void sendFile(File file, String host, int port) throws IOException;
    void receiveFile(Socket socket) throws IOException;
    String getMyIp();

    interface IPeerScan {
        Set<User> scanUser();
    }
}