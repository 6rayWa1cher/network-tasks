package org.a6raywa1cher.network_tasks.task2_2;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task2.AbstractServer;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class MTServer extends AbstractServer {
    public static final int PORT = 25565;

    public MTServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        MTServer simpleServer = new MTServer(PORT);
        simpleServer.listen();
    }

    @Override
    protected void processSocket(Socket socket) throws IOException {
        new Thread(() -> {
            try {
                this.processRequest(socket);
            } catch (IOException e) {
                log.error("IOException", e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("IOException", e);
                }
            }
        }).start();
    }

}
