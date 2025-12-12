package dhkthn.p2p.config;

import dhkthn.p2p.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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

    public static String getLocalIpAddress() {
        try {
            // Duyệt qua tất cả các giao diện mạng (Wi-Fi, Ethernet, Loopback,...)
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = netInterfaces.nextElement();

                // Lọc các giao diện không hợp lệ:
                // 1. Giao diện đang tắt (isUp())
                // 2. Giao diện Loopback (127.0.0.1)
                // 3. Giao diện ảo (Virtual/VPN)
                if (!netInterface.isUp() || netInterface.isLoopback() || netInterface.isVirtual()) {
                    continue;
                }

                // Duyệt qua tất cả địa chỉ IP gán cho giao diện đó
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Lọc để chỉ lấy IPv4 (InstanceOf Inet4Address) và không phải địa chỉ Localhost
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        // Trả về địa chỉ IP thực tế (Ví dụ: 192.168.1.5)
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Lỗi khi lấy giao diện mạng: " + e.getMessage());
        }

        // Trường hợp không tìm thấy IP hợp lệ (Ví dụ: không kết nối mạng)
        return null;
    }
}
