package com.ipi.jva350.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ipi.jva350.model.SalarieAideADomicile;

@DataJpaTest
class SalarieAideADomicileRepositoryTest {

    @Autowired
    private SalarieAideADomicileRepository salarieRepo;

    @Test
    void testFindByNom() {
        // Arrange
        String nomSalarie = "Dupont";
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom(nomSalarie);
        salarie.setMoisDebutContrat(LocalDate.of(2022, 1, 1));
        salarie.setMoisEnCours(LocalDate.of(2022, 5, 1));
        
        // Act
        salarieRepo.save(salarie);
        SalarieAideADomicile trouve = salarieRepo.findByNom(nomSalarie);
        
        // Assert
        assertNotNull(trouve, "Le salarié devrait être trouvé par son nom");
        assertEquals(nomSalarie, trouve.getNom(), "Le nom du salarié trouvé devrait correspondre");
        assertEquals(salarie.getMoisDebutContrat(), trouve.getMoisDebutContrat(), 
            "La date de début de contrat devrait correspondre");
    }
    
    @Test
    void testFindByNomInexistant() {
        // Act
        SalarieAideADomicile trouve = salarieRepo.findByNom("NomInexistant");
        
        // Assert
        assertNull(trouve, "Aucun salarié ne devrait être trouvé avec un nom inexistant");
    }
    
    @Test
    void testFindByNomAvecPlusieursSalaries() {
        // Arrange
        String nomSalarie1 = "Dupont";
        String nomSalarie2 = "Martin";
        
        SalarieAideADomicile salarie1 = new SalarieAideADomicile();
        salarie1.setNom(nomSalarie1);
        salarie1.setMoisDebutContrat(LocalDate.of(2022, 1, 1));
        salarie1.setMoisEnCours(LocalDate.of(2022, 5, 1));
        
        SalarieAideADomicile salarie2 = new SalarieAideADomicile();
        salarie2.setNom(nomSalarie2);
        salarie2.setMoisDebutContrat(LocalDate.of(2021, 6, 1));
        salarie2.setMoisEnCours(LocalDate.of(2022, 5, 1));
        
        salarieRepo.save(salarie1);
        salarieRepo.save(salarie2);
        
        // Act
        SalarieAideADomicile trouveDupont = salarieRepo.findByNom(nomSalarie1);
        SalarieAideADomicile trouveMartin = salarieRepo.findByNom(nomSalarie2);
        
        // Assert
        assertNotNull(trouveDupont, "Le salarié Dupont devrait être trouvé");
        assertNotNull(trouveMartin, "Le salarié Martin devrait être trouvé");
        assertEquals(nomSalarie1, trouveDupont.getNom(), "Le nom du salarié trouvé devrait être Dupont");
        assertEquals(nomSalarie2, trouveMartin.getNom(), "Le nom du salarié trouvé devrait être Martin");
    }
}