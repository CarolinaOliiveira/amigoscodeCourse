package com.carolinacode.customer;

import org.springframework.data.jpa.repository.JpaRepository;


//nesta interface só é necessario testar os metodos que fomos nós a adicionar, os restantes métodos sao da responsabilidade do JpaRepository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsCostumerByEmail(String email);
}
