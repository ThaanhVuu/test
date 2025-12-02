package dhkthn.p2p.service.message;

import dhkthn.p2p.model.storeTest.IPeerFileTransfer2;
import lombok.*;

import java.io.File;

@Getter @Setter @RequiredArgsConstructor
@Builder
public class FileSender implements IFile{
    private final IPeerFileTransfer2 peer;

    @Override
    public void sendFile(File file, String host, int port) {
//        peer.sendFile(file, host, port);
    }

    @Override
    public void receiveFile() {

    }

    @Override
    public void sendMessageSendFile() {

    }
}
