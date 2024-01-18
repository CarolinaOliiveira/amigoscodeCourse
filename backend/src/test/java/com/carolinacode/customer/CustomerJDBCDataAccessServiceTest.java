package com.carolinacode.customer;

import com.carolinacode.AbstractTestContainers;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();


    //este metodo é executado antes de cada um dos testes da classe. Aqui estamos a criar um novo CustomerJDBCDataAccessService em cada teste.
    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    //Para testar o selectAllCustomers estamos a por um cliente, visto que a bd inicia vazia e dps a verificar que a base de dados nao se encontra vazia
    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                "Manuel", "manuel@gmail.com",58
        );
        underTest.insertCustomer(customer);

        List<Customer> actual = underTest.selectAllCustomers();
        System.out.println(actual);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = "manuela@gmail.com";
        Customer customer = new Customer(
                "Manuela", email,58
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);

        System.out.println(actual);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmpty_selectCustomerById() {
        String email = "manuele@gmail.com";
        Customer customer = new Customer(
                "Manuele", email,58
        );
        underTest.insertCustomer(customer);

        int id = -1;
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isEmpty();
    }


    @Test
    void existsPersonWithEmail() {
        String email = "manueli@gmail.com";
        Customer customer = new Customer(
                "Manueli", email,58
        );
        underTest.insertCustomer(customer);

        boolean result = underTest.existsPersonWithEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void doesntExistPersonWithEmail(){
        String email = "dfwegfadgh@gmail.com";

        boolean result = underTest.existsPersonWithEmail(email);

        assertThat(result).isFalse();
    }

    @Test
    void existsPersonWitId() {
        String email = "manuelo@gmail.com";
        Customer customer = new Customer(
                "Manuelo", email,58
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        boolean result = underTest.existsPersonWitId(id);

        assertThat(result).isTrue();
    }

    @Test
    void doesntExistPersonWithId(){
        int id = 0;

        boolean result = underTest.existsPersonWitId(id);

        assertThat(result).isFalse();
    }

    @Test
    void deleteCustomerById() {
        String email = "manuelu@gmail.com";
        Customer customer = new Customer(
                "Manuelu", email,58
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(id);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isEmpty();
    }

    @Test
    void deleteNonExistingCustomerById(){
        int id = 0;

        String email = "ana@gmail.com";
        Customer customer = new Customer(
                "Ana", email,58
        );
        underTest.insertCustomer(customer);

        underTest.deleteCustomerById(id);

        List<Customer> customers = underTest.selectAllCustomers();
        System.out.println(customers);

        assertThat(customers).isNotEmpty();
    }

    @Test
    void updateCustomer() {
        String email = "ane@gmail.com";
        Customer customer = new Customer(
                "Ane", email,58
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();


        String newEmail = "anu@gmail.com";
        String newName = "Anu";

        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()==id);
            assertThat(c.getAge()==58);
            assertThat(c.getName().equals(newName));
            assertThat(c.getEmail().equals(newEmail));
        });


    }
}