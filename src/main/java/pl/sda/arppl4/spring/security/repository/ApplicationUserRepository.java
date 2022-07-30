package pl.sda.arppl4.spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sda.arppl4.spring.security.model.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserRepository
extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
