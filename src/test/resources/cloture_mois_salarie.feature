Feature: Clôture de mois pour un salarié aide à domicile
  En tant que gestionnaire RH
  Je veux clôturer le mois en cours d'un salarié aide à domicile
  Afin de préparer sa feuille de paie et mettre à jour ses jours travaillés et congés acquis

  Background: 
    Given un salarié nommé "Martin" avec 0 jours travaillés pour l'année N
    And avec 10 congés payés acquis pour l'année N
    And dont le mois en cours est Avril 2022
    And qui a commencé son contrat en Janvier 2022

  Scenario: Clôture d'un mois sans report à l'année suivante
    When je clôture le mois avec 15 jours travaillés
    Then le mois en cours du salarié devrait être Mai 2022
    And le nombre de jours travaillés pour l'année N devrait être de 15
    And le nombre de congés payés acquis pour l'année N devrait être de 12.5

  Scenario: Clôture du dernier mois de l'année de congés (Mai)
    Given le mois en cours du salarié est Mai 2022
    When je clôture le mois avec 18 jours travaillés
    Then le mois en cours du salarié devrait être Juin 2022
    And le nombre de jours travaillés pour l'année N devrait être de 0
    And le nombre de jours travaillés pour l'année N-1 devrait être de 18
    And le nombre de congés payés acquis pour l'année N devrait être de 0
    And le nombre de congés payés pris pour l'année N-1 devrait être de 0