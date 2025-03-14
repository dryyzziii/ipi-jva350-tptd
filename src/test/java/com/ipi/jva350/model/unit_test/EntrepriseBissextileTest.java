package com.ipi.jva350.model.unit_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipi.jva350.model.Entreprise;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntrepriseBissextileTest {

     // Méthode fournissant des données de test paramétrées
    static Stream<Arguments> anneesProvider() {
        return Stream.of(
            // année, estBissextile, description
            Arguments.of(2020, true, "Année divisible par 4 (règle générale)"),
            Arguments.of(2100, false, "Année divisible par 100 mais pas par 400"),
            Arguments.of(2023, false, "Année non bissextile standard"),
            Arguments.of(1900, false, "Année divisible par 100 mais pas par 400 (historique)"),
            Arguments.of(2024, true, "Année bissextile future"),
            Arguments.of(1500, false, "Année ancienne non bissextile")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("anneesProvider")
    @DisplayName("Test de la méthode bissextile pour différentes années")
    void testBissextile(int annee, boolean estBissextileAttendu, String description) {
        // WHEN (Act) : Appel de la méthode à tester
        boolean estBissextile = Entreprise.bissextile(annee);
        
        // THEN (Assert) : Vérification du résultat
        assertEquals(estBissextileAttendu, estBissextile, description);
    }
    
}
