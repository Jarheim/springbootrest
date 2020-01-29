package com.setpace.springrest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.setpace.dezerializers.*;
import com.setpace.logging.ApacheRequestInterceptor;
import com.setpace.logging.ApacheResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class HttpConfig {

    static HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpClient client = HttpClientBuilder
                .create()
                .disableContentCompression()
                .addInterceptorFirst(new ApacheRequestInterceptor())
                .addInterceptorFirst(new ApacheResponseInterceptor())
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }

    static List<HttpMessageConverter<?>> deserializationFeature(boolean supportAllMediaTypes,
                                                                boolean failOnUnknownProps) {
        ObjectMapper objectMapper = new ObjectMapper()
                .setAnnotationIntrospector(new CustomDeserializer())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProps)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        registerSerialization(objectMapper);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        if (supportAllMediaTypes) {
            mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        }

        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();
        httpMessageConverters.add(mappingJackson2HttpMessageConverter);

        return httpMessageConverters;
    }

    private static void registerSerialization(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addSerializer(LocalTime.class, new LocalTimeSerializer());
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        module.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        module.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
        objectMapper.registerModule(module);
    }
}
