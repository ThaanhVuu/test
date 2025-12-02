package dhkthn.p2p.model;

import dhkthn.p2p.config.AppConfig;
import dhkthn.p2p.model.interfaces.IUser;
import lombok.*;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class User implements Serializable, IUser {
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    private String username;
    private String password;
    private List<Message> messages;
    private int port;
    private String host;

    @Override
    public boolean matches(String rawPassword){
        return this.password.equals(AppConfig.hashPassword(rawPassword));
    }

    @Override
    public void saveUser() throws IOException, ClassNotFoundException {
        Set<User> users = getUsers();
        if(users.contains(this)) throw new RuntimeException("User existed");
        users.add(this);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConfig.getPATH_SAVE_USER()));
        oos.writeObject(users);

        oos.close();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    @Override
    public Set<User> getUsers() throws IOException, ClassNotFoundException {
        Set<User> users = new HashSet<>();
        File f = new File(AppConfig.getPATH_SAVE_USER());
        if (!f.exists()) {
            f.createNewFile();
            return users;
        }

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConfig.getPATH_SAVE_USER()));
        Object userSet = ois.readObject();
        if (userSet instanceof Set)
            users = (Set<User>) userSet;

        ois.close();

        return users;
    }

    @Override
    public User authenticate(String username, String rawPassword) throws IOException, ClassNotFoundException {
            Set<User> users = getUsers();
            for (User u : users){
                if(u.getUsername().equals(username)){
                    return u.matches(rawPassword) ? u : null;
                }
            }
        return null;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof User u)) return false;
        return this.username != null && this.username.equalsIgnoreCase(u.username);
    }

    @Override
    public int hashCode(){
        return username == null ? 0 : username.toLowerCase().hashCode();
    }

    public static String getLocalIP() {
        try {
            var interfaces = java.net.NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                var ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                var addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    var addr = addrs.nextElement();

                    // skip IPv6 và loopback
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}

        return "127.0.0.1"; // fallback
    }

}
