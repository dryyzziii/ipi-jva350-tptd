package com.ipi.jva350.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EntrepriseTest {

    // Méthode fournissant des données de test paramétrées
    static Stream<Arguments> casDePlageProvider() {
        return Stream.of(
            // date, resultatAttendu, description
            Arguments.of(LocalDate.of(2023, 12, 31), true, "Date à la fin de l'année"),
            Arguments.of(null, false, "Date null"),
            Arguments.of(LocalDate.of(2022, 12, 31), false, "Date de l'année précédente")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("casDePlageProvider")
    @DisplayName("Test de la méthode estDansPlage avec différentes dates")
    void testEstDansPlage(LocalDate date, boolean resultatAttendu, String description) {
        // Création d'un salarié pour le test avec une plage définie
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setMoisEnCours(LocalDate.of(2023, 6, 1)); // Définit l'année en cours comme 2023-2024
    
        // WHEN (Act) : Appel de la méthode à tester
        boolean resultat = salarie.estDansPlage(date);
        
        // THEN (Assert) : Vérification du résultat
        assertEquals(resultatAttendu, resultat, description);
    }
}