package com.ipi.jva350.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;

@SpringBootTest
@Transactional
public class SalarieAideADomicileServiceIntegrationTest {

    @Autowired
    private SalarieAideADomicileService salarieService;
    
    @Autowired
    private SalarieAideADomicileRepository salarieRepo;
    
    @BeforeEach
    void setUp() {
        // Nettoyer la base de données avant chaque test
        salarieRepo.deleteAll();
    }
    
    @Test
    @DisplayName("Test d'intégration du calcul de limite avec prise en compte de la moyenne des congés pris")
    void testCalculeLimiteAvecMoyenneConges() {
        // GIVEN (Arrange) : Initialisation de la base avec plusieurs salariés ayant des congés pris
        // Salarié 1: a pris 60% de ses congés
        SalarieAideADomicile salarie1 = new SalarieAideADomicile.Builder("Dupont", 
            LocalDate.of(2020, 1, 1), LocalDate.of(2023, 6, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(15.0)
            .build();
        
        // Salarié 2: a pris 40% de ses congés
        SalarieAideADomicile salarie2 = new SalarieAideADomicile.Builder("Martin", 
            LocalDate.of(2019, 6, 1), LocalDate.of(2023, 6, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(10.0)
            .build();
        
        salarieRepo.save(salarie1);
        salarieRepo.save(salarie2);
        
        // Paramètres pour le calcul de la limite
        LocalDate moisEnCours = LocalDate.of(2023, 7, 1); // Juillet
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        LocalDate moisDebutContrat = LocalDate.of(2020, 1, 1); // 3 ans d'ancienneté
        LocalDate premierJourDeConge = LocalDate.of(2023, 7, 15); // Juillet
        LocalDate dernierJourDeConge = LocalDate.of(2023, 7, 30); // Juillet
        
        // WHEN (Act) : Calcul de la limite avec les données de la base
        long limite = salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, moisDebutContrat, premierJourDeConge, dernierJourDeConge);
        
        // THEN (Assert) : Vérification que la limite est calculée correctement
        // La moyenne des congés pris est de 50%, ce qui devrait influencer la limite
        assertTrue(limite > 0, "La limite calculée devrait être positive");
    }
    
    @Test
    @DisplayName("Test d'intégration du bonus d'ancienneté dans le calcul de limite")
    void testCalculeLimiteAvecBonusAnciennete() {
        // GIVEN (Arrange) : Deux salariés avec des anciennetés différentes mais les mêmes congés
        // Salarié junior (1 an)
        SalarieAideADomicile salarieJunior = new SalarieAideADomicile.Builder("Junior", 
            LocalDate.of(2022, 1, 1), LocalDate.of(2023, 6, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(10.0)
            .build();
        
        // Salarié senior (10 ans)
        SalarieAideADomicile salarieSenior = new SalarieAideADomicile.Builder("Senior", 
            LocalDate.of(2013, 1, 1), LocalDate.of(2023, 6, 1))
            .congesPayesAcquisAnneeNMoins1(25.0)
            .congesPayesPrisAnneeNMoins1(10.0)
            .build();
        
        salarieRepo.save(salarieJunior);
        salarieRepo.save(salarieSenior);
        
        // Paramètres communs
        LocalDate moisEnCours = LocalDate.of(2023, 7, 1);
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        LocalDate premierJourDeConge = LocalDate.of(2023, 7, 15);
        LocalDate dernierJourDeConge = LocalDate.of(2023, 7, 30);
        
        // WHEN (Act) : Calcul des limites pour les deux salariés
        long limiteJunior = salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, salarieJunior.getMoisDebutContrat(), 
                premierJourDeConge, dernierJourDeConge);
        
        long limiteSenior = salarieService.calculeLimiteEntrepriseCongesPermis(
                moisEnCours, congesPayesAcquisAnneeNMoins1, salarieSenior.getMoisDebutContrat(), 
                premierJourDeConge, dernierJourDeConge);
        
        // THEN (Assert) : Le senior devrait avoir une limite plus élevée grâce au bonus d'ancienneté
        assertTrue(limiteSenior > limiteJunior, 
            "Un salarié avec plus d'ancienneté devrait avoir une limite de congés plus élevée");
        
        // La différence devrait être d'environ 9 jours (10 ans - 1 an d'ancienneté, avec un maximum de 10)
        long differenceLimite = limiteSenior - limiteJunior;
        assertTrue(differenceLimite >= 9 && differenceLimite <= 10, 
            "La différence de limite due à l'ancienneté devrait être d'environ 9 jours");
    }
}