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

import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet

/**
 * Extension class with configurable parameters for Pitest plugin.
 *
 * Note: additionalClasspath, mutableCodePaths, sourceDirs, reportDir and pitestVersion are automatically set using project
 *   configuration. sourceDirs, reportDir and pitestVersion can be overridden by an user.
 */

class PitestPluginExtension {

    protected final static List<String> DYNAMIC_LIBRARY_EXTENSIONS = ['so', 'dll', 'dylib']
    protected final static List<String> FILE_EXTENSIONS_TO_FILTER_FROM_CLASSPATH = ['pom'] + DYNAMIC_LIBRARY_EXTENSIONS

    final PropertyState<String> pitestVersion
    final PropertyState<File> reportDir
    final PropertyState<Set<String>> targetClasses
    final PropertyState<Set<String>> targetTests
    final PropertyState<Integer> dependencyDistance
    final PropertyState<Integer> threads
    final PropertyState<Boolean> mutateStaticInits
    final PropertyState<Boolean> includeJarFiles
    final PropertyState<Set<String>> mutators
    final PropertyState<Set<String>> excludedMethods
    final PropertyState<Set<String>> excludedClasses
    final PropertyState<Set<String>> avoidCallsTo
    final PropertyState<Boolean> verbose
    final PropertyState<BigDecimal> timeoutFactor
    final PropertyState<Integer> timeoutConstInMillis
    final PropertyState<Integer> maxMutationsPerClass
    /**
     * JVM arguments to use when PIT launches child processes
     *
     * Note. This parameter type was changed from String to List<String> in 0.33.0.
     */
    final PropertyState<List<String>> jvmArgs
    final PropertyState<Set<String>> outputFormats
    final PropertyState<Boolean> failWhenNoMutations
    final PropertyState<Set<String>> includedGroups
    final PropertyState<Set<String>> excludedGroups
    final PropertyState<Boolean> detectInlinedCode
    final PropertyState<Boolean> timestampedReports
    final PropertyState<File> historyInputLocation
    final PropertyState<File> historyOutputLocation
    final PropertyState<Boolean> enableDefaultIncrementalAnalysis
    final PropertyState<Integer> mutationThreshold
    final PropertyState<Integer> coverageThreshold
    final PropertyState<String> mutationEngine
    final PropertyState<Set<SourceSet>> testSourceSets
    final PropertyState<Set<SourceSet>> mainSourceSets
    final PropertyState<Boolean> exportLineCoverage
    final PropertyState<File> jvmPath

    /**
     * JVM arguments to use when Gradle plugin launches the main PIT process.
     *
     * @since 0.33.0 (specific for Gradle plugin)
     */
    final PropertyState<List<String>> mainProcessJvmArgs

    /**
     * Additional mutableCodePaths (paths with production classes which should be mutated).<p/>
     *
     * By default all classes produced by default sourceSets (or defined via mainSourceSets property) are used as production code to mutate.
     * In some rare cases it is required to pass additional classes, e.g. from JAR produced by another subproject. Issue #25.
     *
     * Samples usage ("itest" project depends on "shared" project):
     * <pre>
     * configure(project(':itest')) {
     *     dependencies {
     *         compile project(':shared')
     *     }
     *
     *     apply plugin: "info.solidsoft.pitest"
     *     //mutableCodeBase - additional configuration to resolve :shared project JAR as mutable code path for PIT
     *     configurations { mutableCodeBase { transitive false } }
     *     dependencies { mutableCodeBase project(':shared') }
     *     pitest {
     *         mainSourceSets = [project.sourceSets.main, project(':shared').sourceSets.main]
     *         additionalMutableCodePaths = [configurations.mutableCodeBase.singleFile]
     *     }
     * }
     * </pre>
     *
     * @since 1.1.3 (specific for Gradle plugin)
     */
    final PropertyState<Set<File>> additionalMutableCodePaths

    /**
     * Plugin configuration parameters.
     *
     * Should be defined a map:
     * <pre>
     * pitest {
     *     pluginConfiguration = ["plugin1.key1": "value1", "plugin1.key2": "value2"]
     * }
     * </pre>
     *
     * @since 1.1.6
     */
    final PropertyState<Map<String, String>> pluginConfiguration

    final PropertyState<Integer> maxSurviving

    /**
     * Use classpath file instead of passing classpath in a command line
     *
     * Useful with very long classpath and Windows - see https://github.com/hcoles/pitest/issues/276
     * Disabled by default.
     *
     * @since 1.2.0
     */
    @Incubating
    final PropertyState<Boolean> useClasspathFile

    /**
     * Turned on/off features in PIT itself and its plugins.
     *
     * Some details: https://github.com/hcoles/pitest/releases/tag/pitest-parent-1.2.1
     *
     * @since 1.2.1
     */
    @Incubating
    final PropertyState<List<String>> features

