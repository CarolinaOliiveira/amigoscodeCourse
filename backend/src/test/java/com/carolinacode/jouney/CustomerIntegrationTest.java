package com.carolinacode.jouney;


import com.carolinacode.customer.*;
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
toda a aplicação através de pedidos API, já faz sentido usar esta anotação porque "liga"
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
        Gender gender =Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
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
        Customer expectedCustomer = new Customer(name, email, age, Gender.FEMALE);

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

    @Test
    void canDeleteCustomer() {
        //create a registration request
        String name = "cate";
        String email = "cate@gmail.com";
        int age = 23;
        Gender gender = Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
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

        //get the of the customer registered
        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();


        //delete customer with id
        webTestClient.delete()
                        .uri(uri + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk();

        //get customer by id
        webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer(){
        //create a registration request
        String name = "car";
        String email = "car@gmail.com";
        int age = 23;
        Gender gender = Gender.MALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
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

        //get customer by id
        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        String newName = "caro";
        String newEmail = "caro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, newEmail, null);
        Customer expectedCustomer = new Customer(id, newName, newEmail, age, Gender.MALE);

        //fazer update do customer
        webTestClient.put()
                        .uri(uri + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                        .exchange()
                        .expectStatus().isOk();

        //ir buscar o cliente e verificar que se ele foi atualizado
        Customer updatedCustomer = webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }
}
