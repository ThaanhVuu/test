package dhkthn.p2p.model.interfaces;

import dhkthn.p2p.model.Message;
import dhkthn.p2p.model.User;

import java.util.List;

public interface IPeerMessenger extends IPeer {
    void sendMessage(User to, String content);
    void saveMessage();
    List<Message> getMessageHistory();
}
