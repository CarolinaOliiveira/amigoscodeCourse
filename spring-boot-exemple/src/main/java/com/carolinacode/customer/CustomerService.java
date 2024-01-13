package com.carolinacode.customer;

import com.carolinacode.exception.DuplicateResourceException;
import com.carolinacode.exception.RequestValidationException;
import com.carolinacode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


//Business Layer
@Service
public class CustomerService {

    private final CustomerDAO customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCostumers() {
         return customerDao.selectAllCustomers();

    }

    public Customer getCustomer(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer (CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists, if not add customer
        String email = customerRegistrationRequest.email();
        if(customerDao.existsPersonWithEmail(email))
            throw new DuplicateResourceException("Email already taken");
        customerDao.insertCustomer(
                new Customer(customerRegistrationRequest.name(),
                        email,
                        customerRegistrationRequest.age())
        );
    }

    public void deleteCustomer(Integer id){
        if(!customerDao.existsPersonWitId(id))
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest request){
        boolean changes=false;
        //Ver se existe
        Customer oldCustomer = getCustomer(id);

        //checkar todos os campos e confirmar se há novos updates
        if(request.name()!=null && !request.name().equals(oldCustomer.getName())) {
            oldCustomer.setName(request.name());
            changes = true;
        }

        if(request.age()!=null && request.age()!=oldCustomer.getAge()){
            oldCustomer.setAge(request.age());
            changes=true;
        }

        if(request.email()!=null && !request.email().equals(oldCustomer.getEmail())){
            if(customerDao.existsPersonWithEmail(request.email()))
                throw new DuplicateResourceException("Email already taken");

            oldCustomer.setEmail(request.email());
            changes=true;
        }

        //mensagem de erro por não haver nada a alterar
        if (!changes)
           throw new RequestValidationException("no data changes found");

        customerDao.updateCustomer(oldCustomer);
    }
}
