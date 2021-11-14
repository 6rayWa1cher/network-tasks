package org.a6raywa1cher.network_tasks.task2;

import java.io.IOException;
import java.net.Socket;

public class SimpleServer extends AbstractServer {
    public static final int PORT = 25565;

    public SimpleServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        SimpleServer simpleServer = new SimpleServer(PORT);
        simpleServer.listen();
    }

    @Override
    protected void processSocket(Socket socket) throws IOException {
        this.processRequest(socket);
    }
}
