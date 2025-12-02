package dhkthn.p2p.model.interfaces;

import dhkthn.p2p.model.User;

import java.io.IOException;
import java.util.Set;

public interface IUser {
    boolean matches(String rawPassword);
    void saveUser() throws IOException, ClassNotFoundException;
    Set<User> getUsers() throws IOException, ClassNotFoundException;
    User authenticate(String username, String rawPassword) throws IOException, ClassNotFoundException;
}
