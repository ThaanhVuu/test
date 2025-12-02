package dhkthn.p2p.model;

import dhkthn.p2p.model.interfaces.IPeerMessenger;

import java.util.List;

public class PeerMessenger implements IPeerMessenger {
    private final User user;

    public PeerMessenger(User user) {
        this.user = user;
    }

    @Override
    public void sendMessage(User to, String content) {

    }

    @Override
    public void saveMessage(Message message) {
        user.getMessages().add(message);
    }

    @Override
    public List<Message> getMessageHistory() {
        return List.of();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
