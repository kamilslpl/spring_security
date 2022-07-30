package pl.sda.arppl4.spring.security.config.jwt;

public interface SecurityConstants {
    String APPLICATION_NAME = "springSecurity";
    String APPLICATION_KEY = "DSKDSFSFSJDS";
    String HEADER_AUTH = "Authorization";
    String HEADER_AUTH_BEARER = "Bearer:";
    String HEADER_EXPIRATION = "Expire_at";

    String HEADER_ROLES = "APP_roles";
    String ROLES_SEPARATOR = ",";
}
