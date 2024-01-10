package com.carolinacode;

import com.carolinacode.customer.Customer;
import com.carolinacode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){

        return args -> {
            Customer alex = new Customer("Alex", "alex@gmail.com", 21);

            Customer jamila = new Customer ("Jamila", "jamila@gmail.com", 19);
            List <Customer> customers = List.of(alex, jamila);
            //customerRepository.saveAll(customers);
        };
    }



}