    PitestPluginExtension(Project project) {
        pitestVersion = project.property(String.class)
        reportDir = project.property(File.class)
        targetClasses = project.property(Set.class)
        targetTests = project.property(Set.class)
        dependencyDistance = project.property(Integer.class)
        threads = project.property(Integer.class)
        mutateStaticInits = project.property(Boolean.class)
        includeJarFiles = project.property(Boolean.class)
        mutators = project.property(Set.class)
        excludedMethods = project.property(Set.class)
        excludedClasses = project.property(Set.class)
        avoidCallsTo = project.property(Set.class)
        verbose = project.property(Boolean.class)
        timeoutFactor = project.property(BigDecimal.class)
        timeoutConstInMillis = project.property(Integer.class)
        maxMutationsPerClass = project.property(Integer.class)
        jvmArgs = project.property(List.class)
        outputFormats = project.property(Set.class)
        failWhenNoMutations = project.property(Boolean.class)
        includedGroups = project.property(Set.class)
        excludedGroups = project.property(Set.class)
        detectInlinedCode = project.property(Boolean.class)
        timestampedReports = project.property(Boolean.class)
        historyInputLocation = project.property(File.class)
        historyOutputLocation = project.property(File.class)
        enableDefaultIncrementalAnalysis = project.property(Boolean.class)
        mutationThreshold = project.property(Integer.class)
        coverageThreshold = project.property(Integer.class)
        mutationEngine = project.property(String.class)
        testSourceSets = project.property(Set.class)
        mainSourceSets = project.property(Set.class)
        exportLineCoverage = project.property(Boolean.class)
        jvmPath = project.property(File.class)
        additionalMutableCodePaths = project.property(Set.class)
        maxSurviving = project.property(Integer.class)
        useClasspathFile = project.property(Boolean.class)
        features = project.property(List.class)
        pluginConfiguration = project.property(Map.class)
    }

    /**
     * Alias for enableDefaultIncrementalAnalysis.
     *
     * To make migration from PIT Maven plugin to PIT Gradle plugin easier.
     *
     * @since 1.1.10
     */
    void setWithHistory(Boolean withHistory) {
        this.enableDefaultIncrementalAnalysis.set(withHistory)
    }

    Provider<String> getPitestVersionProvider() {
        return pitestVersion
    }

    String getPitestVersion() {
        return pitestVersion.get()
    }

    void setPitestVersion(String version) {
        this.pitestVersion.set(version)
    }

    Provider<File> getReportDirProvider() {
        return reportDir
    }

    void setReportDir(String reportDirAsString) {
        this.reportDir.set(new File(reportDirAsString))
    }

    Provider<Set<String>> getTargetClassesProvider() {
        return targetClasses
    }

    void setTargetClasses(Set<String> targetClasses) {
        this.targetClasses.set(targetClasses)
    }

    Provider<Set<String>> getTargetTestsProvider() {
        return targetTests
    }

    void setTargetTests(Set<String> targetClasses) {
        this.targetTests.set(targetClasses)
    }

    Provider<Integer> getDependencyDistanceProvider() {
        return dependencyDistance
    }

    void setDependencyDistance(Integer value) {
        this.dependencyDistance.set(value)
    }

    Provider<Integer> getThreadsProvider() {
        return threads
    }

    void setThreads(Integer value) {
        this.threads.set(value)
    }

    Provider<Boolean> getMutateStaticInitsProvider() {
        return mutateStaticInits
    }

    void setMutateStaticInits(Boolean value) {
        this.mutateStaticInits.set(value)
    }

    Provider<Boolean> getIncludeJarFilesProvider() {
        return includeJarFiles
    }

    void setIncludeJarFiles(Boolean value) {
        this.includeJarFiles.set(value)
    }

    Provider<Set<String>> getMutatorsProvider() {
        return mutators
    }

    void setMutators(Set<String> value) {
        this.mutators.set(value)
    }

    Provider<Set<String>> getExcludedMethodsProvider() {
        return excludedMethods
    }

    void setExcludedMethods(Set<String> value) {
        this.excludedMethods.set(value)
    }

    Provider<Set<String>> getExcludedClassesProvider() {
        return excludedClasses
    }

    void setExcludedClasses(Set<String> value) {
        this.excludedClasses.set(value)
    }

    Provider<Set<String>> getAvoidCallsToProvider() {
        return avoidCallsTo
    }

    void setAvoidCallsTo(Set<String> value) {
        this.avoidCallsTo.set(value)
    }

    Provider<Boolean> getVerboseProvider() {
        return verbose
    }

    void setVerbose(Boolean value) {
        this.verbose.set(value)
    }

    Provider<BigDecimal> getTimeoutFactorProvider() {
        return timeoutFactor
    }

    void setTimeoutFactor(String value) {
        this.timeoutFactor.set(new BigDecimal(value))
    }

    Provider<Integer> getTimeoutConstInMillisProvider() {
        return timeoutConstInMillis
    }

    void setTimeoutConstInMillis(Integer value) {
        this.timeoutConstInMillis.set(value)
    }

    Provider<Integer> getMaxMutationsPerClassProvider() {
        return maxMutationsPerClass
    }

    void setMaxMutationsPerClass(Integer value) {
        this.maxMutationsPerClass.set(value)
    }

