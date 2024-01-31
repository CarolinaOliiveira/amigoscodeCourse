package com.carolinacode.customer;


import java.util.List;

public record CustomerDTO (
    //para o DTO Pattern, que em vez de enviarmos aos pedidos http os clientes, enviamos uma nova entidade, CustomerDTO, onde podemos controlar quais os parametros que são passados ao cliente
    Integer id,
    String name,
    String email,
    Gender gender,
    Integer age,
    List<String> roles,
    String username
){

}
