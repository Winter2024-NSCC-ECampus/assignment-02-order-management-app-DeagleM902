package org.example.a02.repository;

import org.example.a02.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //Fill as needed
}
