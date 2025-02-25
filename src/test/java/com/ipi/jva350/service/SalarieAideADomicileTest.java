package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipi.jva350.model.SalarieAideADomicile;

class SalarieAideADomicileTest {
    private SalarieAideADomicile salarie;

    @BeforeEach
    void setUp() {
        // Initialisation d'un salarié pour les tests
        salarie = new SalarieAideADomicile();
        salarie.setMoisDebutContrat(LocalDate.of(2022, 1, 1)); // Début de contrat au 1er janvier 2022
        salarie.setMoisEnCours(LocalDate.of(2022, 5, 1)); // Mois en cours: mai 2022
        // On s'assure que les congés pris sont vides pour chaque test
        salarie.setCongesPayesPris(new LinkedHashSet<>());
    }

    // Source de données pour le test paramétré
    static Stream<Arguments> joursTravaillesProvider() {
        return Stream.of(
            // joursTravailles, droitAuxCongesPayes, description
            Arguments.of(9.0, false, "Moins de 10 jours travaillés"),
            Arguments.of(10.0, false, "Exactement 10 jours travaillés"),
            Arguments.of(11.0, true, "11 jours travaillés (juste au-dessus de la limite)"),
            Arguments.of(200.0, true, "Beaucoup de jours travaillés"),
            Arguments.of(0.0, false, "Nouvel employé"),
            Arguments.of(-5.0, false, "Valeur négative de jours travaillés")
        );
    }

    @ParameterizedTest(name = "{2}: {0} jours travaillés => droit aux congés payés: {1}")
    @MethodSource("joursTravaillesProvider")
    @DisplayName("Test du droit aux congés payés selon les jours travaillés")
    void testDroitAuxCongesPayes(double joursTravailles, boolean droitAttendu, String description) {
        // Arrange
        salarie.setJoursTravaillesAnneeNMoins1(joursTravailles);
        
        // Act & Assert
        assertEquals(droitAttendu, salarie.aLegalementDroitADesCongesPayes(),
                "Pour " + joursTravailles + " jours travaillés (" + description + "), le droit aux congés payés devrait être " + droitAttendu);
    }
}