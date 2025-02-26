package com.ipi.jva350;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import com.ipi.jva350.service.SalarieAideADomicileService;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@SpringBootTest
public class ClotureStepDefinitions extends SpringCucumberIntegrationTest {

    @Autowired
    private SalarieAideADomicileService salarieService;
    
    @Autowired
    private SalarieAideADomicileRepository salarieRepository;
    
    private SalarieAideADomicile salarie;
    
    @Before
    public void setup() {
        // Suppression des données de test précédentes si nécessaire
        salarieRepository.deleteAll();
    }
    
    @After
    public void cleanup() {
        // Nettoyage après test
        salarieRepository.deleteAll();
    }
    
    private Month getFrenchMonth(String frenchMonth) {
        switch (frenchMonth.toUpperCase()) {
            case "JANVIER": return Month.JANUARY;
            case "FEVRIER": return Month.FEBRUARY;
            case "MARS": return Month.MARCH;
            case "AVRIL": return Month.APRIL;
            case "MAI": return Month.MAY;
            case "JUIN": return Month.JUNE;
            case "JUILLET": return Month.JULY;
            case "AOUT": return Month.AUGUST;
            case "SEPTEMBRE": return Month.SEPTEMBER;
            case "OCTOBRE": return Month.OCTOBER;
            case "NOVEMBRE": return Month.NOVEMBER;
            case "DECEMBRE": return Month.DECEMBER;
            default: throw new IllegalArgumentException("Mois inconnu: " + frenchMonth);
        }
    }
    
    @Given("un salarié nommé {string} avec {double} jours travaillés pour l'année N")
    public void un_salarie_nomme_avec_jours_travailles(String nom, double joursTravailles) {
        salarie = new SalarieAideADomicile();
        salarie.setNom(nom);
        salarie.setJoursTravaillesAnneeN(joursTravailles);
    }
    
    @Given("avec {double} congés payés acquis pour l'année N")
    public void avec_conges_payes_acquis_pour_l_annee_n(double congesAcquis) {
        salarie.setCongesPayesAcquisAnneeN(congesAcquis);
    }
    
    @Given("dont le mois en cours est {word} {int}")
    public void dont_le_mois_en_cours_est(String mois, Integer annee) {
        Month month = getFrenchMonth(mois);
        LocalDate date = LocalDate.of(annee, month, 1);
        salarie.setMoisEnCours(date);
    }
    
    @Given("qui a commencé son contrat en {word} {int}")
    public void qui_a_commence_son_contrat_en(String mois, Integer annee) {
        Month month = getFrenchMonth(mois);
        LocalDate date = LocalDate.of(annee, month, 1);
        salarie.setMoisDebutContrat(date);
        
        // À ce stade, le salarié est complètement défini, nous pouvons le sauvegarder
        salarieRepository.save(salarie);
    }
    
    @Given("le mois en cours du salarié est {word} {int}")
    public void le_mois_en_cours_du_salarie_est(String mois, Integer annee) {
        Month month = getFrenchMonth(mois);
        LocalDate date = LocalDate.of(annee, month, 1);
        salarie.setMoisEnCours(date);
        salarieRepository.save(salarie);
    }
    
    @When("je clôture le mois avec {double} jours travaillés")
    public void je_cloture_le_mois_avec_jours_travailles(double joursTravailles) {
        // Récupérer la version la plus récente du salarié depuis la base de données
        salarie = salarieRepository.findByNom(salarie.getNom());
        
        // Clôturer le mois
        salarieService.clotureMois(salarie, joursTravailles);
        
        // Récupérer à nouveau le salarié mis à jour
        salarie = salarieRepository.findByNom(salarie.getNom());
    }
    
    @Then("le mois en cours du salarié devrait être {word} {int}")
    public void le_mois_en_cours_du_salarie_devrait_etre(String mois, Integer annee) {
        Month expectedMonth = getFrenchMonth(mois);
        LocalDate expectedDate = LocalDate.of(annee, expectedMonth, 1);
        
        assertEquals(expectedMonth, salarie.getMoisEnCours().getMonth(), 
            "Le mois en cours devrait être " + mois);
        assertEquals(expectedDate.getYear(), salarie.getMoisEnCours().getYear(), 
            "L'année du mois en cours devrait être " + annee);
    }
    
    @Then("le nombre de jours travaillés pour l'année N devrait être de {double}")
    public void le_nombre_de_jours_travailles_pour_l_annee_n_devrait_etre_de(double joursTravailles) {
        assertEquals(joursTravailles, salarie.getJoursTravaillesAnneeN(), 0.01, 
            "Le nombre de jours travaillés pour l'année N devrait être " + joursTravailles);
    }
    
    @Then("le nombre de congés payés acquis pour l'année N devrait être de {double}")
    public void le_nombre_de_conges_payes_acquis_pour_l_annee_n_devrait_etre_de(double congesAcquis) {
        assertEquals(congesAcquis, salarie.getCongesPayesAcquisAnneeN(), 0.01, 
            "Le nombre de congés payés acquis pour l'année N devrait être " + congesAcquis);
    }
    
    @Then("le nombre de jours travaillés pour l'année N-1 devrait être de {double}")
    public void le_nombre_de_jours_travailles_pour_l_annee_n_moins_1_devrait_etre_de(double joursTravailles) {
        assertEquals(joursTravailles, salarie.getJoursTravaillesAnneeNMoins1(), 0.01, 
            "Le nombre de jours travaillés pour l'année N-1 devrait être " + joursTravailles);
    }
    
    @Then("le nombre de congés payés acquis pour l'année N-1 devrait être de {double}")
    public void le_nombre_de_conges_payes_acquis_pour_l_annee_n_moins_1_devrait_etre_de(double congesAcquis) {
        assertEquals(congesAcquis, salarie.getCongesPayesAcquisAnneeNMoins1(), 0.01, 
            "Le nombre de congés payés acquis pour l'année N-1 devrait être " + congesAcquis);
    }
    
    @Then("le nombre de congés payés pris pour l'année N-1 devrait être de {double}")
    public void le_nombre_de_conges_payes_pris_pour_l_annee_n_moins_1_devrait_etre_de(double congesPris) {
        assertEquals(congesPris, salarie.getCongesPayesPrisAnneeNMoins1(), 0.01, 
            "Le nombre de congés payés pris pour l'année N-1 devrait être " + congesPris);
    }
}