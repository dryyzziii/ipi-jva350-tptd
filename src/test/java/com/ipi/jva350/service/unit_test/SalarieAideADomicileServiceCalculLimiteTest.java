package com.ipi.jva350.service.unit_test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import com.ipi.jva350.service.SalarieAideADomicileService;

@ExtendWith(MockitoExtension.class)
class SalarieAideADomicileServiceCalculLimiteTest {

    @Mock
    private SalarieAideADomicileRepository salarieAideADomicileRepository;
    
    @InjectMocks
    private SalarieAideADomicileService salarieService;
    
    @BeforeEach
    void setUp() {
        // Configurer le mock pour renvoyer une valeur par défaut pour partCongesPrisTotauxAnneeNMoins1
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(0.5);
    }
    
    @Test
    @DisplayName("Test de calcul de limite pour un congé en hiver")
    void testCalculeLimiteCongesHiver() {
        // GIVEN (Arrange) : Configuration pour un congé en hiver
        LocalDate moisEnCours = LocalDate.of(2023, 12, 1);
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        LocalDate moisDebutContrat = LocalDate.of(2020, 1, 1); // 3 ans d'ancienneté
        LocalDate premierJourDeConge = LocalDate.of(2023, 12, 15);
        LocalDate dernierJourDeConge = LocalDate.of(2023, 12, 30);
        
        salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, moisDebutContrat, premierJourDeConge, dernierJourDeConge);
        
        // THEN (Assert) : Vérification que la limite est calculée correctement
        verify(salarieAideADomicileRepository).partCongesPrisTotauxAnneeNMoins1();
    }
    
    @Test
    @DisplayName("Test de l'impact de l'ancienneté sur la limite")
    void testCalculeLimiteAvecAnciennete() {
        // GIVEN (Arrange) : Configuration pour tester l'impact de l'ancienneté
        LocalDate moisEnCours = LocalDate.of(2023, 6, 1);
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        
        // Deux salariés avec des anciennetés différentes
        LocalDate moisDebutContratJunior = LocalDate.of(2022, 1, 1); // 1 an d'ancienneté
        LocalDate moisDebutContratSenior = LocalDate.of(2013, 1, 1); // 10 ans d'ancienneté
        
        LocalDate premierJourDeConge = LocalDate.of(2023, 7, 15);
        LocalDate dernierJourDeConge = LocalDate.of(2023, 7, 30);
        
        // WHEN (Act) : Calcul des limites pour les deux salariés
        long limiteJunior = salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, moisDebutContratJunior, premierJourDeConge, dernierJourDeConge);
        
        long limiteSenior = salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, moisDebutContratSenior, premierJourDeConge, dernierJourDeConge);
        
        // THEN (Assert) : Le senior devrait avoir une limite plus élevée
        assertTrue(limiteSenior > limiteJunior, 
            "Un salarié avec plus d'ancienneté devrait avoir une limite de congés plus élevée");
    }
}