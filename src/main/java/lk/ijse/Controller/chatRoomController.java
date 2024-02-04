package lk.ijse.Controller;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.event.ActionEvent;
import lk.ijse.sinhala.translationLogic;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static lk.ijse.Controller.loginController.names;


public class chatRoomController {
    private final String[] emojis = {
            "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE05",
            "\uD83D\uDE06", "\uD83D\uDE07", "\uD83D\uDE08", "\uD83D\uDE09", "\uD83D\uDE0A", "\uD83D\uDE0B",
            "\uD83D\uDE0C", "\uD83D\uDE0D", "\uD83D\uDE0E", "\uD83D\uDE0F", "\uD83D\uDE10", "\uD83D\uDE11",
            "\uD83D\uDE12", "\uD83D\uDE13"
    };

    public TextField txtMsg;
    public VBox vBox;
    public AnchorPane emojiAnchorpane;
    public GridPane emojiGridpane;
    public Label lblName;
    public javafx.scene.control.ScrollPane scrollPane;
    public CheckBox sinhalaSelectBox;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName ;
    public static String sendBy;



    public void initialize(){
        clientName = names.get(names.size() - 1);
        lblName.setText(clientName);

        emojiAnchorpane.setVisible(false);
        int buttonIndex = 0;
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                if (buttonIndex < emojis.length) {
                    String emoji = emojis[buttonIndex];
                    JFXButton emojiButton = createEmojiButton(emoji);
                    emojiGridpane.add(emojiButton, column, row);
                    buttonIndex++;
                } else {
                    break;
                }
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost", 3030);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Client connected");
                    System.out.println(clientName+" joined.");

                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receivingMsg(receivingMsg, chatRoomController.this.vBox);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private JFXButton createEmojiButton(String emoji) {
        JFXButton button = new JFXButton(emoji);
        button.getStyleClass().add("emoji-button");
        button.setOnAction(this::emojibtnonAction);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setFillWidth(button, true);
        GridPane.setFillHeight(button, true);
        button.setStyle("-fx-font-size: 15; -fx-text-fill: black; -fx-background-color: #F0F0F0; -fx-border-radius: 50");
        return button;
    }

    private void emojibtnonAction(ActionEvent event) {
        JFXButton button = (JFXButton) event.getSource();
        txtMsg.appendText(button.getText());
    }

    private void receivingMsg(String receivingMsg, VBox msgVbox) {
        if (receivingMsg.matches(".*\\.(png|jpe?g|gif)$")){

            Label Sender = new Label(sendBy);
            File imageFile = new File(receivingMsg);
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5, 5, 5, 10));
            hBox.getChildren().addAll(Sender,imageView);
            Platform.runLater(() -> {
                msgVbox.getChildren().add(hBox);

            });

        }else {
            String name = receivingMsg.split(":")[0];
            String msgFromServer = receivingMsg.split(":")[1];

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(name);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            Text text = new Text(msgFromServer);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #42B98D; -fx-font-weight: bold; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0,0,0));

            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    msgVbox.getChildren().add(hBoxName);
                    msgVbox.getChildren().add(hBox);
                }
            });
        }
    }

    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if(!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 0, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 14");
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-background-color: #008080; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);


                try {
                    dataOutputStream.writeUTF(clientName + ":" + msgToSend);
                    dataOutputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            txtMsg.clear();
        }
    }


    @FXML
    private void btnSendOnAction(ActionEvent event) {
        if(!sinhalaSelectBox.isSelected()) {
            sendMsg(txtMsg.getText());
        }else {
            onActionSelectBox(new ActionEvent());
        }

        emojiAnchorpane.setVisible(false);
    }

    @FXML
    void attachFileOnAction(ActionEvent actionEvent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Open");
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            sendImage(fileToOpen.getPath(),lblName.getText());
            System.out.println(fileToOpen.getPath() + " chosen.");
        }

    }

    private void sendImage(String file,String sender) {
        sendBy = sender;
        File imageFile = new File(file);
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();

        hBox.setPadding(new Insets(5, 5, 5, 10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(file);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnImojiOnAction() {emojiAnchorpane.setVisible(true);
    }

    public void onActionSelectBox(ActionEvent event) {
        if(txtMsg.getText().matches("^[a-zA-Z ]*$")){
            if(sinhalaSelectBox.isSelected()) {
                translateMsg(txtMsg.getText());
            }

        }else{
            new Alert(Alert.AlertType.ERROR, "Your Message Is Not Valid Text !!!").show();
        }

    }

    private void translateMsg(String text) {
        translationLogic tr = new translationLogic();
        sendMsg(tr.convertText(text));
    }

    public void txtmsgOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }
}