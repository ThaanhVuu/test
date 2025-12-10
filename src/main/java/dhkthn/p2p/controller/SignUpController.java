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
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

@SuppressWarnings("ALL")
public class SignUpController {
    private final UserRepo userRepo;

    public SignUpController() throws SQLException {
        this.userRepo = new UserRepo();
    }

    @FXML
    private MFXTextField usernameField;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private MFXPasswordField passwordField2;

    @FXML
    private void handleSignUpBtn(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String password2 = passwordField2.getText();

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (username.trim().isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.show(currentStage, "Vui lòng điền đầy đủ thông tin.", Duration.seconds(3));
            return;
        }

        if(!password2.equals(password)){
            Toast.show(currentStage, "Mật khẩu không khớp", Duration.seconds(3));
            return;
        }

        if(userRepo.checkUserExist(username)){
            Toast.show(currentStage, "Tài khoản đã tồn tại", Duration.seconds(3));
            return;
        }

        userRepo.save(User.builder()
                        .username(username)
                        .password(SecurityConfig.encode(password))
                .build());

        Toast.show(currentStage, "Đăng kí thành công", Duration.seconds(3));

        App.setRoot("sign-in.fxml");
    }

    @FXML
    private void handleSignInClicked(InputEvent e) throws IOException {
        App.setRoot("sign-in.fxml");
    }
}
