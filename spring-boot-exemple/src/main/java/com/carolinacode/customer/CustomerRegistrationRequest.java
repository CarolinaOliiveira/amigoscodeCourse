package com.carolinacode.customer;

public record CustomerRegistrationRequest(
    String name,
    String email,
    Integer age
){

}
