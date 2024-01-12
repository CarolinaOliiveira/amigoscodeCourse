package com.carolinacode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


//aceder aos dados com JDBC. JDBC deve ser usado quando existem queries complexas que têm de ser feitas à base de dados. Caso contrario JPA é o mais indicado porque tira a responsabilidade de escrever sql

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;


    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                """;

        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, age) 
                VALUES (?, ?, ?)
                """;
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
        System.out.println("jdbcTemplate.update = " + update);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {

        var sql = """
                SELECT count(*) 
                FROM customer
                WHERE email = ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, email);

        if(total != null && total>0)
            return true;
        else return false;
    }

    @Override
    public boolean existsPersonWitId(Integer id) {
        var sql = """
                SELECT count(*) 
                FROM customer
                WHERE id = ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, id);

        if(total != null && total>0)
            return true;
        else return false;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customer 
                WHERE id = ? 
                """;
        int result = jdbcTemplate.update(sql, id);
        System.out.println("deleteCustomerById result = " + result);
    }

    @Override
    public void updateCustomer(Customer update) {
        if(update.getName() != null){
            var sql = """
                update customer set name = ? where id = ?
                """;
            int result = jdbcTemplate.update(sql, update.getName(), update.getId());
            System.out.println("update customer name result = " + result);
        }

        if(update.getAge() != null){
            var sql = """
                update customer set age = ? where id = ?
                """;
            int result = jdbcTemplate.update(sql, update.getAge(), update.getId());
            System.out.println("update customer age result = " + result);
        }

        if(update.getEmail() != null){
            var sql = """
                update customer set email = ? where id = ?
                """;
            int result = jdbcTemplate.update(sql, update.getEmail(), update.getId());
            System.out.println("update customer email result = " + result);
        }
    }
}
