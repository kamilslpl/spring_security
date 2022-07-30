package pl.sda.arppl4.spring.security.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.sda.arppl4.spring.security.model.ApplicationUser;
import pl.sda.arppl4.spring.security.model.ApplicationUserRole;
import pl.sda.arppl4.spring.security.repository.dto.AuthenticationRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AuthenticationRequest authRequest = objectMapper.readValue(request.getInputStream(), AuthenticationRequest.class);

            Authentication authentication = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()));
            return authentication;

        } catch (IOException ioe) {
            log.error("Error while logging in");
            throw new RuntimeException(ioe);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // Rzutowanie / Casting
        ApplicationUser applicationUser;
        if (authResult.getPrincipal() instanceof ApplicationUser) {
            applicationUser = (ApplicationUser) authResult.getPrincipal();
        } else {
            throw new RuntimeException("Wrong type!");
        }

        String token = Jwts.builder()
                .setClaims(new HashMap<>() {
                    {
                        put("app_name", SecurityConstants.APPLICATION_NAME);
                        put("roles", applicationUser
                                .getRoles()
                                .stream()
                                .map(ApplicationUserRole::getName)
                                .collect(Collectors.joining(","))
                        );
                    }
                })
                .setSubject(applicationUser.getUsername())
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusDays(10)))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.APPLICATION_KEY)
                .compact();

        response.addHeader(SecurityConstants.HEADER_EXPIRATION, String.valueOf(Timestamp.valueOf(LocalDateTime.now().plusDays(10))));
        response.addHeader(SecurityConstants.HEADER_AUTH, SecurityConstants.HEADER_AUTH_BEARER + token);
        response.addHeader(SecurityConstants.HEADER_ROLES, String.valueOf(applicationUser.getRoles()));
        response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.info("UnsuccessfulAuthentication " + failed, failed);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());


    }
}
