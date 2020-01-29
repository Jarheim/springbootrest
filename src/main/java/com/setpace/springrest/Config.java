package com.setpace.springrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;

import java.time.Duration;

import static com.setpace.springrest.HttpConfig.clientHttpRequestFactory;
import static com.setpace.springrest.HttpConfig.deserializationFeature;

@Configuration
@ComponentScan(basePackages = "com.setpace")
public class Config {

    @Value("${spring.username}")
    private String username;

    @Value("${spring.password}")
    private String password;

    @Bean
    public TestRestTemplate testRestTemplate() {
        TestRestTemplate template = setupTestRestTemplate(false, false);
        setBasicAuth(template);
        return template;
    }

    private TestRestTemplate setupTestRestTemplate(boolean supportAllMediaTypes, boolean failOnUnknownProps) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .messageConverters(deserializationFeature(supportAllMediaTypes, failOnUnknownProps))
                .setReadTimeout(Duration.ofSeconds(30));

        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder);
        testRestTemplate.getRestTemplate().setRequestFactory(clientHttpRequestFactory());

        return testRestTemplate;
    }

    private void setBasicAuth(TestRestTemplate testRestTemplate) {
        BasicAuthenticationInterceptor bai = new BasicAuthenticationInterceptor(username, password);
        testRestTemplate.getRestTemplate().getInterceptors().add(bai);
    }
}
