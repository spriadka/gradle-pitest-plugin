/* Copyright (c) 2012 Marcin Zajączkowski
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.solidsoft.gradle.pitest

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.UnionFileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaBasePlugin

/**
 * The main class for Pitest plugin.
 */
class PitestPlugin implements Plugin<Project> {
    public final static String DEFAULT_PITEST_VERSION = '1.2.2'
    public final static String PITEST_TASK_GROUP = "Report"
    public final static String PITEST_TASK_NAME = "pitest"
    public final static String SCM_PITEST_TASK_NAME = "scmPitest"
    public final static String PITEST_CONFIGURATION_NAME = 'pitest'

    private final static List<String> DYNAMIC_LIBRARY_EXTENSIONS = ['so', 'dll', 'dylib']
    private final static List<String> FILE_EXTENSIONS_TO_FILTER_FROM_CLASSPATH = ['pom'] + DYNAMIC_LIBRARY_EXTENSIONS

    private final static Logger log =  Logging.getLogger(PitestPlugin)

    @PackageScope   //visible for testing
    final static String PIT_HISTORY_DEFAULT_FILE_NAME = 'pitHistory.txt'
    private final static String PIT_ADDITIONAL_CLASSPATH_DEFAULT_FILE_NAME = "pitClasspath"

    private Project project
    private PitestPluginExtension extension
    private ScmPitestPluginExtension scmExtension

    void apply(Project project) {
        this.project = project
        applyRequiredJavaPlugin()
        createConfigurations()
        createExtension(project)
        createScmExtension(project)
        project.plugins.withType(JavaBasePlugin) {
            PitestTask task = project.tasks.create(PITEST_TASK_NAME, PitestTask)
            task.with {
                description = "Run PIT analysis for java classes"
                group = PITEST_TASK_GROUP
            }
            ScmPitestTask scmPitestTask = project.tasks.create(SCM_PITEST_TASK_NAME, ScmPitestTask)
            scmPitestTask.with {
                description = "Run PIT analysis for java classes using specified scm repository"
                group = PITEST_TASK_GROUP
            }
            configureTaskDefault(task)
            configureScmTaskDefault(scmPitestTask)
        }
    }

    private void applyRequiredJavaPlugin() {
        //The new Gradle plugin mechanism requires all mandatory plugins to be applied explicit
        //See: https://github.com/szpak/gradle-pitest-plugin/issues/21
        project.apply(plugin: 'java')
    }

    private void createConfigurations() {
        project.rootProject.buildscript.configurations.maybeCreate(PITEST_CONFIGURATION_NAME).with {
            visible = false
            description = "The Pitest libraries to be used for this project."
        }
    }

    //TODO: MZA: Maybe move it to the constructor of an extension class?
    private void createExtension(Project project) {
        extension = project.extensions.create("pitest", PitestPluginExtension)
        extension.reportDir = new File("${project.reporting.baseDir.path}/pitest")
        extension.pitestVersion = DEFAULT_PITEST_VERSION
        extension.testSourceSets = [project.sourceSets.test] as Set
        extension.mainSourceSets = [project.sourceSets.main] as Set
    }

    private void createScmExtension(Project project) {
        scmExtension = project.extensions.create("scmPitest", ScmPitestPluginExtension)
        scmExtension.reportDir = new File("${project.reporting.baseDir.path}/pitest")
        scmExtension.pitestVersion = DEFAULT_PITEST_VERSION
        scmExtension.testSourceSets = [project.sourceSets.test] as Set
        scmExtension.mainSourceSets = [project.sourceSets.main] as Set
    }

