package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipi.jva350.exception.SalarieException;
import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;

@SpringBootTest
class SalarieAideADomicileTest {
    @Autowired
    private SalarieAideADomicileService salarieService;

    @Autowired
    private SalarieAideADomicileRepository salarieRepo;

    private SalarieAideADomicile salarie;

    @BeforeEach
    void setUp() {
        // GIVEN (Arrange) : Nettoyage de la base et préparation d'un salarié avec
        // droits aux congés
        // Nettoyer la base de données avant chaque test
        salarieRepo.deleteAll();

        // Créer un salarié qui a droit aux congés payés
        salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisDebutContrat(LocalDate.of(2022, 1, 1));
        salarie.setMoisEnCours(LocalDate.of(2022, 5, 1));
        salarie.setJoursTravaillesAnneeNMoins1(15.0); // Plus de 10 jours pour avoir droit aux congés
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0); // 25 jours de congés acquis
    }

    @Test
    public void testAjouteConge() throws SalarieException {
        // GIVEN (Arrange) : Création du salarié avec des paramètres appropriés
        salarie.setMoisEnCours(LocalDate.of(2022, 6, 1)); // Set the current month to June 1, 2022
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0); // Ensure the employee has enough vacation days from previous
                                                        // year
        salarie.setMoisDebutContrat(LocalDate.of(2021, 1, 1)); // Set contract start date for seniority calculation

        salarieService.creerSalarieAideADomicile(salarie);

        LocalDate jourDebut = LocalDate.of(2022, 6, 2); // 1er juin 2022
        LocalDate jourFin = LocalDate.of(2022, 6, 3); // 3 juin 2022

        // WHEN (Act) : Ajout d'un congé
        salarieService.ajouteConge(salarie, jourDebut, jourFin);

        // Récupérer le salarié mis à jour depuis la base
        SalarieAideADomicile salarieMAJ = salarieRepo.findByNom("Dupont");

        // THEN (Assert) : Vérification des résultats
        assertNotNull(salarieMAJ, "Le salarié devrait exister en base");

        // Vérifier que les congés ont été ajoutés
        Set<LocalDate> congesPris = salarieMAJ.getCongesPayesPris();
        assertNotNull(congesPris, "La liste des congés pris ne devrait pas être null");
        assertFalse(congesPris.isEmpty(), "Des congés devraient avoir été ajoutés");

        // Vérifier le nombre de jours décomptés (mercredi, jeudi, vendredi)
        // Le 1er juin 2022 est un mercredi, le 2 un jeudi, le 3 un vendredi
        assertEquals(3, congesPris.size(), "3 jours ouvrables devraient être décomptés");

        // Vérifier les dates spécifiques
        assertTrue(congesPris.contains(jourDebut), "Le jour de début devrait être dans les congés pris");
        assertTrue(congesPris.contains(jourDebut.plusDays(1)), "Le jour suivant devrait être dans les congés pris");
        assertTrue(congesPris.contains(jourFin), "Le jour de fin devrait être dans les congés pris");

        // Vérifier le décompte des congés
        assertTrue(salarieMAJ.getCongesPayesPrisAnneeNMoins1() > 0,
                "Le compteur de congés pris devrait être incrémenté");
        assertEquals(3, salarieMAJ.getCongesPayesPrisAnneeNMoins1(),
                "3 jours devraient être décomptés des congés");
    }

    @Test
    public void testAjouteCongeAvecDepartWeekEnd() throws SalarieException {
        // GIVEN (Arrange) : Création du salarié en base et définition des dates de
        // congé commençant un weekend
        salarieService.creerSalarieAideADomicile(salarie);
        LocalDate jourDebut = LocalDate.of(2022, 6, 4); // Samedi 4 juin 2022
        LocalDate jourFin = LocalDate.of(2022, 6, 7); // Mardi 7 juin 2022

        // WHEN (Act) : Ajout d'un congé partant d'un weekend
        salarieService.ajouteConge(salarie, jourDebut, jourFin);

        // Récupérer le salarié mis à jour depuis la base
        SalarieAideADomicile salarieMAJ = salarieRepo.findByNom("Dupont");

        // THEN (Assert) : Vérification des résultats
        assertNotNull(salarieMAJ, "Le salarié devrait exister en base");

        // Vérifier que les congés ont été ajoutés
        Set<LocalDate> congesPris = salarieMAJ.getCongesPayesPris();
        assertNotNull(congesPris, "La liste des congés pris ne devrait pas être null");
        assertFalse(congesPris.isEmpty(), "Des congés devraient avoir été ajoutés");

        // Vérifier le nombre de jours décomptés (samedi, lundi, mardi)
        // Le dimanche n'est pas décompté
        assertEquals(1, congesPris.size(), "1 jours ouvrables devraient être décomptés");

        // Vérifier que le dimanche n'est PAS dans les congés pris
        assertFalse(congesPris.contains(LocalDate.of(2022, 6, 5)),
                "Le dimanche ne devrait pas être décompté");
    }

    @Test
    public void testAjouteCongeImpossibleAvantMoisEnCours() {
        // GIVEN (Arrange) : Préparation d'un congé avec dates avant le mois en cours
        try {
            salarieService.creerSalarieAideADomicile(salarie);
            LocalDate jourDebut = LocalDate.of(2022, 4, 1); // Avant le mois en cours (mai 2022)
            LocalDate jourFin = LocalDate.of(2022, 4, 5);

            // WHEN (Act) : Tentative d'ajout de congé avant le mois en cours
            salarieService.ajouteConge(salarie, jourDebut, jourFin);

            // Si on arrive ici, le test échoue
            fail("Une exception aurait dû être levée pour des congés avant le mois en cours");
        } catch (SalarieException e) {
            // THEN (Assert) : Vérification que l'exception contient le bon message
            assertTrue(e.getMessage().contains("avant le mois en cours"),
                    "Le message d'erreur devrait indiquer que les congés sont avant le mois en cours");
        }
    }

    @Test
    public void testAjouteCongeImpossibleSansAssezDeJoursTravailles() {
        // GIVEN (Arrange) : Préparation d'un salarié sans droit aux congés (moins de 10
        // jours travaillés)
        salarie.setJoursTravaillesAnneeNMoins1(5.0); // Moins de 10 jours, pas droit aux congés

        try {
            salarieService.creerSalarieAideADomicile(salarie);
            LocalDate jourDebut = LocalDate.of(2022, 6, 1);
            LocalDate jourFin = LocalDate.of(2022, 6, 3);

            // WHEN (Act) : Tentative d'ajout de congé pour un salarié sans droit
            salarieService.ajouteConge(salarie, jourDebut, jourFin);

            // Si on arrive ici, le test échoue
            fail("Une exception aurait dû être levée pour un salarié sans droit aux congés");
        } catch (SalarieException e) {
            // THEN (Assert) : Vérification que l'exception contient le bon message
            assertTrue(e.getMessage().contains("droit à des congés"),
                    "Le message d'erreur devrait indiquer que le salarié n'a pas droit aux congés");
        }
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
                Arguments.of(-5.0, false, "Valeur négative de jours travaillés"));
    }

    @ParameterizedTest(name = "{2}: {0} jours travaillés => droit aux congés payés: {1}")
    @MethodSource("joursTravaillesProvider")
    @DisplayName("Test du droit aux congés payés selon les jours travaillés")
    void testDroitAuxCongesPayes(double joursTravailles, boolean droitAttendu, String description) {
        // GIVEN (Arrange) : Configuration d'un salarié avec un nombre spécifique de
        // jours travaillés
        salarie.setJoursTravaillesAnneeNMoins1(joursTravailles);

        // WHEN & THEN (Act & Assert) : Vérification que le droit aux congés payés est
        // correct
        assertEquals(droitAttendu, salarie.aLegalementDroitADesCongesPayes(),
                "Pour " + joursTravailles + " jours travaillés (" + description
                        + "), le droit aux congés payés devrait être " + droitAttendu);
    }
}