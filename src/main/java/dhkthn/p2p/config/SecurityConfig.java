package dhkthn.p2p.config;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityConfig {
    public static String encode(String rawPassword){
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String hasedPassword) {
        return BCrypt.checkpw(rawPassword, hasedPassword);
    }
}
