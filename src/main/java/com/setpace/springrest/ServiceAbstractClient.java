package com.setpace.springrest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.setpace.springrest.ParamType.getMapTypeRef;
import static com.setpace.springrest.ParamType.getParamTypeRef;
import static com.setpace.springrest.RestCaller.restCaller;

@Slf4j
public abstract class ServiceAbstractClient extends UriController {

    @Autowired
    private TestRestTemplate restTemplate;

    @Qualifier("testRestTemplateSupportingAllMediaTypes")
    @Autowired
    protected TestRestTemplate templateSupportingAllMediaTypes;

    protected void setRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected void useBasicAuthentication(String username, String password) {
        BasicAuthenticationInterceptor bai = new BasicAuthenticationInterceptor(username, password);
        restTemplate.getRestTemplate().getInterceptors().add(RestCaller.basicAuthInterceptorIndex, bai);
    }

    protected <T> ResponseEntity<T> postForEntity(URI url, Object request,
                                                  Class<T> responseType) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate.postForEntity(url, request, responseType), responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<T> delete(URI uri, Class<T> type) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate.exchange(uri, HttpMethod.DELETE, null, type), type)
                .getResponseType();
    }

    protected <T> ResponseEntity<T> delete(URI uri, Class<T> type, HttpEntity httpEntity) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, type), type)
                .getResponseType();
    }

    protected  <T> ResponseEntity<T> getForEntity(URI uri, Class<T> responseType) throws RestClientException {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate.getForEntity(uri, responseType), responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<T> put(URI uri, Object requestObject, Class<T> responseType) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(requestObject), responseType), responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> putForList(URI uri, Object requestObject, Class<T> responseType) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.PUT,
                                        new HttpEntity<>(requestObject),
                                        getParamTypeRef(responseType)),
                        responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> deleteForList(URI uri, Class<T> responseType,
                                                        Object requestObject) {

        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.DELETE,
                                        new HttpEntity<>(requestObject),
                                        getParamTypeRef(responseType)),
                        responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> postForList(URI uri, Object requestObject, Class<T> responseType) {
        return postForList(uri.toString(), responseType, requestObject);
    }

    protected <T> ResponseEntity<List<T>> postForList(String uri, Class<T> responseType, Object requestObject) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.POST,
                                        new HttpEntity<>(requestObject),
                                        getParamTypeRef(responseType)),
                        responseType)
                .getResponseType();
    }

    protected <K, V> ResponseEntity<Map<K, V>> postForMap(URI uri, Object requestObject, Class<K> responseKeyType, Class<V> responseValueType) {
        ParameterizedTypeReference<Map<K, V>> responseType = getMapTypeRef(responseKeyType, responseValueType);
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.POST,
                                        new HttpEntity<>(requestObject),
                                        responseType),
                        responseType.getType())
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> postForList(String uri, Class<T> responseType, HttpEntity<?> httpEntity) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.POST,
                                        httpEntity,
                                        getParamTypeRef(responseType)),
                        responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> getForList(URI url, Class<T> responseType) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(url,
                                        HttpMethod.GET,
                                        null,
                                        getParamTypeRef(responseType)),
                        responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> getForList(String url, Class<T> responseType, Object... urlVariables) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(url,
                                        HttpMethod.GET,
                                        null,
                                        getParamTypeRef(responseType),
                                        urlVariables),
                        responseType)
                .getResponseType();
    }

    protected <T> ResponseEntity<List<T>> getForList(String url, Class<T> responseType, HttpEntity<?> httpEntity,
                                                     Object... urlVariables) {
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(url,
                                        HttpMethod.GET,
                                        httpEntity,
                                        getParamTypeRef(responseType),
                                        urlVariables),
                        responseType)
                .getResponseType();
    }

    protected <K, V> ResponseEntity<Map<K, V>> getForMap(URI uri, Class<K> responseKeyType, Class<V> responseValueType) {
        return getForMap(uri, null, responseKeyType, responseValueType);
    }

    protected <K, V> ResponseEntity<Map<K, V>> getForMap(URI uri, Object requestObject, Class<K> responseKeyType, Class<V> responseValueType) {
        ParameterizedTypeReference<Map<K, V>> responseType = getMapTypeRef(responseKeyType, responseValueType);
        return restCaller(restTemplate)
                .callFunction(x -> restTemplate
                                .exchange(uri,
                                        HttpMethod.GET,
                                        new HttpEntity<>(requestObject),
                                        responseType),
                        responseType.getType())
                .getResponseType();
    }
}