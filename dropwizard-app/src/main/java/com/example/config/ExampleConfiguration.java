package com.example.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ExampleConfiguration extends Configuration {

    @Valid
    @NotNull
    private HttpDependencyConfiguration httpDependency = new HttpDependencyConfiguration();

    @JsonProperty("httpDependency")
    public HttpDependencyConfiguration getHttpDependencyConfiguration() {
        return httpDependency;
    }

    @JsonProperty("httpDependency")
    public void setHttpDependencyConfiguration(HttpDependencyConfiguration httpDependency) {
        this.httpDependency = httpDependency;
    }
}
