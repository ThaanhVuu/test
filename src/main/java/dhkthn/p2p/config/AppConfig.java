package dhkthn.p2p.config;

import dhkthn.p2p.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppConfig {
    @Getter
    private static final String PATH_SAVE_USER = "users.dat";

    @Getter
    private static final String PATH_SAVE_FILE = "received";

    @Getter @Setter
    private static User user;

    @Getter
    private static final int DISCOVERY_PORT = 8888;

    @Getter
    private static final int TIME_OUT = 5000;

    public static InetAddress getBroadcastAddress() throws IOException {
        return InetAddress.getByName("255.255.255.255");
    }

    @Getter
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
