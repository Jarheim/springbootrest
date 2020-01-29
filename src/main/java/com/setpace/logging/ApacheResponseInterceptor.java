package com.setpace.logging;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.setpace.logging.LogHelper.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ApacheResponseInterceptor implements HttpResponseInterceptor {
    private final static Logger log = LoggerFactory.getLogger(ApacheResponseInterceptor.class);
    private static final String APPLICATION_JSON = "application/json";

    private static String responseBody;
    private static HttpResponse response;

    private static HttpEntity recreateHttpEntityFromByteArray(byte[] httpEntityContent, final HttpEntity entity) {
        final Header contentType = entity.getContentType();
        final String contentTypeValue
                = isNull(contentType)
                ? APPLICATION_JSON
                : contentType.getValue();

        final Header contentEncodingHeader = entity.getContentEncoding();
        final EntityBuilder entityBuilder = EntityBuilder
                .create()
                .setContentType(ContentType.parse(contentTypeValue))
                .setStream(new ByteArrayInputStream(httpEntityContent));

        if (nonNull(contentEncodingHeader)) {
            return entityBuilder
                    .setContentEncoding(String
                            .format("%s/%s", contentEncodingHeader.getName(),
                                    contentEncodingHeader.getValue()))
                    .build();
        }
        return entityBuilder.build();
    }

    private void log(HttpResponse httpResponse, String theBody) {
        responseBody = theBody.isEmpty() | theBody.contains("<html>")
                ? indent(theBody)
                : prettyJson(theBody);
        response = httpResponse;

        log.info("============================response begin==============================================");
        log.info("Status code  : {}", httpResponse.getStatusLine().getStatusCode());
        log.info("Status text  : {}", httpResponse.getStatusLine().getReasonPhrase());
        log.info("Response body: " + System.lineSeparator() + responseBody);
        log.info("_Headers      : {}", headersToString(httpResponse.getAllHeaders()));
        log.info("============================response end================================================" +
                System.lineSeparator());
    }

    public static String getResponseBody() {
        return responseBody;
    }

    public static HttpResponse getHttpResponse() {
        return response;
    }

    @Override
    public void process(final HttpResponse response, final HttpContext context) throws IOException {
        byte[] entityBytes = getBytes(response.getEntity());
        String body = new String(entityBytes, StandardCharsets.UTF_8);

        log(response, body);
        response.setEntity(recreateHttpEntityFromByteArray(entityBytes, response.getEntity()));
    }

    private byte[] getBytes(HttpEntity entity) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(entity.getContent(), outputStream);

        return outputStream.toByteArray();
    }
}