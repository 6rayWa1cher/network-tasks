package org.a6raywa1cher.network_tasks.task2_3;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task2.AbstractServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class FJPServer extends AbstractServer {
    public static final int PORT = 25565;

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    public FJPServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        FJPServer simpleServer = new FJPServer(PORT);
        simpleServer.listen();
    }

    @Override
    protected void processSocket(Socket socket) throws IOException {
        forkJoinPool.submit(() -> {
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
        });
    }
}
