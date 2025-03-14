package com.ipi.jva350.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.stream.Stream;

class EntrepriseEstJourFerieTest {
    
    // Méthode fournissant des données de test paramétrées
    static Stream<Arguments> joursFeriesProvider() {
        return Stream.of(
            // jour, estFerie, description
            Arguments.of(LocalDate.of(2023, 1, 1), true, "1er janvier - Jour de l'an"),
            Arguments.of(LocalDate.of(2023, 4, 10), true, "Lundi de Pâques"),
            Arguments.of(LocalDate.of(2023, 5, 1), true, "1er mai - Fête du Travail"),
            Arguments.of(LocalDate.of(2023, 5, 8), true, "8 mai - Fête de la Victoire"),
            Arguments.of(LocalDate.of(2023, 5, 29), true, "Lundi de Pentecôte"),
            Arguments.of(LocalDate.of(2023, 7, 14), true, "14 juillet - Fête nationale"),
            Arguments.of(LocalDate.of(2023, 8, 15), true, "15 août - Assomption"),
            Arguments.of(LocalDate.of(2023, 11, 1), true, "1er novembre - Toussaint"),
            Arguments.of(LocalDate.of(2023, 11, 11), true, "11 novembre - Armistice"),
            Arguments.of(LocalDate.of(2023, 12, 25), true, "25 décembre - Noël"),
            
            // Jours non fériés
            Arguments.of(LocalDate.of(2023, 4, 9), false, "Dimanche de Pâques - non férié"),
            Arguments.of(LocalDate.of(2023, 3, 15), false, "Jour normal - mars"),
            Arguments.of(LocalDate.of(2023, 7, 15), false, "Jour normal - juillet"),
            Arguments.of(LocalDate.of(2023, 12, 26), false, "Jour normal - lendemain de Noël"),
            
            // Années bissextiles
            Arguments.of(LocalDate.of(2024, 1, 1), true, "1er janvier 2024 (année bissextile)")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("joursFeriesProvider")
    @DisplayName("Test de la méthode estJourFerie pour différentes dates")
    void testEstJourFerie(LocalDate jour, boolean estFerieAttendu, String description) {
        // WHEN (Act) : Appel de la méthode à tester
        boolean estFerie = Entreprise.estJourFerie(jour);
        
        // THEN (Assert) : Vérification du résultat
        assertEquals(estFerieAttendu, estFerie, description);
    }
}
