package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipi.jva350.exception.SalarieException;
import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;

@ExtendWith(MockitoExtension.class)
public class SalarieAideADomicileServiceMockTest {

    @Mock
    private SalarieAideADomicileRepository salarieAideADomicileRepository;

    @Spy
    @InjectMocks
    private SalarieAideADomicileService salarieAideADomicileService;

    private SalarieAideADomicile salarie;
    private LocalDate jourDebut;
    private LocalDate jourFin;

    @BeforeEach
    public void setUp() {
        // Préparation d'un salarié avec ses paramètres
        salarie = spy(new SalarieAideADomicile());
        salarie.setId(1L);
        salarie.setNom("Dupont");
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1)); // 2 ans d'ancienneté
        salarie.setMoisEnCours(LocalDate.of(2022, 7, 1));
        salarie.setCongesPayesAcquisAnneeNMoins1(30.0); // 30 jours acquis l'année précédente
        salarie.setCongesPayesPrisAnneeNMoins1(0); // Aucun jour pris encore
        salarie.setCongesPayesPris(new LinkedHashSet<>());
        salarie.setJoursTravaillesAnneeNMoins1(20); // Plus de 10 jours travaillés

        // Dates de début et fin de congé standard pour les tests
        jourDebut = LocalDate.of(2022, 7, 11); // Lundi
        jourFin = LocalDate.of(2022, 7, 16); // Samedi
    }

    @Test
    @DisplayName("Ajout de congé réussi avec salarié ayant droit aux congés")
    public void testAjouteCongeReussi() throws SalarieException {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            joursDecomptes.add(jourDebut.plusDays(i));
        }
        
        // Mock des méthodes du salarié
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        when(salarie.getCongesPayesRestantAnneeNMoins1()).thenReturn(30.0);
        
        // Mock du service pour le calcul de limite
        doReturn(3L).when(salarieAideADomicileService).calculeLimiteEntrepriseCongesPermis(
                any(), anyDouble(), any(), any(), any());

        // Act
        salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);

        // Assert
        // Vérifier que le salarié a été sauvegardé
        verify(salarieAideADomicileRepository, times(1)).save(salarie);
        
        // Vérifier que les jours de congés ont été ajoutés au salarié
        assertEquals(6, salarie.getCongesPayesPris().size());
        assertEquals(6, salarie.getCongesPayesPrisAnneeNMoins1());
    }

    @Test
    @DisplayName("Exception si le salarié n'a pas droit aux congés payés")
    public void testAjouteCongeSalarieSansDroit() {
        // Arrange
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(false);

        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertEquals("N'a pas légalement droit à des congés payés !", exception.getMessage());
        verify(salarieAideADomicileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exception si aucun jour n'est décompté")
    public void testAjouteCongeAucunJourDecompte() {
        // Arrange
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(any(), any()))
                .thenReturn(new LinkedHashSet<>());

        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertEquals("Pas besoin de congés !", exception.getMessage());
        verify(salarieAideADomicileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exception si congé est avant le mois en cours")
    public void testAjouteCongeAvantMoisEnCours() {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        LocalDate jourPasse = LocalDate.of(2022, 6, 1); // Avant le mois en cours (juillet)
        joursDecomptes.add(jourPasse);
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(any(), any()))
                .thenReturn(joursDecomptes);
        when(salarie.getMoisEnCours()).thenReturn(LocalDate.of(2022, 7, 1));

        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, 
                    LocalDate.of(2022, 6, 1), 
                    LocalDate.of(2022, 6, 5));
        });
        
        assertEquals("Pas possible de prendre de congé avant le mois en cours !", exception.getMessage());
        verify(salarieAideADomicileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exception si congé dépasse les jours acquis en N-1")
    public void testAjouteCongeDepasseLesJoursAcquis() {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            joursDecomptes.add(jourDebut.plusDays(i));
        }
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        when(salarie.getCongesPayesRestantAnneeNMoins1()).thenReturn(5.0); // Seulement 5 jours disponibles
        
        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertTrue(exception.getMessage().contains("dépassent les congés acquis en année N-1"));
        verify(salarieAideADomicileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exception si congé dépasse la limite de l'entreprise")
    public void testAjouteCongeDepasseLimiteEntreprise() {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            joursDecomptes.add(jourDebut.plusDays(i));
        }
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        when(salarie.getCongesPayesRestantAnneeNMoins1()).thenReturn((double) 30);
        
        // La limite est de 7, mais le message d'erreur indique qu'on dépasse si c'est inférieur (logique inversée)
        doReturn(7L).when(salarieAideADomicileService).calculeLimiteEntrepriseCongesPermis(
                any(), anyDouble(), any(), any(), any());
        
        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertTrue(exception.getMessage().contains("dépassent la limite des règles de l'entreprise"));
        verify(salarieAideADomicileRepository, never()).save(any());
    }

     @Test
    @DisplayName("Exception si congé est dans l'année suivante (au-delà du premier jour)")
    public void testAjouteCongeAnneeN1() {
        // Arrange
        when(salarie.getMoisEnCours()).thenReturn(LocalDate.of(2022, 5, 15)); // Mois en cours: mai 2022
        
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        // Ajouter des jours dans l'année suivante (au-delà du 31 mai qui est la fin de l'année de congés)
        LocalDate jour1 = LocalDate.of(2022, 6, 1);
        LocalDate jour2 = LocalDate.of(2022, 6, 2);
        LocalDate jour3 = LocalDate.of(2022, 6, 3);
        joursDecomptes.add(jour1);
        joursDecomptes.add(jour2);
        joursDecomptes.add(jour3);
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(any(), any()))
                .thenReturn(joursDecomptes);
                
        // Pour ce test, on doit utiliser une approche différente puisque la méthode est statique
        // On se contente de configurer le test pour qu'il reflète le scénario souhaité
        
        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, 
                    LocalDate.of(2022, 6, 1), 
                    LocalDate.of(2022, 6, 3));
        });
        
        assertTrue(exception.getMessage().contains("Pas possible de prendre de congé dans l'année de congés suivante"));
        verify(salarieAideADomicileRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Vérifie que calculeLimiteEntrepriseCongesPermis est appelé avec les bons paramètres")
    public void testCalculeLimiteEntrepriseCongesPermisParameters() throws SalarieException {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            joursDecomptes.add(jourDebut.plusDays(i));
        }
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        when(salarie.getCongesPayesRestantAnneeNMoins1()).thenReturn(30.0); 
        
        // Mock pour que le test passe la vérification de limite
        doReturn(3L).when(salarieAideADomicileService).calculeLimiteEntrepriseCongesPermis(
                any(), anyDouble(), any(), any(), any());
        
        // Act
        salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        
        // Assert
        // Vérifier que calculeLimiteEntrepriseCongesPermis est appelé avec les bons paramètres
        verify(salarieAideADomicileService).calculeLimiteEntrepriseCongesPermis(
                salarie.getMoisEnCours(),
                salarie.getCongesPayesAcquisAnneeNMoins1(),
                salarie.getMoisDebutContrat(),
                jourDebut,
                jourFin
        );
    }
    
    @Test
    @DisplayName("Vérifie que le repository save est appelé avec le bon salarié")
    public void testRepositorySaveCalledWithCorrectEmployee() throws SalarieException {
        // Arrange
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        for (int i = 0; i < 6; i++) {
            joursDecomptes.add(jourDebut.plusDays(i));
        }
        
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        when(salarie.getCongesPayesRestantAnneeNMoins1()).thenReturn(30.0);
        
        doReturn(3L).when(salarieAideADomicileService).calculeLimiteEntrepriseCongesPermis(
                any(), anyDouble(), any(), any(), any());
        
        // Act
        salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        
        // Assert
        // Capturer l'argument passé à save pour vérifier qu'il s'agit du salarié attendu
        ArgumentCaptor<SalarieAideADomicile> salarieCaptor = ArgumentCaptor.forClass(SalarieAideADomicile.class);
        verify(salarieAideADomicileRepository).save(salarieCaptor.capture());
        
        // Vérifier que c'est bien notre salarié qui a été sauvegardé
        assertSame(salarie, salarieCaptor.getValue());
        
        // Vérifier que les jours de congés ont été correctement ajoutés
        assertEquals(6, salarie.getCongesPayesPris().size());
        assertEquals(6, salarie.getCongesPayesPrisAnneeNMoins1());
    }
}