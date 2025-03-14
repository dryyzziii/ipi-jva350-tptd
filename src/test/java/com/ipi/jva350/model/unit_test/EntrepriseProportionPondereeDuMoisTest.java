package com.ipi.jva350.model.unit_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ipi.jva350.model.Entreprise;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntrepriseProportionPondereeDuMoisTest {
    static Stream<Arguments> moisProvider() {
        return Stream.of(
                // mois, categorieAttendue, description
                Arguments.of(LocalDate.of(2023, 7, 15), "été", "Juillet - haute saison été"),
                Arguments.of(LocalDate.of(2023, 8, 15), "été", "Août - haute saison été"),
                Arguments.of(LocalDate.of(2023, 6, 15), "normal", "Juin - saison normale"),
                Arguments.of(LocalDate.of(2023, 12, 15), "normal", "Décembre - saison normale"),
                Arguments.of(LocalDate.of(2023, 1, 15), "normal", "Janvier - saison normale"),
                Arguments.of(null, "null", "Date null"));
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("moisProvider")
    @DisplayName("Test de proportionPondereeDuMois pour différents mois")
    void testProportionPondereeDuMois(LocalDate date, String categorieAttendue, String description) {
        // WHEN (Act) : Appel de la méthode à tester
        double proportion = Entreprise.proportionPondereeDuMois(date);

        // THEN (Assert) : Vérification selon la catégorie de mois
        if (categorieAttendue.equals("été")) {
            assertTrue(proportion > 0.1, "La proportion pour " + description + " devrait être élevée");
        } else if (categorieAttendue.equals("normal")) {
            assertTrue(proportion > 0, "La proportion pour " + description + " devrait être positive");
        } else {
            assertEquals(0.0, proportion, "La proportion pour une date null devrait être 0");
        }
    }

    @Test
    @DisplayName("Test spécifique pour juillet et août")
    void testProportionPondereeMoisEte() {
        // Juillet et août doivent avoir une proportion plus élevée
        double proportionJuillet = Entreprise.proportionPondereeDuMois(LocalDate.of(2023, 7, 15));
        double proportionAout = Entreprise.proportionPondereeDuMois(LocalDate.of(2023, 8, 15));
        double proportionJuin = Entreprise.proportionPondereeDuMois(LocalDate.of(2023, 6, 15));

        assertTrue(proportionJuillet > proportionJuin, "Juillet devrait avoir une proportion plus élevée que juin");
        assertTrue(proportionAout > proportionJuin, "Août devrait avoir une proportion plus élevée que juin");
    }
}
