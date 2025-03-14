package org.example.a02;

import org.example.a02.model.Product;
import org.example.a02.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class A02Application {

    public static void main(String[] args) {
        SpringApplication.run(A02Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepo) {
        return args -> {
            if (productRepo.count() == 0) {
                productRepo.save(new Product("Laptop", "High-performance laptop", 999.99, 10));
                productRepo.save(new Product("Smartphone", "Latest model smartphone", 699.99, 20));
            }
        };
    }
}
