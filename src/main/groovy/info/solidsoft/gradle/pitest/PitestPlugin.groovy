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
    private ScmPitestTask scmPitestTask
    private PitestTask pitestTask

    void apply(Project project) {
        this.project = project
        applyRequiredJavaPlugin()
        createConfigurations()
        createExtension(project)
        createScmExtension(project)
        project.plugins.withType(JavaBasePlugin) {
            pitestTask = project.tasks.create(PITEST_TASK_NAME, PitestTask)
            pitestTask.with {
                description = "Run PIT analysis for java classes"
                group = PITEST_TASK_GROUP
            }
            scmPitestTask = project.tasks.create(SCM_PITEST_TASK_NAME, ScmPitestTask)
            scmPitestTask.with {
                description = "Run PIT analysis for java classes using specified scm repository"
                group = PITEST_TASK_GROUP
            }
            pitestTask.setMutableCodePaths(calculateBaseMutableCodePaths() + (extension.additionalMutableCodePaths ?: []))
            pitestTask.setSourceDirs(getSourceDirsFromExtension(extension))
            configurePitestTaskFromExtension(pitestTask, extension)
            configureScmTaskFromExtension(scmPitestTask, scmExtension)
        }
        project.afterEvaluate {
            pitestTask.dependsOn(calculateTasksToDependOn())
            if (!pitestTask.targetClasses) {
                if (project.group) {
                    pitestTask.setTargetClasses([project.group + ".*"] as Set)
                }
            }
            addPitDependencies()
        }
    }

    private Set<File> getSourceDirsFromExtension(PitestPluginExtension extension) {
        return extension.mainSourceSets*.allSource.srcDirs.flatten() as Set<File>
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
        extension = project.extensions.create("pitest", PitestPluginExtension, project)
        extension.setReportDir("${project.reporting.baseDir.path}/pitest")
        extension.pitestVersion = DEFAULT_PITEST_VERSION
        extension.testSourceSets = [project.sourceSets.test] as Set
        extension.mainSourceSets = [project.sourceSets.main] as Set
    }

    private void createScmExtension(Project project) {
        scmExtension = project.extensions.create("scmPitest", ScmPitestPluginExtension, project)
        scmExtension.setReportDir("${project.reporting.baseDir.path}/pitest")
        scmExtension.pitestVersion = DEFAULT_PITEST_VERSION
        scmExtension.testSourceSets = [project.sourceSets.test] as Set
        scmExtension.mainSourceSets = [project.sourceSets.main] as Set
    }

    private void configurePitestTaskFromExtension(AbstractPitestTask task, PitestPluginExtension extension) {
        task.setReportDir(extension.getReportDirProvider())
        task.setLaunchClasspath(project.rootProject.buildscript.configurations[PITEST_CONFIGURATION_NAME])
        task.setAdditionalClasspath(getAdditionalClasspathFromExtension(extension))
        task.setAdditionalClasspathFile(new File(project.buildDir, PIT_ADDITIONAL_CLASSPATH_DEFAULT_FILE_NAME))
        task.setUseClasspathFile(extension.getUseClasspathFileProvider())
        task.setTimeoutFactor(extension.getTimeoutFactorProvider())
        task.setTargetClasses(extension.getTargetClassesProvider())
        task.setTargetTests(extension.getTargetTestsProvider())
        task.setDependencyDistance(extension.getDependencyDistanceProvider())
        task.setThreads(extension.getThreadsProvider())
        task.setMutateStaticInits(extension.getMutateStaticInitsProvider())
        task.setIncludeJarFiles(extension.getIncludeJarFilesProvider())
        task.setMutators(extension.getMutatorsProvider())
        task.setExcludedMethods(extension.getExcludedMethodsProvider())
        task.setExcludedClasses(extension.getExcludedClassesProvider())
        task.setAvoidCallsTo(extension.getAvoidCallsToProvider())
        task.setVerbose(extension.getVerboseProvider())
        task.setTimeoutConstInMillis(extension.getTimeoutConstInMillisProvider())
        task.setMaxMutationsPerClass(extension.getMaxMutationsPerClassProvider())
        task.setChildProcessJvmArgs(extension.getJvmArgsProvider())
        task.setOutputFormats(extension.getOutputFormatsProvider())
        task.setFailWhenNoMutations(extension.getFailWhenNoMutationsProvider())
        task.setIncludedGroups(extension.getIncludedGroupsProvider())
        task.setExcludedGroups(extension.getExcludedGroupsProvider())
        task.setDetectInlineCode(extension.getDetectInlinedCodeProvider())
        task.setTimestampedReports(extension.getTimestampedReportsProvider())
        task.setHistoryInputLocation(extension.getHistoryInputLocationProvider())
        task.setHistoryOutputLocation(extension.getHistoryOutputLocationProvider())
        task.setEnableDefaultIncrementalAnalysis(extension.getEnableDefaultIncrementalAnalysisProvider())
        task.setDefaultFileForHistoryData(new File(project.buildDir, PIT_HISTORY_DEFAULT_FILE_NAME))
        task.setMutationThreshold(extension.getMutationThresholdProvider())
        task.setMutationEngine(extension.getMutationEngineProvider())
        task.setCoverageThreshold(extension.getCoverageThresholdProvider())
        task.setExportLineCoverage(extension.getExportLineCoverageProvider())
        task.setJvmPath(extension.getJvmPathProvider())
        task.setMainJvmArgs(extension.getMainProcessJvmArgsProvider())
        task.setPluginConfiguration(extension.getPluginConfigurationProvider())
        task.setMaxSurviving(extension.getMaxSurvivingProvider())
        task.setFeatures(extension.getFeaturesProvider())
    }

    private FileCollection getAdditionalClasspathFromExtension(PitestPluginExtension extension) {
        List<FileCollection> testRuntimeClasspath = extension.testSourceSets*.runtimeClasspath

        FileCollection combinedTaskClasspath = new UnionFileCollection(testRuntimeClasspath)
        FileCollection filteredCombinedTaskClasspath = combinedTaskClasspath.filter { File file ->
            !FILE_EXTENSIONS_TO_FILTER_FROM_CLASSPATH.find { file.name.endsWith(".$it") }
        }

        return filteredCombinedTaskClasspath
    }

    private void configureScmTaskFromExtension(ScmPitestTask task, ScmPitestPluginExtension scmExtension) {
        configurePitestTaskFromExtension(task, scmExtension)
        task.setIncludeFileStatuses(scmExtension.getIncludeFileStatusesProvider())
        task.setScmRoot(scmExtension.getScmRootProvider())
        task.setScm(scmExtension.getScmProvider())
        task.setConnectionType(scmExtension.getConnectionTypeProvider())
        task.setStartScmVersion(scmExtension.getStartScmVersionProvider())
        task.setStartScmVersionType(scmExtension.getStartScmVersionTypeProvider())
        task.setEndScmVersion(scmExtension.getEndScmVersionProvider())
        task.setEndScmVersionType(scmExtension.getEndScmVersionTypeProvider())
        task.setGoal(scmExtension.getGoalProvider())
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