    private void configureTaskDefault(PitestTask task) {
        task.conventionMapping.with {
            additionalClasspath = {
                List<FileCollection> testRuntimeClasspath = extension.testSourceSets*.runtimeClasspath

                FileCollection combinedTaskClasspath = new UnionFileCollection(testRuntimeClasspath)
                FileCollection filteredCombinedTaskClasspath = combinedTaskClasspath.filter { File file ->
                    !FILE_EXTENSIONS_TO_FILTER_FROM_CLASSPATH.find { file.name.endsWith(".$it") }
                }

                return filteredCombinedTaskClasspath
            }
            useAdditionalClasspathFile = { extension.useClasspathFile }
            additionalClasspathFile = { new File(project.buildDir, PIT_ADDITIONAL_CLASSPATH_DEFAULT_FILE_NAME) }
            launchClasspath = {
                project.rootProject.buildscript.configurations[PITEST_CONFIGURATION_NAME]
            }
            mutableCodePaths = { calculateBaseMutableCodePaths() + (extension.additionalMutableCodePaths ?: []) }
            sourceDirs = { extension.mainSourceSets*.allSource.srcDirs.flatten() as Set }

            reportDir = { extension.reportDir }
            targetClasses = {
                log.debug("Setting targetClasses. project.getGroup: {}, class: {}", project.getGroup(), project.getGroup()?.class)
                if (extension.targetClasses) {
                    return extension.targetClasses
                }
                if (project.getGroup()) {   //Assuming it is always a String class instance
                    return [project.getGroup() + ".*"] as Set
                }
                return null
            }
            targetTests = { extension.targetTests }
            dependencyDistance = { extension.dependencyDistance }
            threads = { extension.threads }
            mutateStaticInits = { extension.mutateStaticInits }
            includeJarFiles = { extension.includeJarFiles }
            mutators = { extension.mutators }
            excludedMethods = { extension.excludedMethods }
            excludedClasses = { extension.excludedClasses }
            avoidCallsTo = { extension.avoidCallsTo }
            verbose = { extension.verbose }
            timeoutFactor = { extension.timeoutFactor }
            timeoutConstInMillis = { extension.timeoutConstInMillis }
            maxMutationsPerClass = { extension.maxMutationsPerClass }
            childProcessJvmArgs = { extension.jvmArgs }
            outputFormats = { extension.outputFormats }
            failWhenNoMutations = { extension.failWhenNoMutations }
            includedGroups = { extension.includedGroups }
            excludedGroups = { extension.excludedGroups }
            detectInlinedCode = { extension.detectInlinedCode }
            timestampedReports = { extension.timestampedReports }
            historyInputLocation = { extension.historyInputLocation }
            historyOutputLocation = { extension.historyOutputLocation }
            enableDefaultIncrementalAnalysis = { extension.enableDefaultIncrementalAnalysis }
            defaultFileForHistoryData = { new File(project.buildDir, PIT_HISTORY_DEFAULT_FILE_NAME) }
            mutationThreshold = { extension.mutationThreshold }
            mutationEngine = { extension.mutationEngine }
            coverageThreshold = { extension.coverageThreshold }
            exportLineCoverage = { extension.exportLineCoverage }
            jvmPath = { extension.jvmPath }
            mainProcessJvmArgs = { extension.mainProcessJvmArgs }
            pluginConfiguration = { extension.pluginConfiguration }
            maxSurviving = { extension.maxSurviving }
            features = { extension.features }
        }

        project.afterEvaluate {
            task.dependsOn(calculateTasksToDependOn())

            addPitDependencies()
        }
    }

