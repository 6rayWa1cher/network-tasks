import org.a6raywa1cher.network_tasks.task1.TextRequest;
import org.a6raywa1cher.network_tasks.task2.Server;
import org.a6raywa1cher.network_tasks.task2.SimpleBlockingServer;
import org.a6raywa1cher.network_tasks.task2.SimpleClient;
import org.a6raywa1cher.network_tasks.task2_2.MTServer;
import org.a6raywa1cher.network_tasks.task2_3.FJPServer;
import org.a6raywa1cher.network_tasks.task3.NIOServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestServers {
    int port = 25566;

    private void testServer(Server server) throws InterruptedException {
        Thread serverThread = new Thread(() -> {
            try {
                server.listen(true);
            } catch (IOException e) {
                Assertions.fail(e);
            }
        });
        serverThread.start();
        Thread.sleep(100);
        String[] actualAnswer = new String[1];
        Thread clientThread = new Thread(() -> {
            TextRequest<String> textRequest = new SimpleClient(null, port) {
                @Override
                protected String constructRequest() {
                    return "cat\n";
                }
            };
            try {
                actualAnswer[0] = textRequest.call();
            } catch (IOException e) {
                Assertions.fail(e);
            }
        });
        clientThread.start();
        clientThread.join(5000);
        serverThread.join(5000);
        Assertions.assertEquals("Hello cat!\n", actualAnswer[0]);
    }

    @Test
    void testSimpleServer() throws InterruptedException {
        testServer(new SimpleBlockingServer(port));
    }

    @Test
    void testMTServer() throws InterruptedException {
        testServer(new MTServer(port));
    }

    @Test
    void testFJPServer() throws InterruptedException {
        testServer(new FJPServer(port));
    }

    @Test
    void testNIOServer() throws InterruptedException {
        testServer(new NIOServer(port));
    }
}
