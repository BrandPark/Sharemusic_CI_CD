package com.brandpark.sharemusic;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = Application.class)
public class DomainDependencyTests {

    private static final String ACCOUNT = "..modules.account..";
    private static final String ALBUM_AND_TRACK = "..modules.album..";
    private static final String COMMENT = "..modules.comment..";
    private static final String FOLLOW = "..modules.follow..";
    private static final String NOTIFICATION = "..modules.notification..";
    private static final String UTIL = "..modules.util..";

    @ArchTest ArchRule accountRule = classes().that().resideInAPackage(ACCOUNT)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(ACCOUNT, ALBUM_AND_TRACK
                    , COMMENT, FOLLOW, NOTIFICATION, "..api..", "..partials..", "..validator..");


}
