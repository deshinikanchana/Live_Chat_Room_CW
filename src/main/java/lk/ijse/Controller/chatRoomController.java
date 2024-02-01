package lk.ijse.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lk.ijse.client.ClientHandler;
import lk.ijse.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static lk.ijse.Controller.loginController.names;
import static lk.ijse.client.ClientHandler.clients;

public class chatRoomController {
    public TextArea txtArea;
    public TextField txtmsg;
    public Label lblName;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName = "Client";

    public void initialize(){

            clientName = names.get(names.size()-1);
            lblName.setText(clientName);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        socket = new Socket("localhost", 3001);
                        dataInputStream = new DataInputStream(socket.getInputStream());
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        System.out.println(clientName+" joined.");

                        while (socket.isConnected()){
                            String receivingMsg = dataInputStream.readUTF();
                            String name = receivingMsg.split("-")[0];
                            receiveMessage(name + " : " + receivingMsg.split("-")[1], chatRoomController.this.txtArea);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();

    }

    public void receiveMessage(String msg, TextArea txtArea) throws IOException {
        Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtArea.appendText( "\n" + msg);
                }
            });
    }
    public void btnSendOnAction(ActionEvent event) {
            sendMsg(txtmsg.getText());
    }

    private void sendMsg(String text) {
        if (!text.isEmpty()){
            if (!text.matches(".*\\.(png|jpe?g|gif)$")){
                try {
                    dataOutputStream.writeUTF(clientName + "-" + text);
                    dataOutputStream.flush();
                    txtArea.appendText("\n" + text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtmsg.setText(" ");
            }
        }
    }

    public void btnCamOnAction(ActionEvent event) {
    }
}
