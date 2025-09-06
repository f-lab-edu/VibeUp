package com.flab.vibeup.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping("/health")
public class DbHealthController {
    private final DataSource ds;
    public DbHealthController(DataSource ds) {
        this.ds = ds;
    }

    @GetMapping("/db")
    public String ping() throws Exception {
        try (var c = ds.getConnection(); var ps = c.prepareStatement("select 1"); var rs = ps.executeQuery()) {
            rs.next(); return "DB OK: " + rs.getInt(1);
        }
    }
}
