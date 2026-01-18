package org.simor.adapter.client;

import org.simor.entity.TranslatedContent;
import org.simor.entity.TranslatedDescription;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

public class TranslationRestClient implements TranslationClient {

    private final RestClient translationRestClient;

    public TranslationRestClient(String baseUrl, String path) {
        translationRestClient = RestClient.builder().baseUrl(String.format("%s/%s", baseUrl, path)).build();
    }

    // Assuming description is not empty
    @Override
    public String getTranslation(String description) {
        try {
            MultiValueMap<String, String> formEncodedBody = new LinkedMultiValueMap<>();
            formEncodedBody.add("text", description);
            TranslatedDescription translatedDescription = translationRestClient.post()
                    .body(formEncodedBody)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .body(TranslatedDescription.class);
            return Optional.ofNullable(translatedDescription)
                    .map(TranslatedDescription::contents)
                    .map(TranslatedContent::translated)
                    .orElseThrow(
                            //TODO Is bad gateway correct when there isnt a translation?
                            () -> new TranslationRestClientException(HttpStatus.BAD_GATEWAY,
                                    "Unable to map response"));
        } catch (RestClientResponseException ex) {
            // exception thrown by ResponseSpec when status code >= 400
            throw new TranslationRestClientException(ex.getStatusCode(), ex.getMessage());
        } catch (RestClientException ex) {
            throw new TranslationRestClientException(HttpStatus.BAD_GATEWAY, "Unexpected error occurred");
        }
    }
}
