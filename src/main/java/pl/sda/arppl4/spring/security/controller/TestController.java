package pl.sda.arppl4.spring.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    // dostepne dla kazdego / api/test
    @GetMapping("")
    public String test(){
        return "test";
    }

    // dostpene dla zalogowanego /api/test/authorized
    @GetMapping("/authorized")
    public String testAuthorized(Authentication principal)
    {
        log.info("Po filtracji: " + principal);
        return "testAuthorized GREAT";
    }

}
