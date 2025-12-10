package dhkthn.p2p;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Objects;

public class App extends Application {

    private static Scene scene;

    public static void setRoot(String fxml) {
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(fxml)));
            scene.setRoot(root);
        }catch (Exception e){
            System.out.println("Lỗi khi chuyển màn hình");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sign-in.fxml"));

        scene = new Scene(loader.load(), 1000, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setTitle("Sign In");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}