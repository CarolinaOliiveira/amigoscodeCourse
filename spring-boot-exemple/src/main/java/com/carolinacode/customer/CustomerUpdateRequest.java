package com.carolinacode.customer;

public record CustomerUpdateRequest (
        String name,
        String email,
        Integer age
)
{

}
