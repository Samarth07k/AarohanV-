package com.artistlink.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds demo data on startup when artistlink.demo.seed-enabled is true
 * (default true for dev/demo; set DEMO_SEED=false in production).
 * Idempotent — the seed service no-ops if demo accounts already exist.
 */
@Component
public class DemoSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoSeedRunner.class);

    private final boolean enabled;
    private final DemoSeedService seedService;

    public DemoSeedRunner(@Value("${artistlink.demo.seed-enabled:true}") boolean enabled,
                          DemoSeedService seedService) {
        this.enabled = enabled;
        this.seedService = seedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("[demo-seed] Disabled (artistlink.demo.seed-enabled=false). Skipping.");
            return;
        }
        try {
            seedService.seed();
        } catch (Exception e) {
            // Never let demo seeding crash startup.
            log.warn("[demo-seed] Seeding failed (continuing startup): {}", e.getMessage(), e);
        }
    }
}
