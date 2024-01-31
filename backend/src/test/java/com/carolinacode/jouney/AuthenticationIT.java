package com.carolinacode.jouney;

import com.carolinacode.auth.AuthenticationRequest;
import com.carolinacode.auth.AuthenticationResponse;
import com.carolinacode.customer.CustomerDTO;
import com.carolinacode.customer.CustomerRegistrationRequest;
import com.carolinacode.customer.Gender;
import com.carolinacode.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;
    private static final String AUTHENTICATION_PATH = "/api/v1/auth/login";
    private static final String CUSTOMER_PATH = "/api/v1/customers";
    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void canLogin() {

        String name = "Anabela";
        String email = "ana@gmail.com";
        int age = 51;
        Gender gender =Gender.FEMALE;
        String password= "password";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        AuthenticationRequest loginRequest = new AuthenticationRequest(email, password);

        //verificar que ele n consegue fazer login quando o cliente nao existe
        webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        //registar cliente
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //fazer login cliente
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(loginRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        //pegar no token que existe no header da resposta do login
        String token = result.getResponseHeaders()
                .get(AUTHORIZATION).get(0);

        AuthenticationResponse response = result.getResponseBody();
        CustomerDTO customerDTO = response.customerDTO();

        //verificar que o token é valido
        assertThat(jwtUtil.isTokenValid(token, customerDTO.username()));

        //confirmar que os parametros do utilizador registamos sao os mesmos que passamos no registo
        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name );
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));



    }
}
