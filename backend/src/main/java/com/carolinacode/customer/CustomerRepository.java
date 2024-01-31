package com.carolinacode.customer;

import io.jsonwebtoken.security.Jwks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


//nesta interface só é necessario testar os metodos que fomos nós a adicionar, os restantes métodos sao da responsabilidade do JpaRepository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsCostumerByEmail(String email);
    Optional<Customer> findCustomerByEmail(String email);
}
