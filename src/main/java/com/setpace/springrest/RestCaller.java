package com.setpace.springrest;

import com.setpace.logging.ApacheRequestInterceptor;
import com.setpace.logging.ApacheResponseInterceptor;
import lombok.Getter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.setpace.logging.LogHelper.headersToString;
import static org.assertj.core.api.Assertions.fail;

@Getter
public class RestCaller<T> {
    private TestRestTemplate testRestTemplate;
    private static String newLine = System.lineSeparator();
    public static final int basicAuthInterceptorIndex = 0;

    private RestCaller(T type) {
        this.responseType = type;
    }

    private T responseType;

    private RestCaller(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
        /*if (BeforeEachTest.basicAuth != null) {
            useBasicAuthentication(BeforeEachTest.basicAuth);
        }*/
    }

    private void useBasicAuthentication(BasicAuth basicAuth) {
        BasicAuthenticationInterceptor bai = new BasicAuthenticationInterceptor(basicAuth.getUsername(),
                basicAuth.getPassword());
        testRestTemplate.getRestTemplate().getInterceptors().add(basicAuthInterceptorIndex, bai);
    }

    private RestCaller() {
    }

    public static <E> RestCaller<E> restCaller() {
        return new RestCaller<>();
    }

    public static <E> RestCaller<E> restCaller(TestRestTemplate testRestTemplate) {
        return new RestCaller<>(testRestTemplate);
    }

    public <F> RestCaller<F> callFunction(Function<T, F> whenFunc, Class<?> clazz) {
        return callFunction(whenFunc, clazz.getName());
    }

    public <F> RestCaller<F> callFunction(Function<T, F> whenFunc, Type type) {
        return callFunction(whenFunc, type.toString());
    }

    private <F> RestCaller<F> callFunction(Function<T, F> whenFunc, String className) {
        try {
            return new RestCaller<>(whenFunc.apply(responseType));
        } catch (RestClientException e) {
            if (e.getCause() instanceof HttpMessageNotReadableException) {
                failAndLogHttpMessageNotReadableException(className, e);
            }
            throw e;
        } finally {
            if (testRestTemplate != null) {
                removeAdditionalBasicAuthInterceptor();
            }
        }
    }

    private void removeAdditionalBasicAuthInterceptor() {
        List<ClientHttpRequestInterceptor> basicAuthInterceptors = testRestTemplate.getRestTemplate()
                .getInterceptors().stream()
                .filter(x -> x instanceof BasicAuthenticationInterceptor)
                .collect(Collectors.toList());

        if (basicAuthInterceptors.size() > 1) {
            testRestTemplate.getRestTemplate().getInterceptors()
                    .remove(basicAuthInterceptors.get(basicAuthInterceptorIndex));
        }
    }

    public static void failAndLogHttpMessageNotReadableException(String className, RestClientException e) {
        String message = "The test failed because we were not able to parse the response: " + newLine;
        fail(message + ApacheResponseInterceptor.getResponseBody() + newLine + "...to the object: "
                + newLine + className + newLine + "Timestamp: " + LocalDateTime.now().toString() + newLine +
                "The response code was: " + newLine +
                ApacheResponseInterceptor.getHttpResponse().getStatusLine().getStatusCode() + newLine +
                "Headers: " + headersToString(ApacheResponseInterceptor.getHttpResponse().getAllHeaders()) + newLine +
                "The request method was : " + ApacheRequestInterceptor.getHttpRequest().getRequestLine().getMethod()
                + newLine + "The URL was: " + ApacheRequestInterceptor.getUrl() + newLine
                + "The request body was: " + ApacheRequestInterceptor.getRequestBody() + newLine +
                "Cause: " + newLine + e.getCause());
    }
}