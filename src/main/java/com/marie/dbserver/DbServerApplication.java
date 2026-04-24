package com.marie.dbserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry Point - das Äquivalent zu "node server.js" in Express.
 * SpringApplication.run() startet den eingebetteten Tomcat-Server
 * und scannt alle @Controller, @Service, @Repository Klassen.
 */
@SpringBootApplication
public class DbServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbServerApplication.class, args);
    }
}
