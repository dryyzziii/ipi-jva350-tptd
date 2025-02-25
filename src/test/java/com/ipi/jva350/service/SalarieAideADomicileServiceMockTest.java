package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipi.jva350.exception.SalarieException;
import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;

@ExtendWith(MockitoExtension.class)
class SalarieAideADomicileServiceMockTest {

    @Mock
    private SalarieAideADomicileRepository salarieAideADomicileRepository;

    @InjectMocks
    private SalarieAideADomicileService salarieAideADomicileService;

    private SalarieAideADomicile salarie;

    @BeforeEach
    void setUp() {
        // Préparation d'un salarié mock
        salarie = mock(SalarieAideADomicile.class);
    }

    @Test
    @DisplayName("Exception si le salarié n'a pas droit aux congés payés")
    void testAjouteCongeSalarieSansDroit() {
        // Arrange
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(false);
        LocalDate jourDebut = LocalDate.of(2022, 7, 11);
        LocalDate jourFin = LocalDate.of(2022, 7, 16);

        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertEquals("N'a pas légalement droit à des congés payés !", exception.getMessage());
        verify(salarieAideADomicileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Exception si aucun jour n'est décompté")
    void testAjouteCongeAucunJourDecompte() {
        // Arrange
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        
        // Crée une liste de jours vide
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        
        LocalDate jourDebut = LocalDate.of(2022, 7, 11);
        LocalDate jourFin = LocalDate.of(2022, 7, 16);
        
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);

        // Act & Assert
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        
        assertEquals("Pas besoin de congés !", exception.getMessage());
        verify(salarieAideADomicileRepository, never()).save(any());
    }
}