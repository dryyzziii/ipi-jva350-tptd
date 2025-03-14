package com.ipi.jva350.service.unit_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ipi.jva350.model.SalarieAideADomicile;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class SalarieAideADomicileBuilderTest {
     @Test
    @DisplayName("Test des méthodes du Builder pour les paramètres de l'année N")
    void testBuilderMethodesAnneeN() {
        // GIVEN (Arrange) : Valeurs pour le test
        String nom = "Dupont";
        LocalDate moisDebutContrat = LocalDate.of(2020, 1, 1);
        LocalDate moisEnCours = LocalDate.of(2023, 6, 1);
        double joursTravaillesAnneeN = 150.5;
        double congesPayesAcquisAnneeN = 18.5;
        
        // WHEN (Act) : Utilisation des méthodes du Builder
        SalarieAideADomicile salarie = new SalarieAideADomicile.Builder(nom, moisDebutContrat, moisEnCours)
            .joursTravaillesAnneeN(joursTravaillesAnneeN)
            .congesPayesAcquisAnneeN(congesPayesAcquisAnneeN)
            .build();
        
        // THEN (Assert) : Vérification que les valeurs ont été correctement définies
        assertEquals(nom, salarie.getNom(), "Le nom devrait correspondre");
        assertEquals(moisDebutContrat, salarie.getMoisDebutContrat(), "Le mois de début de contrat devrait correspondre");
        assertEquals(moisEnCours, salarie.getMoisEnCours(), "Le mois en cours devrait correspondre");
        assertEquals(joursTravaillesAnneeN, salarie.getJoursTravaillesAnneeN(), "Les jours travaillés année N devraient correspondre");
        assertEquals(congesPayesAcquisAnneeN, salarie.getCongesPayesAcquisAnneeN(), "Les congés acquis année N devraient correspondre");
    }

    @Test
    @DisplayName("Test des méthodes du Builder pour les paramètres de l'année N-1")
    void testBuilderMethodesAnneeNMoins1() {
        // GIVEN (Arrange) : Valeurs pour le test
        String nom = "Martin";
        LocalDate moisDebutContrat = LocalDate.of(2019, 6, 1);
        LocalDate moisEnCours = LocalDate.of(2023, 5, 1);
        double joursTravaillesAnneeNMoins1 = 200.0;
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        double congesPayesPrisAnneeNMoins1 = 15.0;
        
        // WHEN (Act) : Utilisation des méthodes du Builder
        SalarieAideADomicile salarie = new SalarieAideADomicile.Builder(nom, moisDebutContrat, moisEnCours)
            .joursTravaillesAnneeNMoins1(joursTravaillesAnneeNMoins1)
            .congesPayesAcquisAnneeNMoins1(congesPayesAcquisAnneeNMoins1)
            .congesPayesPrisAnneeNMoins1(congesPayesPrisAnneeNMoins1)
            .build();
        
        // THEN (Assert) : Vérification que les valeurs ont été correctement définies
        assertEquals(nom, salarie.getNom(), "Le nom devrait correspondre");
        assertEquals(moisDebutContrat, salarie.getMoisDebutContrat(), "Le mois de début de contrat devrait correspondre");
        assertEquals(moisEnCours, salarie.getMoisEnCours(), "Le mois en cours devrait correspondre");
        assertEquals(joursTravaillesAnneeNMoins1, salarie.getJoursTravaillesAnneeNMoins1(), "Les jours travaillés année N-1 devraient correspondre");
        assertEquals(congesPayesAcquisAnneeNMoins1, salarie.getCongesPayesAcquisAnneeNMoins1(), "Les congés acquis année N-1 devraient correspondre");
        assertEquals(congesPayesPrisAnneeNMoins1, salarie.getCongesPayesPrisAnneeNMoins1(), "Les congés pris année N-1 devraient correspondre");
    }

    @Test
    @DisplayName("Test de la méthode getPremierJourAnneeDeConges")
    void testGetPremierJourAnneeDeConges() {
        // GIVEN (Arrange) : Différentes dates pour tester les cas
        LocalDate dateJuin = LocalDate.of(2023, 6, 15);
        LocalDate dateJanvier = LocalDate.of(2023, 1, 15);
        LocalDate dateNull = null;
        
        // WHEN & THEN (Act & Assert) : Vérification des résultats pour chaque cas
        // Pour une date en juin ou après, le premier jour de l'année de congés est le 1er juin de la même année
        assertEquals(LocalDate.of(2023, 6, 1), SalarieAideADomicile.getPremierJourAnneeDeConges(dateJuin),
            "Le premier jour pour une date en juin devrait être le 1er juin de la même année");
        
        // Pour une date avant juin, le premier jour de l'année de congés est le 1er juin de l'année précédente
        assertEquals(LocalDate.of(2022, 6, 1), SalarieAideADomicile.getPremierJourAnneeDeConges(dateJanvier),
            "Le premier jour pour une date en janvier devrait être le 1er juin de l'année précédente");
        
        // Pour une date null, le résultat devrait être null
        assertNull(SalarieAideADomicile.getPremierJourAnneeDeConges(dateNull),
            "Le premier jour pour une date null devrait être null");
    }
}
