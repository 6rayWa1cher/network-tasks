package org.a6raywa1cher.network_tasks.task1;

import lombok.SneakyThrows;
import org.a6raywa1cher.network_tasks.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class GetHttpTextRequest extends AbstractTextRequest<String> {
    private static final String END_LINE = "\r\n";

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0";

    private final URL url;

    public GetHttpTextRequest(String url) throws MalformedURLException {
        super(url);
        this.url = new URL(url);
        this.port = 80;
    }

    @Override
    @SneakyThrows
    protected String constructRequest() {
        return "GET " + Utils.urlToResource(url) + ' ' + "HTTP/1.1" + END_LINE +
                "Host: " + url.getHost() + END_LINE +
                "User-Agent: " + USER_AGENT + END_LINE +
                "Accept: */*" + END_LINE +
                END_LINE;
    }

    @Override
    protected String convertResponse(String convertedInput) {
        return convertedInput;
    }
}
