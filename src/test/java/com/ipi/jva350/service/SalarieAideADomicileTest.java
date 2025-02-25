package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ipi.jva350.model.SalarieAideADomicile;

public class SalarieAideADomicileTest {

    private SalarieAideADomicile salarie;
    
    @BeforeEach
    public void setUp() {
        // Initialisation d'un salarié pour les tests
        salarie = new SalarieAideADomicile();
        salarie.setMoisDebutContrat(LocalDate.of(2022, 1, 1)); // Début de contrat au 1er janvier 2022
        salarie.setMoisEnCours(LocalDate.of(2022, 5, 1)); // Mois en cours: mai 2022
        // On s'assure que les congés pris sont vides pour chaque test
        salarie.setCongesPayesPris(new LinkedHashSet<>());
    }
    
    @Test
    @DisplayName("Un salarié avec moins de 10 jours travaillés l'année N-1 n'a pas droit aux congés payés")
    public void testPasDeCongesPayesAvecMoinsDe10JoursTravailles() {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(9); // Moins de 10 jours travaillés
        
        // Act & Assert
        assertFalse(salarie.aLegalementDroitADesCongesPayes());
    }
    
    @Test
    @DisplayName("Un salarié avec exactement 10 jours travaillés l'année N-1 n'a pas droit aux congés payés")
    public void testPasDeCongesPayesAvec10JoursTravailles() {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(10); // Exactement 10 jours travaillés
        
        // Act & Assert
        assertFalse(salarie.aLegalementDroitADesCongesPayes());
    }
    
    @Test
    @DisplayName("Un salarié avec 11 jours travaillés l'année N-1 a droit aux congés payés")
    public void testCongesPayesAvec11JoursTravailles() {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(11); // 11 jours travaillés (juste au-dessus de la limite)
        
        // Act & Assert
        assertTrue(salarie.aLegalementDroitADesCongesPayes());
    }
    
    @Test
    @DisplayName("Un salarié avec beaucoup de jours travaillés l'année N-1 a droit aux congés payés")
    public void testCongesPayesAvecBeaucoupDeJoursTravailles() {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(200); // Beaucoup de jours travaillés
        
        // Act & Assert
        assertTrue(salarie.aLegalementDroitADesCongesPayes());
    }
    
    @Test
    @DisplayName("Un salarié qui commence juste (0 jours travaillés l'année N-1) n'a pas droit aux congés payés")
    public void testPasDeCongesPayesPourNouvelEmploye() {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(0); // Nouvel employé
        
        // Act & Assert
        assertFalse(salarie.aLegalementDroitADesCongesPayes());
    }
    
    @Test
    @DisplayName("Un salarié avec valeur négative de jours travaillés l'année N-1 n'a pas droit aux congés payés")
    public void testPasDeCongesPayesAvecJoursTravaillesNegatifs() {
        // Arrange - cas improbable mais bon pour la couverture de test
        salarie.setJoursTravaillesAnneeNMoins1(-5); 
        
        // Act & Assert
        assertFalse(salarie.aLegalementDroitADesCongesPayes());
    }
}