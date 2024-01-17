package com.carolinacode.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("age")).thenReturn(19);
        when(rs.getString("name")).thenReturn("alex");
        when(rs.getString("email")).thenReturn("alex@gmail.com");
        //When
        Customer actual = customerRowMapper.mapRow(rs, 1);

        Customer expected = new Customer(1, "alex", "alex@gmail.com", 19);

        assertThat(actual).isEqualTo(expected);

    }
}