package org.a6raywa1cher.network_tasks.task2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    private final int port;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void listen(boolean singleRequest) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port, 150)) {
            logger.info("Started listening");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    processSocket(socket);
                } catch (IOException e) {
                    logger.error("IOException with the client", e);
                }
                if (singleRequest) break;
            }
        }
    }

    public void listen() throws IOException {
        listen(false);
    }

    protected abstract void processSocket(Socket socket) throws IOException;

    protected void processRequest(Socket socket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)) {
            logger.info("Got connection from {}", socket.getInetAddress().getHostAddress());
            String name = reader.readLine();
            printWriter.println("Hello " + name + "!");
        }
    }
}
