package com.carolinacode.jouney;


import com.carolinacode.customer.Customer;
import com.carolinacode.customer.CustomerRegistrationRequest;
import com.carolinacode.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
Anotacao para testar com SpringBoot. Nestes testes chamados Integration tests, que testam
toda a aplicação através de pedidos API, já faz sentido ussar esta anotação porque "liga"
a aplicação por completo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final String uri = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        //create a registration request
        String name = "cata";
        String email = "cata@gmail.com";
        int age = 23;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        //send post request
        webTestClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        //verify that customer is present
        Customer expectedCustomer = new Customer(name, email, age);

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        //get customer by id
        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }
}
