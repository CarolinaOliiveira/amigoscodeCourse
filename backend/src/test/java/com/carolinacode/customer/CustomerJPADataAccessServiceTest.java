package com.carolinacode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    @Mock //faz com que a entidade customerRepository seja apenas um mock e não a verdadeira classe, visto que esta já foi testada, assim temos de trabalhar com menos dependencias
    private CustomerRepository customerRepository;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); //inicializar o mock
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception { //faz com que no final de cada teste tenhamos um novo mock
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();

        //Then
        verify(customerRepository).findAll();
        /*verifica se sempre que o metodo selectAllCostumers do CustomerJPADataAccessService,
         o metodo findAll do Customer Repository tb é chamado
         */
    }

    @Test
    void selectCustomerById() {
        //Given
        int id = 1;

        //When
        underTest.selectCustomerById(id);

        //Then
        verify(customerRepository).findById(id);
        /*
        Verifica que findById é chamado e que o argumento que é passado é o mesmo
        que o que o método selectCustomerById recebe
         */
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer("Ana", "ana@gmail.com", 20, Gender.MALE);

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = "ana@gmail.com";

        //When
        underTest.existsPersonWithEmail(email);

        //Then
        verify(customerRepository).existsCostumerByEmail(email);
    }

    @Test
    void existsPersonWitId() {
        //Given
        Integer id = 0;

        //When
        underTest.existsPersonWitId(id);

        //Then
        verify(customerRepository).existsById(id);
    }

    @Test
    void deleteCustomerById() {
        //Given
        Integer id = 0;

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer update = new Customer("Ana", "ana@gmail.com", 20, Gender.MALE);

        //When
        underTest.updateCustomer(update);

        //Then
        verify(customerRepository).save(update);
    }
}