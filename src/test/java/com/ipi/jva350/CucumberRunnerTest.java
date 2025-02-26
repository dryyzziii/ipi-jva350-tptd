package com.ipi.jva350;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources",
    glue = "com.ipi.jva350",
    plugin = {"pretty"}
)
public class CucumberRunnerTest {
    // Cette classe est vide, elle sert uniquement à configurer l'exécution
}