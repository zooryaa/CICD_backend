package com.example.demo.arch_tests;

import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class ArchitectureTest {
    @Test
    void layeresDependCorrectly() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.demo");

        ArchRule onlyAllowControllersConfigurationAndServiceToDependOnServices = classes().that().areAnnotatedWith(Service.class)
                .should().onlyBeAccessed().byClassesThat().areAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith(Service.class).orShould().haveNameMatching(".*Service*");
        onlyAllowControllersConfigurationAndServiceToDependOnServices.check(importedClasses);
    }
}