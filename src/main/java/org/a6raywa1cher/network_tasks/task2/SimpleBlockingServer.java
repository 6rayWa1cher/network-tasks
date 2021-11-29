package org.a6raywa1cher.network_tasks.task2;

import java.io.IOException;
import java.net.Socket;

public class SimpleBlockingServer extends AbstractBlockingServer {
    public SimpleBlockingServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        SimpleBlockingServer simpleServer = new SimpleBlockingServer(Server.DEFAULT_PORT);
        simpleServer.listen();
    }

    @Override
    protected void processSocket(Socket socket) throws IOException {
        this.processRequest(socket);
    }
}
