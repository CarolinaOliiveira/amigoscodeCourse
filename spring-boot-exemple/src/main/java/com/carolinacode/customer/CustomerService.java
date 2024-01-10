package com.carolinacode.customer;

import com.carolinacode.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCostumers() {
         return customerDao.selectAllCustomers();

    }

    public Customer getCustomer(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(()-> new ResourceNotFound("Customer with id [%s] not found".formatted(id)));
    }
}
