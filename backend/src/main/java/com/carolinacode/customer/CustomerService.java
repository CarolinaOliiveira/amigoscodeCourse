package com.carolinacode.customer;

import com.carolinacode.exception.DuplicateResourceException;
import com.carolinacode.exception.RequestValidationException;
import com.carolinacode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


//Business Layer
@Service
public class CustomerService {

    private final CustomerDAO customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCostumers() {
         return customerDao.selectAllCustomers()
                 .stream().map(customerDTOMapper)
                 .collect(Collectors.toList());

    }

    public CustomerDTO getCustomer(Integer id){
        return customerDao.selectCustomerById(id).map(customerDTOMapper)
                .orElseThrow(()-> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer (CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists, if not add customer
        String email = customerRegistrationRequest.email();
        if(customerDao.existsPersonWithEmail(email))
            throw new DuplicateResourceException("Email already taken");
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        email,
                        passwordEncoder.encode(customerRegistrationRequest.password()),
                        customerRegistrationRequest.age(),
                        customerRegistrationRequest.gender())
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
        Customer oldCustomer = customerDao.selectCustomerById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));

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
