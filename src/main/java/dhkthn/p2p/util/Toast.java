package dhkthn.p2p.util;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {

    // Thời gian hiển thị mặc định: 5 giây
    private static final Duration DEFAULT_DURATION = Duration.seconds(5);

    /**
     * Hiển thị thông báo Toast trên Stage chính.
     *
     * @param ownerStage Stage cha mà Toast sẽ xuất hiện trên đó
     * @param message    Nội dung thông báo
     */
    public static void show(Stage ownerStage, String message) {
        show(ownerStage, message, DEFAULT_DURATION);
    }

    public static void show(Stage ownerStage, String message, Duration duration) {

        // 1. Tạo Popup (Giống như một cửa sổ nhỏ không có viền)
        Popup popup = new Popup();
        popup.setAutoHide(true); // Cho phép tự động ẩn khi click ra ngoài

        // 2. Thiết lập nội dung (dùng Label và StackPane)
        Label messageLabel = new Label(message);

        // Thiết lập Style (CSS) cho đẹp mắt hơn
        messageLabel.setStyle(
                "-fx-padding: 10px 20px; " +
                        "-fx-background-color: rgba(0, 0, 0, 0.8); " + // Nền đen trong suốt
                        "-fx-text-fill: white; " +                      // Chữ trắng
                        "-fx-background-radius: 5; " +
                        "-fx-font-size: 14px;"
        );

        StackPane pane = new StackPane(messageLabel);
        pane.setStyle("-fx-background-color: transparent;"); // Quan trọng để nền popup trong suốt

        popup.getContent().add(pane);

        // 3. Thiết lập hẹn giờ (Timer)
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(e -> popup.hide()); // Khi hết giờ thì đóng popup

        // 4. Tính toán vị trí hiển thị (Ví dụ: đặt ở giữa dưới cùng)
        double centerX = ownerStage.getX() + ownerStage.getWidth() - 270;
        double bottomY = ownerStage.getY() + 50;

        popup.setX(centerX - pane.prefWidth(-1) / 2); // Căn giữa theo chiều ngang
        popup.setY(bottomY);

        popup.show(ownerStage);
        delay.play();
    }
}
