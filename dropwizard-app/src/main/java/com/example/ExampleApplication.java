package com.example;

import com.example.resources.ExampleResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class ExampleApplication extends Application<ExampleConfiguration> {

    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) {
        environment.jersey().register(new ExampleResource(configuration));
    }

}