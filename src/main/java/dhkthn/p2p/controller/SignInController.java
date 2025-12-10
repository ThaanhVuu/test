package dhkthn.p2p.controller;

import dhkthn.p2p.App;
import dhkthn.p2p.config.SecurityConfig;
import dhkthn.p2p.model.User;
import dhkthn.p2p.repository.UserRepo;
import dhkthn.p2p.util.Toast;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

public class SignInController {
    private final UserRepo userRepo;

    public SignInController() throws SQLException {
        this.userRepo = new UserRepo();
    }

    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    public void handleSignUpClicked(){
        App.setRoot("register.fxml");
    }

    @FXML
    private void handleSignInButton(ActionEvent event){
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.show(currentStage, "Vui lòng nhập đủ tên người dùng và mật khẩu.", Duration.seconds(3));
            return;
        }
        User user = this.userRepo.getUserByUsername(username);

        if(user == null || !SecurityConfig.matches(password, user.getPassword())){
            Toast.show(currentStage, "Thông tin đăng nhập không chính xác", Duration.seconds(3));
            return;
        }

        App.setRoot("home.fxml");
    }
}
