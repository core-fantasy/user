package com.corefantasy.user;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        try {
            Micronaut.run(Application.class);
        }
        catch (Throwable t) {
            System.err.println(t.getMessage());
            throw t;
        }
    }
}