package info.solidsoft.gradle.pitest.functional

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import groovy.util.logging.Slf4j
import nebula.test.functional.ExecutionResult
import nebula.test.functional.GradleRunner
import spock.lang.Unroll

import java.util.regex.Pattern

/**
 * TODO: Possible extensions:
 *  - Move functional tests to a separate sourceSet and not run them in every build - DONE
 *  - Add nice gradle.build builder
 *  - Add Connector clean up in tear down in IntegrationSpec - DONE
 *  - Add testing against latest nightly Gradle version?
 */
@Slf4j
class PitestPluginFunctional2Spec extends AbstractPitestFunctionalSpec {

    void setup() {
        daemonMaxIdleTimeInSecondsInMemorySafeMode = 1  //trying to mitigate "Gradle killed" issues with Travis
    }

    @Unroll
    def "should run mutation analysis with Gradle #requestedGradleVersion"() {
        given:
            gradleVersion = requestedGradleVersion
            classpathFilter = Predicates.and(GradleRunner.CLASSPATH_DEFAULT, FILTER_SPOCK_JAR)
        when:
            copyResources("testProjects/simple1", "")
        then:
            fileExists('build.gradle')
        when:
            ExecutionResult result = runTasksSuccessfully('pitest')
        then:
            result.wasExecuted(':pitest')
            result.getStandardOutput().contains('Generated 1 mutations Killed 1 (100%)')
        where:
            requestedGradleVersion << resolveRequestedGradleVersions()
    }

    //To prevent failure when Spock for Groovy 2.4 is run with Groovy 2.3 delivered with Gradle <2.8
    //Spock is not needed in this artificial project - just the test classpath leaks to Gradle instance started by Nebula
    private static final Pattern SPOCK_JAR_PATTERN = Pattern.compile(".*spock-core-1\\..*.jar")
    private static final Predicate<URL> FILTER_SPOCK_JAR = { URL url ->
        return !url.toExternalForm().matches(SPOCK_JAR_PATTERN)
    } as Predicate<URL>

    //TODO: Extract regression tests control mechanism to a separate class (or even better trait) when needed in some other place
    private static final String REGRESSION_TESTS_ENV_NAME = "PITEST_REGRESSION_TESTS"
    private static final List<String> GRADLE3_VERSIONS = ["3.5.1", "3.4.1", "3.3", "3.2", "3.1", "3.0"]
    private static final List<String> GRADLE4_VERSIONS = ["4.1", "4.0.1"]
    private static final List<String> GRADLE_LATEST_VERSIONS = ["2.14.1", GRADLE3_VERSIONS[0], GRADLE4_VERSIONS[0]]
    private static final Range<Integer> GRADLE2_MINOR_RANGE = (14..0)

    private static final Closure gradle2AdditionalVersionModifications = { List<String> versions ->
        versions - "2.2" + "2.2.1" - "2.14" + "2.14.1"
    }

    private static List<String> resolveRequestedGradleVersions() {
        String regressionTestsLevel = System.getenv(REGRESSION_TESTS_ENV_NAME)
        log.debug("$REGRESSION_TESTS_ENV_NAME set to '${regressionTestsLevel}'")
        switch (regressionTestsLevel) {
            case "latestOnly":
            case null:
                return GRADLE_LATEST_VERSIONS
            case "quick":
                return GRADLE_LATEST_VERSIONS + "2.0" + "3.0"
            case "full":
                return GRADLE4_VERSIONS + GRADLE3_VERSIONS + gradle2AdditionalVersionModifications(GRADLE2_MINOR_RANGE.collect { "2.$it" })
            default:
                log.warn("Unsupported $REGRESSION_TESTS_ENV_NAME value '`$regressionTestsLevel`' (expected 'latestOnly', 'quick' or 'full'). " +
                        "Assuming 'latestOnly'.")
                return GRADLE_LATEST_VERSIONS
        }
    }
}
