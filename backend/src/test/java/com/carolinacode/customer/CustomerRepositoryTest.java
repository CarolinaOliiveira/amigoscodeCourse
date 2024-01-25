package com.carolinacode.customer;

import com.carolinacode.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest //permite carregar apenas os beans que os components JPA precisam
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers { // ao fazer extend da AbstractTestConstainer, estámos a forçar que a BD a ser usada seja a do TestContainer. Caso contrario usava a que esta no Application.yml

    @Autowired // faz com que seja automaticamente inicializado
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
    }

    @Test
    void existsCostumerByEmail() {
        String email = "manueli@gmail.com";
        Customer customer = new Customer(
                "Manueli", email,58,
                Gender.MALE);
        underTest.save(customer);

        boolean result = underTest.existsCostumerByEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void CostumerByEmailDoesntExist() {
        String email = "aerghrtdjiu@gmail.com";


        boolean result = underTest.existsCostumerByEmail(email);

        assertThat(result).isFalse();
    }
}