package com.ipi.jva350.service.unit_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ipi.jva350.model.SalarieAideADomicile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SalarieAideADomicilePlageTest {

    @Test
    @DisplayName("Test de estDansPlage avec moisEnCours null")
    void testEstDansPlageAvecMoisEnCoursNull() {
        // GIVEN (Arrange) : Création d'un salarié avec moisEnCours null
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setMoisEnCours(null);
        LocalDate date = LocalDate.now();

        // WHEN (Act) : Appel de la méthode
        boolean resultat = salarie.estDansPlage(date);

        // THEN (Assert) : La méthode devrait retourner false sans lancer d'exception
        assertFalse(resultat, "La méthode devrait retourner false lorsque moisEnCours est null");
    }
}
