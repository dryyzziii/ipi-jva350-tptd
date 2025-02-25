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

import com.ipi.jva350.model.Entreprise;
import com.ipi.jva350.model.SalarieAideADomicile;

public class SalarieAideADomicileParameterizedTest {

    private SalarieAideADomicile salarie;
    
    @BeforeEach
    public void setUp() {
        // Initialisation d'un salarié pour les tests
        salarie = new SalarieAideADomicile();
        salarie.setMoisDebutContrat(LocalDate.of(2021, 1, 1));
        salarie.setMoisEnCours(LocalDate.of(2022, 7, 1));
        // On s'assure que les congés pris sont vides pour chaque test
        salarie.setCongesPayesPris(new LinkedHashSet<>());
    }
    
    /**
     * Fournisseur de données pour les tests paramétrés.
     * Chaque argument contient:
     * - date de début de congé
     * - date de fin de congé
     * - nombre de jours ouvrables décomptés attendus
     * - description du test
     */
    static Stream<Arguments> joursCongeDécomptésProvider() {
        return Stream.of(
            // Cas 1: Weekend uniquement (Dimanche non décompté, Samedi décompté s'il est habituellement travaillé)
            Arguments.of(
                LocalDate.parse("2022-07-02"), // Samedi
                LocalDate.parse("2022-07-03"), // Dimanche
                1, // Le samedi est décompté
                "Weekend avec samedi doit décompter 1 jour"
            ),
            
            // Cas 2: Jours ouvrables (du lundi au samedi)
            Arguments.of(
                LocalDate.parse("2022-07-04"), // Lundi
                LocalDate.parse("2022-07-09"), // Samedi
                6,
                "Semaine complète doit décompter 6 jours (lundi-samedi)"
            ),
            
            // Cas 3: Jours fériés ne sont pas décomptés (14 juillet)
            Arguments.of(
                LocalDate.parse("2022-07-13"), // Mercredi
                LocalDate.parse("2022-07-15"), // Vendredi (14 juillet est férié)
                2,
                "Période incluant un jour férié doit décompter les jours ouvrables hors jour férié"
            ),
            
            // Cas 4: Semaine avec dimanche
            Arguments.of(
                LocalDate.parse("2022-07-11"), // Lundi
                LocalDate.parse("2022-07-17"), // Dimanche
                6,
                "Semaine avec dimanche doit décompter 6 jours (lundi-samedi)"
            ),
            
            // Cas 5: Congé à cheval sur deux semaines
            Arguments.of(
                LocalDate.parse("2022-07-14"), // Jeudi (férié)
                LocalDate.parse("2022-07-19"), // Mardi de la semaine suivante
                4, // Vendredi, Samedi, Lundi, Mardi
                "Congé à cheval sur deux semaines doit décompter les jours ouvrables hors férié"
            ),
            
            // Cas 6: Congé d'un seul jour ouvrable
            Arguments.of(
                LocalDate.parse("2022-07-22"), // Vendredi
                LocalDate.parse("2022-07-22"), // Vendredi
                1,
                "Congé d'un jour ouvrable doit décompter 1 jour"
            ),
            
            // Cas 7: Congé commençant un samedi et finissant un lundi
            Arguments.of(
                LocalDate.parse("2022-07-23"), // Samedi
                LocalDate.parse("2022-07-25"), // Lundi
                2,
                "Congé samedi-lundi doit décompter 2 jours (samedi et lundi)"
            ),
            
            // Cas 8: Période longue avec weekends
            Arguments.of(
                LocalDate.parse("2022-08-01"), // Lundi
                LocalDate.parse("2022-08-14"), // Dimanche (2 semaines)
                12, // (lundi-samedi) x 2
                "Période de 2 semaines doit décompter 12 jours ouvrables (lundi-samedi x 2)"
            ),
            
            // Cas 9: Date de fin avant date de début
            Arguments.of(
                LocalDate.parse("2022-07-15"), // Vendredi
                LocalDate.parse("2022-07-14"), // Jeudi (qui est aussi férié)
                0,
                "Date de fin avant date de début ne décompte aucun jour"
            ),
            
            // Cas 10: Congé après des congés déjà pris
            Arguments.of(
                LocalDate.parse("2022-07-26"), // Mardi
                LocalDate.parse("2022-07-29"), // Vendredi
                4,
                "Congé normal après d'autres congés"
            )
        );
    }
    
