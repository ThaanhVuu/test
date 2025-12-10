package dhkthn.p2p.config;

import dhkthn.p2p.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppConfig {
    public static ExecutorService getPool() {
        return Executors.newFixedThreadPool(5); // số thread tùy bạn
    }

    public static int getPort(){
        return 10100;
    }
}
