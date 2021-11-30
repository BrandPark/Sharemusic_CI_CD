package com.brandpark.sharemusic;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = Application.class)
public class PackageDependencyTests {
    private static final String TEST_UTILS = "..testUtils..";
    private static final String API = "com.brandpark..api..";
    private static final String MODULES = "..modules..";
    private static final String PARTIALS = "..partials..";
    private static final String INFRA = "..infra..";

    @ArchTest ArchRule apiPackageRule = classes().that().resideInAPackage(API)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(API, TEST_UTILS);

    @ArchTest ArchRule modulesPackageRule = classes().that().resideInAPackage(MODULES)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(API, PARTIALS, MODULES, TEST_UTILS);

    @ArchTest ArchRule infraPackageRule = classes().that().resideInAPackage(INFRA)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(INFRA, MODULES, API, PARTIALS, TEST_UTILS);

    @ArchTest ArchRule partialsPackageRule = classes().that().resideInAPackage(PARTIALS)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(PARTIALS);
}