    Provider<List<String>> getJvmArgsProvider() {
        return jvmArgs
    }

    void setJvmArgs(List<String> value) {
        this.jvmArgs.set(value)
    }

    Provider<Set<String>> getOutputFormatsProvider() {
        return outputFormats
    }

    void setOutputFormats(Set<String> value) {
        this.outputFormats.set(value)
    }

    Provider<Boolean> getFailWhenNoMutationsProvider() {
        return failWhenNoMutations
    }

    void setFailWhenNoMutations(Boolean value) {
        this.failWhenNoMutations.set(value)
    }

    Provider<Set<String>> getIncludedGroupsProvider() {
        return includedGroups
    }

    void setIncludedGroups(Set<String> value) {
        this.includedGroups.set(value)
    }


    Provider<Set<String>> getExcludedGroupsProvider() {
        return excludedGroups
    }

    void setExcludedGroups(Set<String> value) {
        this.excludedGroups.set(value)
    }


    Provider<Boolean> getDetectInlinedCodeProvider() {
        return detectInlinedCode
    }

    void setDetectInlineCode(Boolean value) {
        this.detectInlinedCode.set(value)
    }

    Provider<Boolean> getTimestampedReportsProvider() {
        return timestampedReports
    }

    void setTimestampedReports(Boolean value) {
        this.timestampedReports.set(value)
    }

    Provider<File> getHistoryInputLocationProvider() {
        return historyInputLocation
    }

    void setHistoryInputLocation(String historyInputLocationPath) {
        this.historyInputLocation.set(new File(historyInputLocationPath))
    }

    Provider<File> getHistoryOutputLocationProvider() {
        return historyOutputLocation
    }

    void setHistoryOutputLocation(String historyOutputLocationPath) {
        this.historyOutputLocation.set(new File(historyOutputLocationPath))
    }

    Provider<Boolean> getEnableDefaultIncrementalAnalysisProvider() {
        return enableDefaultIncrementalAnalysis
    }

    void setEnableDefaultIncrementalAnalysis(Boolean value) {
        this.enableDefaultIncrementalAnalysis.set(value)
    }

    Provider<Integer> getMutationThresholdProvider() {
        return mutationThreshold
    }


    void setMutationTreshold(Integer value) {
        this.mutationThreshold.set(value)
    }

    Provider<Integer> getCoverageThresholdProvider() {
        return coverageThreshold
    }

    void setCoverageTreshold(Integer value) {
        this.coverageThreshold.set(value)
    }

    Provider<String> getMutationEngineProvider() {
        return mutationEngine
    }

    void setMutationEngine(String value) {
        this.mutationEngine.set(value)
    }

    Provider<Set<SourceSet>> getTestSourceSetsProvider() {
        return testSourceSets
    }

    Set<SourceSet> getTestSourceSets() {
        return testSourceSets.get()
    }

    void setTestSourceSets(Set<SourceSet> value) {
        this.testSourceSets.set(value)
    }

    Provider<Set<SourceSet>> getMainSourceSetsProvider() {
        return mainSourceSets
    }

    Set<SourceSet> getMainSourceSets() {
        return mainSourceSets.get()
    }

    void setMainSourceSets(Set<SourceSet> value) {
        this.mainSourceSets.set(value)
    }

    Provider<Boolean> getExportLineCoverageProvider() {
        return exportLineCoverage
    }

    void setExportLineCoverage(Boolean value) {
        this.exportLineCoverage.set(value)
    }

    Provider<File> getJvmPathProvider() {
        return jvmPath
    }

    void setJvmPath(String jvmPathAsString) {
        this.jvmPath.set(new File(jvmPathAsString))
    }

    Provider<List<String>> getMainProcessJvmArgsProvider() {
        return mainProcessJvmArgs
    }

    void setMainJvmArgs(List<String> value) {
        this.mainProcessJvmArgs.set(value)
    }

    Provider<Set<File>> getAdditionalMutableCodePathsProvider() {
        return additionalMutableCodePaths
    }

    Set<File> getAdditionalMutableCodePaths() {
        return additionalMutableCodePaths.isPresent() ? additionalMutableCodePaths.get() : Collections.emptySet()
    }

    void setAdditionalMutableCodePaths(Set<File> value) {
        this.additionalMutableCodePaths.set(value)
    }

    Provider<Map<String, String>> getPluginConfigurationProvider() {
        return pluginConfiguration
    }

    void setPluginConfiguration(Map<String, String> value) {
        this.pluginConfiguration.set(value)
    }

    Provider<Integer> getMaxSurvivingProvider() {
        return maxSurviving
    }

    void setMaxSurviving(Integer value) {
        this.maxSurviving.set(value)
    }

    Provider<Boolean> getUseClasspathFileProvider() {
        return useClasspathFile
    }

    void setUseClasspathFile(Boolean value) {
        this.useClasspathFile.set(value)
    }

    Provider<List<String>> getFeaturesProvider() {
        return features
    }

    void setFeatures(List<String> value) {
        this.features.set(value)
    }
}
