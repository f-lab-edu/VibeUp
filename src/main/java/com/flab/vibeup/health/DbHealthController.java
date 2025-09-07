package com.flab.vibeup.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class DbHealthController {
    @PersistenceContext
    private EntityManager em;

    @GetMapping("/health/db")
    public String ping() {
        Integer result = (Integer) em.createNativeQuery("select 1").getSingleResult();
        return "DB OK: " + result;
    }
}