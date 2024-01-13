package com.carolinacode.customer;

import com.carolinacode.exception.DuplicateResourceException;
import com.carolinacode.exception.RequestValidationException;
import com.carolinacode.exception.ResourceNotFoundException;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCostumers() {
        //When
        underTest.getAllCostumers();

        //Then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        Integer id =  10;
        Customer customer = new Customer(id, "alex", "alex@gmail.com", 19);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer)); //dizer ao mockito o comportamento do metodo

        //When
        Customer actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void cantGetCustomer() {
        //Given
        Integer id =  10;
        //Quando o resultado de chamar selectCustomerById(id) é vazio
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        //Then vai ser lançada um exceção co aquela mensagem
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";

        //mapear o argumento recebido por addCustomer
        CustomerRegistrationResquest resquest = new CustomerRegistrationResquest(
                "alex", email, 19
        );

        //assumindo que a primeira condicao no metodo addCustomer dá falso, a execução do método continua ..
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        //When addCustomer é chamado
        underTest.addCustomer(resquest);


        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        //Then queremos verificar que insertCustomer foiinvocado e queremos capturar o valor que lhe foi passado como argumento
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        //por fim, falta verificar que os valores do customer capturado sao os mesmos que os do request
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(resquest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(resquest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(resquest.age());
    }

    @Test
    void dontAddCustomer() {
        //Given
        String email = "alex@gmail.com";
        //mapear o argumento recebido por addCustomer
        CustomerRegistrationResquest request = new CustomerRegistrationResquest(
                "alex", email, 19
        );

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        //Then vai ser lançada um exceção com aquela mensagem
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        //verificar que o insertCustomer nunca é executado
         verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {
        //Given
        Integer id = 10;
        when(customerDAO.existsPersonWitId(id)).thenReturn(true);
        //When
        underTest.deleteCustomer(id);
        //Then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void deleteCustomer_thatDoesntExist() {
        //Given
        Integer id = 10;
        when(customerDAO.existsPersonWitId(id)).thenReturn(false);


        //Then vai ser lançada um exceção com aquela mensagem
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        //verificar que o deleteCustomerById nunca é executado
        verify(customerDAO, never()).deleteCustomerById(id);
    }



    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        Integer id =  10;
        String newEmail = "alexandre@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest("Alexandre", newEmail, 23);

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));

        //verificar que o novo email nao existe na base de dados
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer updatedCustomer = customerArgumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(request.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        Integer id =  10;
        CustomerUpdateRequest request = new CustomerUpdateRequest("Alexandre", null, null);

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));

        //When
        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer updatedCustomer = customerArgumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(request.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(oldCustomer.getEmail());
        assertThat(updatedCustomer.getAge()).isEqualTo(oldCustomer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        Integer id =  10;
        String newEmail = "alexandre@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, newEmail, null);

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));


        //verificar que o novo email nao existe na base de dados
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer updatedCustomer = customerArgumentCaptor.getValue();

        assertThat(updatedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(updatedCustomer.getName()).isEqualTo(oldCustomer.getName());
        assertThat(updatedCustomer.getAge()).isEqualTo(oldCustomer.getAge());
    }


    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        Integer id =  10;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, 21);

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));

        //When
        underTest.updateCustomer(id, request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer updatedCustomer = customerArgumentCaptor.getValue();

        assertThat(updatedCustomer.getAge()).isEqualTo(request.age());
        assertThat(updatedCustomer.getEmail()).isEqualTo(oldCustomer.getEmail());
        assertThat(updatedCustomer.getName()).isEqualTo(oldCustomer.getName());
    }

    @Test
    void cantUpdateBecauseDuplicateEmail() {
        //Given
        Integer id =  10;
        String newEmail = "alexandre@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, newEmail, null);

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));


        //verificar que o novo email nao existe na base de dados
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        //Then o customer nunca é atualizado
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void cantUpdateCustomerWithNoChanger() {
        //Given
        Integer id =  10;
        String newEmail = "alexandre@gmail.com";

        Customer oldCustomer = new Customer(id, "alex", "alex@gmail.com", 19);
        //ir buscar o customer
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(oldCustomer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(oldCustomer.getName(), oldCustomer.getEmail(), oldCustomer.getAge());


        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");
        //Then verificar que customer nunca é atualizado
        verify(customerDAO, never()).updateCustomer(any());

    }

}