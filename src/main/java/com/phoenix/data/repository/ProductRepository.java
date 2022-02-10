package com.phoenix.data.repository;

import com.phoenix.data.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
     Optional<Product> findByName(String name);
     Optional<Product> findProductById(Long id);
}
