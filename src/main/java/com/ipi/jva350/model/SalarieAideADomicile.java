package com.ipi.jva350.model;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Entity
public class SalarieAideADomicile {

    public static final float CONGES_PAYES_ACQUIS_PAR_MOIS = 2.5f;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    public static final List<DayOfWeek> joursHabituellementTravailles = new ArrayList<>();

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
    private LinkedHashSet<LocalDate> congesPayesPris = new LinkedHashSet<>();
    private double joursTravaillesAnneeNMoins1 = 0;
    private double congesPayesAcquisAnneeNMoins1 = 0;
    private double congesPayesPrisAnneeNMoins1 = 0;

    public SalarieAideADomicile() {
    }

    /**
     * Constructor for SalarieAideADomicile.
     * @param nom Name of the employee
     * @param moisDebutContrat Start date of the contract
     * @param moisEnCours Current month
     * @param joursTravaillesAnneeN Days worked in year N
     * @param congesPayesAcquisAnneeN Paid leaves acquired in year N
     * @param joursTravaillesAnneeNMoins1 Days worked in year N-1
     * @param congesPayesAcquisAnneeNMoins1 Paid leaves acquired in year N-1
     * @param congesPayesPrisAnneeNMoins1 Paid leaves taken in year N-1
     */
    public SalarieAideADomicile(String nom, LocalDate moisDebutContrat, LocalDate moisEnCours,
                                double joursTravaillesAnneeN, double congesPayesAcquisAnneeN,
                                double joursTravaillesAnneeNMoins1, double congesPayesAcquisAnneeNMoins1, 
                                double congesPayesPrisAnneeNMoins1) {
        this.nom = nom;
        this.moisDebutContrat = moisDebutContrat;
        this.moisEnCours = moisEnCours;
        this.joursTravaillesAnneeNMoins1 = joursTravaillesAnneeNMoins1;
        this.congesPayesAcquisAnneeNMoins1 = congesPayesAcquisAnneeNMoins1;
        this.congesPayesPrisAnneeNMoins1 = congesPayesPrisAnneeNMoins1;
        this.joursTravaillesAnneeN = joursTravaillesAnneeN;
        this.congesPayesAcquisAnneeN = congesPayesAcquisAnneeN;
    }

    /**
     * Get the first day of the leave year based on the given date.
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
     * Check if a date is within a specific range.
     * @param date Date to check
     * @return true if the date is within the range, false otherwise
     * @throws UnsupportedOperationException This method is not implemented yet
     */
    public static boolean estDansPlage(LocalDate date) {
        throw new UnsupportedOperationException("Cette méthode n'est pas encore implémentée");
    }

    /**
     * D'après https://femme-de-menage.ooreka.fr/comprendre/conges-payes-femme-de-menage :
     * Pour s'ouvrir des droits à congés payés – capitalisation de jours + prise et/ou paiement – l'aide ménagère doit avoir travaillé pour le particulier employeur :
     *     pendant au moins dix jours (pas forcément de suite) ;
     *     à l'intérieur d'une période de temps – dite de « référence » – allant du 1er juin de l'année N au 31 mai de l'année N - 1.
     * NB. on considère que la précédente ligne est correcte d'un point de vue des spécifications métier
     * bien que l'originale dans le lien dit "N+1" au lieu de "N-1"
     * @return true if the employee is legally entitled to paid leave, false otherwise
     */
    public boolean aLegalementDroitADesCongesPayes() {
        return this.getJoursTravaillesAnneeNMoins1() > 10;
    }

    /**
     * Calculate the days of leave counted for a time range.
     * @param dateDebut Start date
     * @param dateFin End date
     * @return Set of dates representing days of leave counted, ordered
     */
    public LinkedHashSet<LocalDate> calculeJoursDeCongeDecomptesPourPlage(LocalDate dateDebut, LocalDate dateFin) {
        LinkedHashSet<LocalDate> joursDeCongeDecomptes = new LinkedHashSet<>();

        if (dateDebut.isAfter(dateFin)) {
            return joursDeCongeDecomptes;
        }

        LocalDate dernierJourDeCongePris = this.getCongesPayesPris().isEmpty() ? null
                : this.getCongesPayesPris().stream().reduce((first, second) -> second).get();

        dateDebut = (dernierJourDeCongePris == null || dernierJourDeCongePris.isAfter(dateDebut)) ?
                dateDebut : dateDebut.plusDays(1);

        LocalDate jour = dateDebut;
        if (dateDebut.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                    && !Entreprise.estJourFerie(dateDebut) && estHabituellementTravaille(dateDebut)) {
            joursDeCongeDecomptes.add(dateDebut);
        }
        for (jour = jour.plusDays(1) ; jour.minusDays(1).isBefore(dateFin)
                || (!estHabituellementTravaille(jour) && estJourOuvrable(jour));
             jour = jour.plusDays(1)) {
            if (jour.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                    && !Entreprise.estJourFerie(jour)) {
                joursDeCongeDecomptes.add(jour);
            }
        }
        return joursDeCongeDecomptes;
    }
    
    /**
     * Check if a day is a working day (not Sunday and not a holiday).
     * @param jour Day to check
     * @return true if it's a working day, false otherwise
     */
    public boolean estJourOuvrable(LocalDate jour) {
        return jour.getDayOfWeek().getValue() != DayOfWeek.SUNDAY.getValue()
                && !Entreprise.estJourFerie(jour);
    }
    
    /**
     * Check if a day is usually worked.
     * @param jour Day to check
     * @return true if it's usually worked, false otherwise
     */
    public boolean estHabituellementTravaille(LocalDate jour) {
        return joursHabituellementTravailles.contains(jour.getDayOfWeek());
    }

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

    public LinkedHashSet<LocalDate> getCongesPayesPris() {
        return congesPayesPris;
    }

    public void setCongesPayesPris(LinkedHashSet<LocalDate> congesPayesPris) {
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
        if (this == o) return true;
        if (!(o instanceof SalarieAideADomicile)) return false;
        SalarieAideADomicile s = (SalarieAideADomicile) o;
        return Objects.equals(id, s.id) &&
                Objects.equals(nom, s.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }
}