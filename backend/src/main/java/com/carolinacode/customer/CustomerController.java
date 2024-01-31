package com.carolinacode.customer;

import com.carolinacode.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping()
    public List<CustomerDTO> getCustomers(){
        return customerService.getAllCostumers();
    }

    @GetMapping("{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Integer id){
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<?> RegisterCustomer(@RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER"); //criamos um token em que a subject é o email, que identifica o user e um scope para identificar o tipo de utilizador e dar as respetivas permições
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id){
        customerService.deleteCustomer(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(@RequestBody CustomerUpdateRequest request, @PathVariable("id") Integer id){
        customerService.updateCustomer(id, request);
    }
}