    private void configureScmTaskDefault(ScmPitestTask task) {
        task.conventionMapping.with {
            additionalClasspath = {
                List<FileCollection> testRuntimeClasspath = scmExtension.testSourceSets*.runtimeClasspath

                FileCollection combinedTaskClasspath = new UnionFileCollection(testRuntimeClasspath)
                FileCollection filteredCombinedTaskClasspath = combinedTaskClasspath.filter { File file ->
                    !FILE_EXTENSIONS_TO_FILTER_FROM_CLASSPATH.find { file.name.endsWith(".$it") }
                }

                return filteredCombinedTaskClasspath
            }
            useAdditionalClasspathFile = { scmExtension.useClasspathFile }
            additionalClasspathFile = { new File(project.buildDir, PIT_ADDITIONAL_CLASSPATH_DEFAULT_FILE_NAME) }
            launchClasspath = {
                project.rootProject.buildscript.configurations[PITEST_CONFIGURATION_NAME]
            }
            mutableCodePaths = { calculateBaseMutableCodePaths() + (extension.additionalMutableCodePaths ?: []) }
            sourceDirs = { scmExtension.mainSourceSets*.allSource.srcDirs.flatten() as Set }

            reportDir = { scmExtension.reportDir }
            targetClasses = {
                log.debug("Setting targetClasses. project.getGroup: {}, class: {}", project.getGroup(), project.getGroup()?.class)
                if (scmExtension.targetClasses) {
                    return scmExtension.targetClasses
                }
                if (project.getGroup()) {   //Assuming it is always a String class instance
                    return [project.getGroup() + ".*"] as Set
                }
                return null
            }
            targetTests = { scmExtension.targetTests }
            dependencyDistance = { scmExtension.dependencyDistance }
            threads = { scmExtension.threads }
            mutateStaticInits = { scmExtension.mutateStaticInits }
            includeJarFiles = { scmExtension.includeJarFiles }
            mutators = { scmExtension.mutators }
            excludedMethods = { scmExtension.excludedMethods }
            excludedClasses = { scmExtension.excludedClasses }
            avoidCallsTo = { scmExtension.avoidCallsTo }
            verbose = { scmExtension.verbose }
            timeoutFactor = { scmExtension.timeoutFactor }
            timeoutConstInMillis = { scmExtension.timeoutConstInMillis }
            maxMutationsPerClass = { scmExtension.maxMutationsPerClass }
            childProcessJvmArgs = { scmExtension.jvmArgs }
            outputFormats = { scmExtension.outputFormats }
            failWhenNoMutations = { scmExtension.failWhenNoMutations }
            includedGroups = { scmExtension.includedGroups }
            excludedGroups = { scmExtension.excludedGroups }
            detectInlinedCode = { scmExtension.detectInlinedCode }
            timestampedReports = { scmExtension.timestampedReports }
            historyInputLocation = { scmExtension.historyInputLocation }
            historyOutputLocation = { scmExtension.historyOutputLocation }
            enableDefaultIncrementalAnalysis = { scmExtension.enableDefaultIncrementalAnalysis }
            defaultFileForHistoryData = { new File(project.buildDir, PIT_HISTORY_DEFAULT_FILE_NAME) }
            mutationThreshold = { scmExtension.mutationThreshold }
            mutationEngine = { scmExtension.mutationEngine }
            coverageThreshold = { scmExtension.coverageThreshold }
            exportLineCoverage = { scmExtension.exportLineCoverage }
            jvmPath = { scmExtension.jvmPath }
            mainProcessJvmArgs = { scmExtension.mainProcessJvmArgs }
            pluginConfiguration = { scmExtension.pluginConfiguration }
            maxSurviving = { scmExtension.maxSurviving }
            features = { scmExtension.features }
            scm = {scmExtension.scm}
            startScmVersion = {scmExtension.startScmVersion}
            startScmVersionType = {scmExtension.startScmVersionType}
            endScmVersion = {scmExtension.endScmVersion}
            endScmVersionType = {scmExtension.endScmVersionType}
            includeFileStatuses = {scmExtension.includeFileStatuses}
            scmRoot = {scmExtension.scmRoot}
        }

        project.afterEvaluate {
            task.dependsOn(calculateTasksToDependOn())

            addPitDependencies()
        }
    }

    @CompileStatic
    private Set<File> calculateBaseMutableCodePaths() {
        if (isGradleVersionBefore4()) {
            log.warn("WARNING. Support for Gradle <4.0 in gradle-pitest-plugin is deprecated (due to incompatible changes in Gradle itself).")
            //Casting to Iterable to eliminate "NoSuchMethodError: org.codehaus.groovy.runtime.DefaultGroovyMethods.flatten(Ljava/util/List;)Ljava/util/List;"
            //while compiling code with Groovy 2.4.11 (Gradle 4.1) and running with Groovy 2.3.2 (Gradle 2.0)
            return ((Iterable<File>)extension.mainSourceSets*.output.classesDir).flatten() as Set<File>
        } else {
            return extension.mainSourceSets*.output.classesDirs.files.flatten() as Set<File>
        }
    }

    @CompileStatic
    private boolean isGradleVersionBefore4() {
        String gradleVersionAsString = project.gradle.gradleVersion
        return gradleVersionAsString.startsWith("2.") || gradleVersionAsString.startsWith("3.")
    }

    @CompileStatic
    private Set<String> calculateTasksToDependOn() {
        Set<String> tasksToDependOn = extension.testSourceSets.collect { it.name + "Classes" } as Set
        log.debug("pitest tasksToDependOn: $tasksToDependOn")
        return tasksToDependOn
    }

    @CompileStatic
    private void addPitDependencies() {
        log.info("Using PIT: $extension.pitestVersion")
        project.rootProject.buildscript.dependencies.add(PITEST_CONFIGURATION_NAME, "org.pitest:pitest-command-line:$extension.pitestVersion")
    }
}