    @ParameterizedTest(name = "{3}")
    @MethodSource("joursCongeDécomptésProvider")
    @DisplayName("Test du calcul des jours de congé décomptés")
    public void testCalculeJoursDeCongeDecomptesPourPlage(
            LocalDate jourDebut,
            LocalDate jourFin,
            int nbJoursAttendu,
            String description) {
        
        // Act
        LinkedHashSet<LocalDate> joursDecomptes = salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin);
        
        // Assert
        assertEquals(nbJoursAttendu, joursDecomptes.size(),
                "Le nombre de jours décomptés devrait être " + nbJoursAttendu + " pour " + description);
        
        // Vérification supplémentaire: pas de dimanche dans les jours décomptés
        assertFalse(joursDecomptes.stream().anyMatch(d -> d.getDayOfWeek().getValue() == 7),
                "Aucun dimanche ne devrait être décompté");
    }
    
    @ParameterizedTest(name = "Début le {0}, fin le {1}")
    @MethodSource("joursCongeDécomptésProvider")
    @DisplayName("Test des jours spécifiques décomptés")
    public void testJoursSpecifiquesDecomptes(
            LocalDate jourDebut,
            LocalDate jourFin,
            int nbJoursAttendu,
            String description) {
        
        // Act
        LinkedHashSet<LocalDate> joursDecomptes = salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin);
        
        // Si le test est pour des dates inversées, aucune vérification supplémentaire nécessaire
        if (jourDebut.isAfter(jourFin)) {
            return;
        }
        
        // Assert
        // Vérifier que tous les jours décomptés sont dans la plage
        joursDecomptes.forEach(jour -> {
            assertTrue(
                !jour.isBefore(jourDebut) && !jour.isAfter(jourFin),
                "Le jour décompté " + jour + " doit être entre " + jourDebut + " et " + jourFin
            );
        });
        
        // Vérifier que tous les jours décomptés sont des jours ouvrables (lundi-samedi)
        joursDecomptes.forEach(jour -> {
            int dayOfWeek = jour.getDayOfWeek().getValue();
            assertTrue(dayOfWeek >= 1 && dayOfWeek <= 6,
                "Le jour décompté " + jour + " doit être un jour ouvrable (1-6)");
        });
        
        // Vérifier qu'aucun jour férié n'est décompté
        joursDecomptes.forEach(jour -> {
            assertFalse(Entreprise.estJourFerie(jour),
                "Le jour décompté " + jour + " ne doit pas être un jour férié");
        });
    }
    
    @ParameterizedTest(name = "Début le {0}, fin le {1}")
    @MethodSource("joursCongeDécomptésProvider")
    @DisplayName("Test congés déjà pris et nouveaux congés")
    public void testCongesDejaEtNouveauxConges(
            LocalDate jourDebut,
            LocalDate jourFin,
            int nbJoursAttendu,
            String description) {
            
        // Cas spécial: simuler des congés déjà pris
        if (description.contains("Congé normal après d'autres congés")) {
            // On ajoute un congé précédent
            LinkedHashSet<LocalDate> congesDejaUtilises = new LinkedHashSet<>();
            congesDejaUtilises.add(LocalDate.parse("2022-07-20")); // Mercredi avant la période de test
            salarie.setCongesPayesPris(congesDejaUtilises);
        }
        
        // Act
        LinkedHashSet<LocalDate> joursDecomptes = salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin);
        
        // Assert
        assertEquals(nbJoursAttendu, joursDecomptes.size(),
                "Le nombre de jours décomptés devrait être " + nbJoursAttendu + " pour " + description);
    }
}