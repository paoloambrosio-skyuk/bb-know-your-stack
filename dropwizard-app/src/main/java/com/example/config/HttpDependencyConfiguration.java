package com.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.HttpClientConfiguration;

import javax.validation.constraints.NotNull;

public class HttpDependencyConfiguration extends HttpClientConfiguration {

    @NotNull
    private String url;


    @JsonProperty
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public void setUrl(String url) {
        this.url = url;
    }
}
