package org.a6raywa1cher.network_tasks.task1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        TextRequest<String> httpTextRequest = new GetHttpTextRequest("http://pmk.tversu.ru");
        File file = new File("output.html");
        try (PrintWriter printWriter = new PrintWriter(file)) {
            String call = httpTextRequest.call();
            printWriter.print(call);
        }
    }
}