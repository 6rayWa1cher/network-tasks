package org.a6raywa1cher.network_tasks.task1_extended;

import org.a6raywa1cher.network_tasks.task1.TextRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        TextRequest httpTextRequest = new HttpTextRequest("http://pmk.tversu.ru");
        File file = new File("output.html");
        try (PrintWriter printWriter = new PrintWriter(file)) {
            HttpResponse call = (HttpResponse) httpTextRequest.call();
            printWriter.print(call.body());
        }
    }
}