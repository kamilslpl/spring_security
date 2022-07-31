package pl.sda.arppl4.spring.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sda.arppl4.spring.security.model.ApplicationUser;
import pl.sda.arppl4.spring.security.repository.ApplicationUserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<ApplicationUser> applicationUserOptional = applicationUserRepository.findByUsername(username);
        if (applicationUserOptional.isPresent()){
            log.info("User found " + username );

            return applicationUserOptional.get();
        }
        log.warn("User not found !!! " + username);
        throw new UsernameNotFoundException("Cant find user with username " + username);

    }
}
