package com.ak47007;

import com.ak47007.core.EnableObjectStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableObjectStorage
public class DemoLocalObjectStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoLocalObjectStorageApplication.class, args);
    }

}
