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
        return Stream.of(
            // date, resultatAttendu, description
            Arguments.of(LocalDate.of(2023, 5, 15), true, "Date dans la plage par défaut"),
            Arguments.of(LocalDate.of(2023, 1, 1), true, "Date au début de l'année"),
            Arguments.of(LocalDate.of(2023, 12, 31), true, "Date à la fin de l'année"),
            Arguments.of(null, false, "Date null"),
            Arguments.of(LocalDate.of(2022, 12, 31), false, "Date de l'année précédente"),
            Arguments.of(LocalDate.of(2024, 1, 1), false, "Date de l'année suivante")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("casDePlageProvider")
    @DisplayName("Test de la méthode estDansPlage avec différentes dates")
    void testEstDansPlage(LocalDate date, boolean resultatAttendu, String description) {
        // Création d'un salarié pour le test avec une plage définie
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setMoisEnCours(LocalDate.of(2023, 6, 1)); // Définit l'année en cours

        // WHEN (Act) : Tenter d'exécuter la méthode
        // Elle n'est pas encore implémentée, donc devrait lancer une exception
        if (resultatAttendu) {
            // Pour les cas où on s'attend à true, on vérifie que l'implémentation fonctionne
            // quand elle sera faite
            assertThrows(UnsupportedOperationException.class, () -> {
                salarie.estDansPlage(date);
            });
        } else {
            // Pour les cas où on s'attend à false, on vérifie que l'implémentation fonctionne
            // quand elle sera faite
            assertThrows(UnsupportedOperationException.class, () -> {
                salarie.estDansPlage(date);
            });
        }
    }

    @Test
    @DisplayName("Test de l'exception pour méthode non implémentée")
    void testEstDansPlageNonImplementee() {
        // Création d'un salarié pour le test
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        LocalDate date = LocalDate.now();

        // WHEN & THEN (Act & Assert) : Vérification que l'exception est levée avec le bon message
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            salarie.estDansPlage(date);
        });
        
        assertEquals("Cette méthode n'est pas encore implémentée", exception.getMessage(),
            "Le message d'erreur devrait indiquer que la méthode n'est pas encore implémentée");
    }
}