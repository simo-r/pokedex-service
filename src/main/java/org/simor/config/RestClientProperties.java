package org.simor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rest-client")
@Getter
@Setter
public class RestClientProperties {

    private RestClient pokemon;
    private RestClient shakespeareTranslation;
    private RestClient yodaTranslation;

    @Getter
    @Setter
    public static class RestClient {
        private String baseUrl;
        private String path;
    }
}

