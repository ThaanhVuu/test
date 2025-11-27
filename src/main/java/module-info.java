module dhkthn.p2p {
    requires com.google.gson;
    requires MaterialFX;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;

    opens dhkthn.p2p to javafx.fxml;
    exports dhkthn.p2p;
}