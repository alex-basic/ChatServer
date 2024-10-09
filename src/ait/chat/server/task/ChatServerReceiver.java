package ait.chat.server.task;

import ait.chat.model.Message;
import ait.mediation.BlkQueue;

import java.io.*;
import java.net.Socket;

public class ChatServerReceiver implements Runnable {
    private final Socket socket;
    private final BlkQueue<Message> messageBox;

    public ChatServerReceiver(Socket socket, BlkQueue<Message> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket) {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message message = (Message) ois.readObject();
                if (message == null) {
                    System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ", closed");
                    break;
                }
                messageBox.push(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
