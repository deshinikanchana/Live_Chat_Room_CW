package lk.ijse.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.Controller.chatRoomController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class Client implements Runnable, Serializable {
    private final String name;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private chatRoomController chatRoom;

    public Client(String name) throws IOException {
        this.name = name;

        socket = new Socket("localhost", 3001);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        outputStream.writeUTF(name);
        outputStream.flush();

        try {
            loadScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void run() {
        String message = "";
        while (!message.equals("exit")) {
            try {
                message = inputStream.readUTF();
                if (message.equals("*image*")) {
                    receiveImage();
                } else {
                    chatRoom.writeMessage(message);
                }

            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        outputStream.writeUTF(msg);
        outputStream.flush();
        System.out.println(msg);
    }

    public void sendImage(byte[] bytes) throws IOException {
        outputStream.writeUTF("*image*");
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void loadScene() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ClientChatForm.fxml"));
        Parent parent = loader.load();
        chatRoom = loader.getController();
        chatRoom.setClient(this);
        stage.setResizable(false);
        stage.setScene(new Scene(parent));
        stage.setTitle(name + "'s Chat");
        stage.show();
        System.out.println(name + " joined !");

        stage.setOnCloseRequest(event -> {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
                System.out.println(name + "left !");
            } catch (IOException e) {
                System.out.println(e);
            }
        });

    }

    public String getName() {
        return name;
    }

    private void receiveImage() throws IOException {
        String utf = inputStream.readUTF();
        int size = inputStream.readInt();
        byte[] bytes = new byte[size];
        inputStream.readFully(bytes);
        System.out.println(name + "- Image received: from " + utf);
        chatRoom.setImage(bytes, utf);
    }
}
