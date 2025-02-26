package com.ipi.jva350;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
public class SpringCucumberIntegrationTest {
    
    @Test
    public void springCucumberIntegrationWorks() {
        // Vérification simple que l'intégration Spring-Cucumber fonctionne
        // Ce test ne fait rien d'autre que confirmer que le contexte a été chargé
        // Les tests réels se feront dans les steps Cucumber
        assertNotNull(this, "Cette classe devrait pouvoir s'instancier");
    }
}