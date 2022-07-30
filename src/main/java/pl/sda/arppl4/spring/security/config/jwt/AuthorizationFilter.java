package pl.sda.arppl4.spring.security.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {


    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException{
        String header = request.getHeader(SecurityConstants.HEADER_AUTH);

        if (header.startsWith(SecurityConstants.HEADER_AUTH_BEARER)){
            String[] headerParts = header.split(":");
            if(headerParts.length == 2){
                String headerToken = headerParts[1];
                UsernamePasswordAuthenticationToken token = extractTokenFromJWT(headerToken);
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken extractTokenFromJWT(String jwtToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.APPLICATION_KEY)
                .parseClaimsJws(jwtToken)
                .getBody();

        String username = claims.getSubject();
        String roles = claims.get("roles", String.class);

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                Arrays.asList(roles.split(SecurityConstants.ROLES_SEPARATOR))
                        .stream()
                        .map((name) -> new SimpleGrantedAuthority("ROLE_" +name))
                        .collect(Collectors.toSet())
        );

    }
}
