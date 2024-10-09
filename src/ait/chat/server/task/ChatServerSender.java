package ait.chat.server.task;

import ait.chat.model.Message;
import ait.mediation.BlkQueue;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatServerSender implements Runnable {
    private final BlkQueue<Message> messageBox;
    private final Set<PrintWriter> clients;

    public ChatServerSender(BlkQueue<Message> messageBox) {
        this.messageBox = messageBox;
        clients = new HashSet<>();
    }

    public synchronized boolean addClient(Socket socket) throws IOException {
        return clients.add(new PrintWriter(socket.getOutputStream(), true));
    }

    @Override
    public void run() {
        while (true) {
            Message message = messageBox.pop();
            synchronized (this) {
                Iterator<PrintWriter> iterator = clients.iterator();
                while (iterator.hasNext()) {
                    PrintWriter clientWriter = iterator.next();
                    if (clientWriter.checkError()) {
                        iterator.remove();
                    } else {
                        clientWriter.println(message);
                    }
                }
            }
        }
    }
}
