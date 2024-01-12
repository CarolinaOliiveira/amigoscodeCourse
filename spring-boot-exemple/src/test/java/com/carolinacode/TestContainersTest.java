package com.carolinacode;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;


//nunca utilizar a anotação @SpringBootTest para unit tests, porque vai "ligar" o application context e fazer com que os testes sejam mais lentos.


@Testcontainers
public class TestContainersTest extends AbstractTestContainers {

    @Test
    void canStartPostgresDB() {
       assertThat(postgresSQLContainer.isRunning()).isTrue();
        assertThat(postgresSQLContainer.isCreated()).isTrue();
    }

}
