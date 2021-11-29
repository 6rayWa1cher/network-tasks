package org.a6raywa1cher.network_tasks.task2;

import lombok.extern.slf4j.Slf4j;
import org.a6raywa1cher.network_tasks.task1.AbstractTextRequest;
import org.a6raywa1cher.network_tasks.task1.TextRequest;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.a6raywa1cher.network_tasks.Utils.generateRandomString;

@Slf4j
public class SimpleClient extends AbstractTextRequest<String> {
    public SimpleClient(String hostname, int port) {
        super(hostname, port);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        TextRequest<String> textRequest = new SimpleClient(null, Server.DEFAULT_PORT);
        log.info(textRequest.call());
    }

    @Override
    protected String constructRequest() {
        return generateRandomString() + "\n";
    }

    @Override
    protected String convertResponse(String convertedInput) {
        return convertedInput;
    }
}
