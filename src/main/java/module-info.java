module dhkthn.p2p {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Third-party libraries
    requires com.google.gson;
    requires MaterialFX;
    requires org.kordamp.bootstrapfx.core;

    // Lombok (chỉ cần compile-time)
    requires static lombok;

    // Mở package cho FXMLLoader (bắt buộc nếu dùng FXML)
    opens dhkthn.p2p to javafx.fxml;

    // Export để các class trong package này có thể được truy cập
    exports dhkthn.p2p;
}
