package com.ipi.jva350.model;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class SalarieAideADomicile {

    public static final float CONGES_PAYES_ACQUIS_PAR_MOIS = 2.5f;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    protected static final List<DayOfWeek> joursHabituellementTravailles = new ArrayList<>();

    static {
        joursHabituellementTravailles.add(DayOfWeek.MONDAY);
        joursHabituellementTravailles.add(DayOfWeek.TUESDAY);
        joursHabituellementTravailles.add(DayOfWeek.WEDNESDAY);
        joursHabituellementTravailles.add(DayOfWeek.THURSDAY);
        joursHabituellementTravailles.add(DayOfWeek.FRIDAY);
    }

    private LocalDate moisEnCours;
    private LocalDate moisDebutContrat;

    private double joursTravaillesAnneeN = 0;
    private double congesPayesAcquisAnneeN = 0;

    /** en année N sur l'acquis N-1 */
    @Convert(converter = LinkedHashSetStringConverter.class)
    @Column
    private Set<LocalDate> congesPayesPris = new LinkedHashSet<>();
    private double joursTravaillesAnneeNMoins1 = 0;
    private double congesPayesAcquisAnneeNMoins1 = 0;
    private double congesPayesPrisAnneeNMoins1 = 0;

    /**
     * Constructeur par défaut pour JPA
     */
    public SalarieAideADomicile() {
    }

    /**
     * Constructeur privé utilisé par le Builder
     */
    private SalarieAideADomicile(Builder builder) {
        this.nom = builder.nom;
        this.moisDebutContrat = builder.moisDebutContrat;
        this.moisEnCours = builder.moisEnCours;
        this.joursTravaillesAnneeNMoins1 = builder.joursTravaillesAnneeNMoins1;
        this.congesPayesAcquisAnneeNMoins1 = builder.congesPayesAcquisAnneeNMoins1;
        this.congesPayesPrisAnneeNMoins1 = builder.congesPayesPrisAnneeNMoins1;
        this.joursTravaillesAnneeN = builder.joursTravaillesAnneeN;
        this.congesPayesAcquisAnneeN = builder.congesPayesAcquisAnneeN;
    }

    /**
     * Builder pour SalarieAideADomicile
     */
    public static class Builder {
        private String nom;
        private LocalDate moisDebutContrat;
        private LocalDate moisEnCours;
        private double joursTravaillesAnneeN = 0;
        private double congesPayesAcquisAnneeN = 0;
        private double joursTravaillesAnneeNMoins1 = 0;
        private double congesPayesAcquisAnneeNMoins1 = 0;
        private double congesPayesPrisAnneeNMoins1 = 0;

        /**
         * Constructeur du Builder avec les paramètres obligatoires
         * 
         * @param nom              Nom du salarié
         * @param moisDebutContrat Date de début du contrat
         * @param moisEnCours      Mois en cours
         */
        public Builder(String nom, LocalDate moisDebutContrat, LocalDate moisEnCours) {
            this.nom = nom;
            this.moisDebutContrat = moisDebutContrat;
            this.moisEnCours = moisEnCours;
        }

        /**
         * Définit les jours travaillés en année N
         * 
         * @param joursTravaillesAnneeN Jours travaillés en année N
         * @return Builder
         */
        public Builder joursTravaillesAnneeN(double joursTravaillesAnneeN) {
            this.joursTravaillesAnneeN = joursTravaillesAnneeN;
            return this;
        }

        /**
         * Définit les congés payés acquis en année N
         * 
         * @param congesPayesAcquisAnneeN Congés payés acquis en année N
         * @return Builder
         */
        public Builder congesPayesAcquisAnneeN(double congesPayesAcquisAnneeN) {
            this.congesPayesAcquisAnneeN = congesPayesAcquisAnneeN;
            return this;
        }

        /**
         * Définit les jours travaillés en année N-1
         * 
         * @param joursTravaillesAnneeNMoins1 Jours travaillés en année N-1
         * @return Builder
         */
        public Builder joursTravaillesAnneeNMoins1(double joursTravaillesAnneeNMoins1) {
            this.joursTravaillesAnneeNMoins1 = joursTravaillesAnneeNMoins1;
            return this;
        }

        /**
         * Définit les congés payés acquis en année N-1
         * 
         * @param congesPayesAcquisAnneeNMoins1 Congés payés acquis en année N-1
         * @return Builder
         */
        public Builder congesPayesAcquisAnneeNMoins1(double congesPayesAcquisAnneeNMoins1) {
            this.congesPayesAcquisAnneeNMoins1 = congesPayesAcquisAnneeNMoins1;
            return this;
        }

        /**
         * Définit les congés payés pris en année N-1
         * 
         * @param congesPayesPrisAnneeNMoins1 Congés payés pris en année N-1
         * @return Builder
         */
        public Builder congesPayesPrisAnneeNMoins1(double congesPayesPrisAnneeNMoins1) {
            this.congesPayesPrisAnneeNMoins1 = congesPayesPrisAnneeNMoins1;
            return this;
        }

        /**
         * Construit l'instance SalarieAideADomicile
         * 
         * @return instance de SalarieAideADomicile
         */
        public SalarieAideADomicile build() {
            return new SalarieAideADomicile(this);
        }
    }

    /**
     * Get the first day of the leave year based on the given date.
     * 
     * @param d Date to calculate from
     * @return First day of the leave year
     */
    public static LocalDate getPremierJourAnneeDeConges(LocalDate d) {
        if (d == null) {
            return null;
        }

        int month = d.getMonthValue();
        LocalDate firstDayOfYear;

        if (month >= 6) {
            firstDayOfYear = LocalDate.of(d.getYear(), 6, 1);
        } else {
            firstDayOfYear = LocalDate.of(d.getYear() - 1, 6, 1);
        }

        return firstDayOfYear;
    }

    /**
     * Vérifie si une date donnée est dans la plage de l'année de congés en cours.
     * Pour cette implémentation, nous considérons qu'une date est dans la plage
     * si elle fait partie de l'année de congés en cours (du 1er juin au 31 mai
     * suivant).
     * 
     * @param date Date à vérifier
     * @return true si la date est dans l'année de congés en cours, false sinon
     */
    public boolean estDansPlage(LocalDate date) {
        if (date == null || this.moisEnCours == null) {
            return false;
        }

        // Récupérer le premier jour de l'année de congés en cours
        LocalDate debutAnneeConges = getPremierJourAnneeDeConges(this.moisEnCours);

        // Vérifier si debutAnneeConges est null (cas où moisEnCours serait mal
        // initialisé)
        if (debutAnneeConges == null) {
            return false;
        }

        // L'année de congés se termine le 31 mai de l'année suivante
        LocalDate finAnneeConges = LocalDate.of(debutAnneeConges.getYear() + 1, 5, 31);

        // Utiliser la méthode statique Entreprise.estDansPlage pour vérifier si la date
        // est dans l'intervalle
        return Entreprise.estDansPlage(date, debutAnneeConges, finAnneeConges);
    }

    /**
     * D'après
     * https://femme-de-menage.ooreka.fr/comprendre/conges-payes-femme-de-menage :
     * Pour s'ouvrir des droits à congés payés – capitalisation de jours + prise
     * et/ou paiement – l'aide ménagère doit avoir travaillé pour le particulier
     * employeur :
     * pendant au moins dix jours (pas forcément de suite) ;
     * à l'intérieur d'une période de temps – dite de « référence » – allant du 1er
     * juin de l'année N au 31 mai de l'année N - 1.
     * NB. on considère que la précédente ligne est correcte d'un point de vue des
     * spécifications métier
     * bien que l'originale dans le lien dit "N+1" au lieu de "N-1"
     * 
     * @return true if the employee is legally entitled to paid leave, false
     *         otherwise
     */
    public boolean aLegalementDroitADesCongesPayes() {
        return this.getJoursTravaillesAnneeNMoins1() > 10;
    }

    /**
     * Calculate the days of leave counted for a time range.
     * 
     * @param dateDebut Start date
     * @param dateFin   End date
     * @return Set of dates representing days of leave counted, ordered
     */
    public Set<LocalDate> calculeJoursDeCongeDecomptesPourPlage(LocalDate dateDebut, LocalDate dateFin) {
        LinkedHashSet<LocalDate> joursDeCongeDecomptes = new LinkedHashSet<>();

        if (dateDebut.isAfter(dateFin)) {
            return joursDeCongeDecomptes;
        }

        LocalDate dernierJourDeCongePris = this.getCongesPayesPris().isEmpty() ? null
                : this.getCongesPayesPris().stream().reduce((first, second) -> second).get();

        dateDebut = (dernierJourDeCongePris == null || dernierJourDeCongePris.isAfter(dateDebut)) ? dateDebut
                : dateDebut.plusDays(1);

        LocalDate jour = dateDebut;
        if (dateDebut.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                && !Entreprise.estJourFerie(dateDebut) && estHabituellementTravaille(dateDebut)) {
            joursDeCongeDecomptes.add(dateDebut);
        }
        for (jour = jour.plusDays(1); jour.minusDays(1).isBefore(dateFin)
                || (!estHabituellementTravaille(jour) && estJourOuvrable(jour)); jour = jour.plusDays(1)) {
            if (jour.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                    && !Entreprise.estJourFerie(jour)) {
                joursDeCongeDecomptes.add(jour);
            }
        }
        return joursDeCongeDecomptes;
    }

    /**
     * Check if a day is a working day (not Sunday and not a holiday).
     * 
     * @param jour Day to check
     * @return true if it's a working day, false otherwise
     */
    public boolean estJourOuvrable(LocalDate jour) {
        return jour.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                && !Entreprise.estJourFerie(jour);
    }

    /**
     * Check if a day is usually worked.
     * 
     * @param jour Day to check
     * @return true if it's usually worked, false otherwise
     */
    public boolean estHabituellementTravaille(LocalDate jour) {
        return joursHabituellementTravailles.contains(jour.getDayOfWeek());
    }

    // Getters et setters inchangés...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public SalarieAideADomicile setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public double getJoursTravaillesAnneeN() {
        return joursTravaillesAnneeN;
    }

    public void setJoursTravaillesAnneeN(double joursTravaillesAnneeN) {
        this.joursTravaillesAnneeN = joursTravaillesAnneeN;
    }

    public double getCongesPayesAcquisAnneeN() {
        return congesPayesAcquisAnneeN;
    }

    public void setCongesPayesAcquisAnneeN(double congesPayesAcquisAnneeN) {
        this.congesPayesAcquisAnneeN = congesPayesAcquisAnneeN;
    }

    public Set<LocalDate> getCongesPayesPris() {
        return congesPayesPris;
    }

    public void setCongesPayesPris(Set<LocalDate> congesPayesPris) {
        this.congesPayesPris = congesPayesPris;
    }

    public double getJoursTravaillesAnneeNMoins1() {
        return joursTravaillesAnneeNMoins1;
    }

    public void setJoursTravaillesAnneeNMoins1(double joursTravaillesAnneeNMoins1) {
        this.joursTravaillesAnneeNMoins1 = joursTravaillesAnneeNMoins1;
    }

    public double getCongesPayesRestantAnneeNMoins1() {
        return this.congesPayesAcquisAnneeNMoins1 - this.getCongesPayesPrisAnneeNMoins1();
    }

    public double getCongesPayesAcquisAnneeNMoins1() {
        return congesPayesAcquisAnneeNMoins1;
    }

    public void setCongesPayesAcquisAnneeNMoins1(double congesPayesAcquisAnneeNMoins1) {
        this.congesPayesAcquisAnneeNMoins1 = congesPayesAcquisAnneeNMoins1;
    }

    public double getCongesPayesPrisAnneeNMoins1() {
        return congesPayesPrisAnneeNMoins1;
    }

    public void setCongesPayesPrisAnneeNMoins1(double congesPayesPrisAnneeNMoins1) {
        this.congesPayesPrisAnneeNMoins1 = congesPayesPrisAnneeNMoins1;
    }

    public LocalDate getMoisEnCours() {
        return moisEnCours;
    }

    public void setMoisEnCours(LocalDate moisEnCours) {
        this.moisEnCours = moisEnCours;
    }

    public LocalDate getMoisDebutContrat() {
        return moisDebutContrat;
    }

    public void setMoisDebutContrat(LocalDate moisDebutContrat) {
        this.moisDebutContrat = moisDebutContrat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SalarieAideADomicile))
            return false;
        SalarieAideADomicile s = (SalarieAideADomicile) o;
        return Objects.equals(id, s.id) &&
                Objects.equals(nom, s.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }
}