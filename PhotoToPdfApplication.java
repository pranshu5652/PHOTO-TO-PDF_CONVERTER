package com.pranshu.phototopdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhotoToPdfApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoToPdfApplication.class, args);
        System.out.println("Photo-to-PDF app chal raha hai -> http://localhost:8080");
    }
}
