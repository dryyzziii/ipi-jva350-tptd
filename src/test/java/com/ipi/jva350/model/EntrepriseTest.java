package com.ipi.jva350.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EntrepriseTest {

    // Méthode fournissant des données de test paramétrées
    static Stream<Arguments> casDePlageProvider() {
        LocalDate reference = LocalDate.of(2023, 5, 15);
        return Stream.of(
            // date, debut, fin, resultat attendu, description
            Arguments.of(reference, reference, reference, true, "Date égale aux bornes (même jour)"),
            Arguments.of(reference, reference.minusDays(5), reference.plusDays(5), true, "Date au milieu de la plage"),
            Arguments.of(reference, reference, reference.plusDays(10), true, "Date égale à la borne inférieure"),
            Arguments.of(reference, reference.minusDays(10), reference, true, "Date égale à la borne supérieure"),
            Arguments.of(reference, reference.plusDays(1), reference.plusDays(10), false, "Date avant la plage"),
            Arguments.of(reference, reference.minusDays(10), reference.minusDays(1), false, "Date après la plage"),
            Arguments.of(null, reference.minusDays(5), reference.plusDays(5), false, "Date null"),
            Arguments.of(reference, null, reference.plusDays(5), false, "Début null"),
            Arguments.of(reference, reference.minusDays(5), null, false, "Fin null"),
            Arguments.of(reference, null, null, false, "Début et fin null"),
            Arguments.of(null, null, null, false, "Tous les paramètres null"),
            Arguments.of(reference, reference.plusDays(5), reference.minusDays(5), false, "Plage inversée (début > fin)")
        );
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("casDePlageProvider")
    @DisplayName("Test de la méthode estDansPlage avec différents cas")
    void testEstDansPlage(LocalDate date, LocalDate debut, LocalDate fin, boolean resultatAttendu, String description) {
        // WHEN (Act) : Appel de la méthode à tester
        boolean resultat = Entreprise.estDansPlage(date, debut, fin);
        
        // THEN (Assert) : Vérification du résultat
        assertEquals(resultatAttendu, resultat, description);
    }

    @Test
    @DisplayName("Test de documentation - La méthode est inclusive")
    void testEstDansPlageInclusive() {
        // GIVEN (Arrange) : Préparation des dates pour vérifier le caractère inclusif
        LocalDate date = LocalDate.of(2023, 5, 15);
        LocalDate debut = date;
        LocalDate fin = date;
        
        // WHEN & THEN : Vérification que la méthode est inclusive
        assertTrue(Entreprise.estDansPlage(date, debut, fin), 
            "La méthode devrait être inclusive (retourner true si la date est égale aux bornes)");
    }
}