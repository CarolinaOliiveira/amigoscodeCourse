package com.carolinacode.auth;

import com.carolinacode.customer.CustomerDTO;

public record AuthenticationResponse (
        String token,
        CustomerDTO customerDTO

) {


}
