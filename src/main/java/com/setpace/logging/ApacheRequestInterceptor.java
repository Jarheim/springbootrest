package com.setpace.logging;

import com.google.common.base.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

import static com.setpace.logging.LogHelper.*;

public class ApacheRequestInterceptor implements HttpRequestInterceptor {
    //a comment
    private final static Logger log = LoggerFactory.getLogger(ApacheRequestInterceptor.class);

    private static String body;
    private static HttpRequest request;
    private static String url;

    private static String formatUrl(HttpRequest httpRequest) {
        final HttpHost target = ((HttpRequestWrapper) httpRequest).getTarget();
        final String portString = target.getPort() == -1 ? "" : ":" + target.getPort();
        final URI uri = ((HttpRequestWrapper) httpRequest).getURI();
        return String.format("%s://%s%s%s",
                target.getSchemeName(), target.getHostName(), portString, uri);
    }

    private void log(HttpRequest httpRequest, String requestBody) {
        body = Strings.isNullOrEmpty(requestBody)
                ? indent("REQUEST BODY IS EMPTY")
                : prettyJson(requestBody);
        url = formatUrl(httpRequest);
        request = httpRequest;

        log.info("===========================request begin===============================================");
        log.info("URI         : {}", url);
        log.info("Method      : {}", httpRequest.getRequestLine().getMethod());
        log.info("Request body: " + System.lineSeparator() + "{}", body);
        log.info("_Headers     : {}", headersToString(httpRequest.getAllHeaders()));
        log.info("===========================request end=================================================" +
                System.lineSeparator());
    }

    public static String getUrl() {
        return url;
    }

    public static String getRequestBody() {
        return body;
    }

    public static HttpRequest getHttpRequest() {
        return request;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpContext context) throws IOException {
        String requestBody = null;
        HttpRequest original = ((HttpRequestWrapper) httpRequest).getOriginal();
        if (original instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) original).getEntity();
            requestBody = IOUtils.toString(entity.getContent());
        }

        log(httpRequest, requestBody);
    }
}