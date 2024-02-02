package lk.ijse.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.client.Client;
import lk.ijse.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class loginController {
    public AnchorPane pane;
    public TextField txtUserName;
    private Server server;

    public  static List<String> names = new ArrayList<>();
    public void initialize() {
        new Thread(() -> {
            try {
                server = Server.getInstance();
                server.makeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void btnLoginOnAction(ActionEvent event) throws IOException {
       load();
    }

    private void load() throws IOException {
        if (Pattern.matches("^[a-zA-Z\\s]+", txtUserName.getText())) {
            names.add(txtUserName.getText());

            Client client = new Client(txtUserName.getText());
            Thread thread = new Thread(client);
            thread.start();
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setTitle(txtUserName.getText() + " 's Chat Room");
            stage.show();

            txtUserName.setText(" ");
        } else{
        new Alert(Alert.AlertType.ERROR, "Valid User Name Is Required !!!").show();
    }
    }
}
