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
        // GIVEN (Arrange) : Configuration d'un salarié sans droit aux congés payés
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(false);
        LocalDate jourDebut = LocalDate.of(2022, 7, 11);
        LocalDate jourFin = LocalDate.of(2022, 7, 16);
        
        // WHEN & THEN (Act & Assert) : Vérification que l'exception est levée avec le bon message
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        assertEquals("N'a pas légalement droit à des congés payés !", exception.getMessage());
        
        // THEN (Assert) : Vérification que la méthode save n'a jamais été appelée
        verify(salarieAideADomicileRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Exception si aucun jour n'est décompté")
    void testAjouteCongeAucunJourDecompte() {
        // GIVEN (Arrange) : Configuration d'un salarié avec droit aux congés mais aucun jour à décompter
        when(salarie.aLegalementDroitADesCongesPayes()).thenReturn(true);
        // Crée une liste de jours vide
        LinkedHashSet<LocalDate> joursDecomptes = new LinkedHashSet<>();
        LocalDate jourDebut = LocalDate.of(2022, 7, 11);
        LocalDate jourFin = LocalDate.of(2022, 7, 16);
        when(salarie.calculeJoursDeCongeDecomptesPourPlage(jourDebut, jourFin)).thenReturn(joursDecomptes);
        
        // WHEN & THEN (Act & Assert) : Vérification que l'exception est levée avec le bon message
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, jourDebut, jourFin);
        });
        assertEquals("Pas besoin de congés !", exception.getMessage());
        
        // THEN (Assert) : Vérification que la méthode save n'a jamais été appelée
        verify(salarieAideADomicileRepository, never()).save(any());
    }
}