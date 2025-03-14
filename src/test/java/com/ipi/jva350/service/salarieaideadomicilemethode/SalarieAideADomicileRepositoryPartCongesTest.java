package com.ipi.jva350.service.salarieaideadomicilemethode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;

@SpringBootTest
class SalarieAideADomicileRepositoryPartCongesTest {

    @Autowired
    private SalarieAideADomicileRepository salarieRepo;
    
    @BeforeEach
    void setUp() {
        // Nettoyage de la base de données avant chaque test
        salarieRepo.deleteAll();
    }
    
    @Test
    void testPartCongesPrisTotauxAnneeNMoins1AvecSalaries() {
        // GIVEN (Arrange) : Création de salariés avec des congés acquis et pris
        // Salarié 1: a pris 15 jours sur 25 acquis (60%)
        SalarieAideADomicile salarie1 = new SalarieAideADomicile.Builder("Dupont", 
            LocalDate.of(2020, 1, 1), LocalDate.of(2023, 5, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(15.0)
            .build();
        
        // Salarié 2: a pris 20 jours sur 25 acquis (80%)
        SalarieAideADomicile salarie2 = new SalarieAideADomicile.Builder("Martin", 
            LocalDate.of(2019, 6, 1), LocalDate.of(2023, 5, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(20.0)
            .build();
        
        salarieRepo.save(salarie1);
        salarieRepo.save(salarie2);
        
        // WHEN (Act) : Calcul de la part des congés pris
        Double partCongesPris = salarieRepo.partCongesPrisTotauxAnneeNMoins1();
        
        // THEN (Assert) : Vérification que la part est correcte
        // (15 + 20) / (25 + 25) = 35 / 50 = 0.7 ou 70%
        assertNotNull(partCongesPris, "La part des congés pris ne devrait pas être null");
        assertEquals(0.7, partCongesPris, 0.001, "La part des congés pris devrait être de 70%");
    }
    
    @Test
    void testPartCongesPrisTotauxAnneeNMoins1SansConges() {
        // GIVEN (Arrange) : Création de salariés sans congés pris
        SalarieAideADomicile salarie1 = new SalarieAideADomicile.Builder("Dupont", 
            LocalDate.of(2020, 1, 1), LocalDate.of(2023, 5, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(0.0)
            .build();
        
        SalarieAideADomicile salarie2 = new SalarieAideADomicile.Builder("Martin", 
            LocalDate.of(2019, 6, 1), LocalDate.of(2023, 5, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(0.0)
            .build();
        
        salarieRepo.save(salarie1);
        salarieRepo.save(salarie2);
        
        // WHEN (Act) : Calcul de la part des congés pris
        Double partCongesPris = salarieRepo.partCongesPrisTotauxAnneeNMoins1();
        
        // THEN (Assert) : Vérification que la part est de 0%
        assertNotNull(partCongesPris, "La part des congés pris ne devrait pas être null");
        assertEquals(0.0, partCongesPris, 0.001, "La part des congés pris devrait être de 0%");
    }
    
    @Test
    void testPartCongesPrisTotauxAnneeNMoins1SansSalaries() {
        // WHEN (Act) : Calcul de la part des congés pris sans salariés en base
        Double partCongesPris = salarieRepo.partCongesPrisTotauxAnneeNMoins1();
        
        // THEN (Assert) : Vérification que la méthode renvoie null si aucun salarié
        assertNull(partCongesPris, "La part des congés pris devrait être null sans salariés");
    }
}