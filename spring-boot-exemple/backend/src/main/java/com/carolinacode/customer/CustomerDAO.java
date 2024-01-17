package com.carolinacode.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    boolean existsPersonWitId(Integer id);
    void deleteCustomerById(Integer id);
    void updateCustomer(Customer update);
}
