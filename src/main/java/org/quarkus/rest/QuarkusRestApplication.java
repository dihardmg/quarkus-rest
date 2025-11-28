package org.quarkus.rest;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class QuarkusRestApplication implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Starting Quarkus REST Application...");
        Quarkus.waitForExit();
        return 0;
    }
}