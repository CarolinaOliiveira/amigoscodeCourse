package com.carolinacode.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // pega campo onde está o token que existe no header de cada pedido

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //request é rejeitado
            return;
        }

        String jwt = authHeader.substring(7); //pega no token

        String subject = jwtUtil.getSubject(jwt);//pega no email que esta na subject do token

        if(subject != null && SecurityContextHolder.getContext().getAuthentication() ==null) { //se user!=null e não esta autenticado queremos autenticar o user
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject); //devolve o Customer que tem o email que estava no token

            if(jwtUtil.isTokenValid(jwt, userDetails.getUsername())){ //verificar se o token é válido
                UsernamePasswordAuthenticationToken authenticationToken = //criar o UsernamePasswordAuthenticationToken
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response); //continuar para o proximo filtro
    }
}
