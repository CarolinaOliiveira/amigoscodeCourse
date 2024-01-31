package com.carolinacode.jouney;


import com.carolinacode.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
                name, email, "password", age, gender
        );

        //send post request
        String jwtToken = webTestClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //get customer by id
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(c -> c.id())
                .findFirst()
                .orElseThrow();

        //verify that customer is present
        CustomerDTO expectedCustomer = new CustomerDTO(
                id, name, email, gender, age, List.of("ROLE_USER"), email
        );

        assertThat(allCustomers).contains(expectedCustomer);

        webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        //create a registration request
        String name = "cate";
        String email = "cate@gmail.com";
        int age = 23;
        Gender gender = Gender.FEMALE;
        CustomerRegistrationRequest request1 = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, "cati@gmail.com", "password", age, gender
        );

        //send post request to create customer 1 (customer que vai ser eliminado)
        webTestClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request1), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();


        //send post request to create customer 2 (para ter sempre o token)
        String jwtToken = webTestClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);;

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //get the of the customer registered
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(c -> c.id())
                .findFirst()
                .orElseThrow();


        //delete customer 1
        webTestClient.delete()
                        .uri(uri + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                        .exchange()
                        .expectStatus()
                        .isOk();

        //get customer 1
        webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
                name, email, "password", age, gender
        );

        //send post request
        String jwtToken =  webTestClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //get customer by id
        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(c -> c.id())
                .findFirst()
                .orElseThrow();

        String newName = "caro";
        String newEmail = "caro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, email, null);
        CustomerDTO expectedCustomer = new CustomerDTO(id, newName, email, Gender.MALE, age, List.of("ROLE_USER"), email);

        //fazer update do customer
        webTestClient.put()
                        .uri(uri + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                        .exchange()
                        .expectStatus().isOk();

        //ir buscar o cliente e verificar que se ele foi atualizado
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(uri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }
}
