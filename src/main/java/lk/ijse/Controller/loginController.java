package lk.ijse.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class loginController {
    public AnchorPane root;
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
        if(!txtUserName.getText().isEmpty()) {

            names.add(txtUserName.getText());

            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/View/ClientChatForm.fxml"))));


            stage.centerOnScreen();
            stage.setTitle(txtUserName.getText() + " 's Chat Room");
            stage.setOnCloseRequest(windowEvent -> {
                for (String nm:names) {
                    if(stage.getTitle().startsWith(nm)) {
                        names.remove(nm);
                        System.out.println(nm + " left from the chat !!!");
                        return;
                    }
                }
            });
            stage.show();

            txtUserName.setText(" ");


        }else{
            new Alert(Alert.AlertType.ERROR, "User Name Is Required !!!").show();
        }
    }
}
