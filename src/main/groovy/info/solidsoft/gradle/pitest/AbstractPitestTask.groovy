package info.solidsoft.gradle.pitest

import groovy.transform.PackageScope
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

abstract class AbstractPitestTask extends JavaExec {

    @Input
    @OutputFile
    File additionalClasspathFile

    @InputFiles
    FileCollection additionalClasspath

    @Input
    FileCollection launchClasspath

    @InputFiles
    Set<File> mutableCodePaths

    @InputFiles
    Set<File> sourceDirs

    @Input
    File defaultFileForHistoryData

    @Input
    final PropertyState<Boolean> useClasspathFile

    @OutputDirectory
    final PropertyState<File> reportDir

    @Input
    final PropertyState<Set<String>> targetClasses

    @Input
    @Optional
    final PropertyState<Set<String>> targetTests

    @Input
    @Optional
    final PropertyState<Integer> dependencyDistance

    @Input
    @Optional
    final PropertyState<Integer> threads

    @Input
    @Optional
    final PropertyState<Boolean> mutateStaticInits

    @Input
    @Optional
    final PropertyState<Boolean> includeJarFiles

    @Input
    @Optional
    final PropertyState<Set<String>> mutators

    @Input
    @Optional
    final PropertyState<Set<String>> excludedMethods

    @Input
    @Optional
    final PropertyState<Set<String>> excludedClasses

    @Input
    @Optional
    final PropertyState<Set<String>> avoidCallsTo

    @Input
    @Optional
    final PropertyState<Boolean> verbose

    @Input
    @Optional
    final PropertyState<BigDecimal> timeoutFactor

    @Input
    @Optional
    final PropertyState<Integer> timeoutConstInMillis

    @Input
    @Optional
    final PropertyState<Integer> maxMutationsPerClass

    @Input
    @Optional
    final PropertyState<List<String>> childProcessJvmArgs

    @Input
    @Optional
    final PropertyState<Set<String>> outputFormats

    @Input
    @Optional
    final PropertyState<Boolean> failWhenNoMutations

    @Input
    @Optional
    final PropertyState<Set<String>> includedGroups

    @Input
    @Optional
    final PropertyState<Set<String>> excludedGroups

    @Input
    @Optional
    final PropertyState<Boolean> detectInlinedCode

    @Input
    @Optional
    final PropertyState<Boolean> timestampedReports

    @Input
    @Optional
    final PropertyState<File> historyInputLocation

    @OutputFile
    @Optional
    final PropertyState<File> historyOutputLocation

    @Input
    @Optional
    final PropertyState<Boolean> enableDefaultIncrementalAnalysis

    @Input
    @Optional
    final PropertyState<Integer> mutationThreshold

    @Input
    @Optional
    final PropertyState<Integer> coverageThreshold

    @Input
    @Optional
    final PropertyState<String> mutationEngine

    @Input
    @Optional
    final PropertyState<Boolean> exportLineCoverage

    @Input
    @Optional
    final PropertyState<File> jvmPath

    @Input
    @Optional
    final PropertyState<List<String>> mainProcessJvmArgs

    @Input
    @Optional
    final PropertyState<Map<String, String>> pluginConfiguration

    @Input
    @Optional
    final PropertyState<Integer> maxSurviving

    @Input
    @Optional
    final PropertyState<List<String>> features

    AbstractPitestTask() {
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
        exportLineCoverage = project.property(Boolean.class)
        jvmPath = project.property(File.class)
        childProcessJvmArgs = project.property(List.class)
        maxSurviving = project.property(Integer.class)
        features = project.property(List.class)
        useClasspathFile = project.property(Boolean.class)
        pluginConfiguration = project.property(Map.class)
        mainProcessJvmArgs = project.property(List.class)
        group = "Report"
    }

    @PackageScope   //visible for testing
    Map<String, String> createTaskArgumentMap() {
        Map<String, String> map = [:]
        map['sourceDirs'] = (getSourceDirs()*.path)?.join(',')
        map['reportDir'] = getReportDir().toString()
        map['targetClasses'] = getTargetClasses()?.join(',')
        map['targetTests'] = getTargetTests()?.join(',')
        map['dependencyDistance'] = getDependencyDistance()?.toString()
        map['threads'] = getThreads()?.toString()
        map['mutateStaticInits'] = getMutateStaticInits()?.toString()
        map['includeJarFiles'] = getIncludeJarFiles()?.toString()
        map["mutators"] = getMutators()?.join(',')
        map['excludedMethods'] = getExcludedMethods()?.join(',')
        map['excludedClasses'] = getExcludedClasses()?.join(',')
        map['avoidCallsTo'] = getAvoidCallsTo()?.join(',')
        map['verbose'] = getVerbose()?.toString()
        map['timeoutFactor'] = getTimeoutFactor()?.toString()
        map['timeoutConst'] = getTimeoutConstInMillis()?.toString()
        map['maxMutationsPerClass'] = getMaxMutationsPerClass()?.toString()
        map['jvmArgs'] = getChildProcessJvmArgs()?.join(',')
        map['outputFormats'] = getOutputFormats()?.join(',')
        map['failWhenNoMutations'] = getFailWhenNoMutations()?.toString()
        map['mutableCodePaths'] = (getMutableCodePaths()*.path)?.join(',')
        map['includedGroups'] = getIncludedGroups()?.join(',')
        map['excludedGroups'] = getExcludedGroups()?.join(',')
        map['detectInlinedCode'] = getDetectInlinedCode()?.toString()
        map['timestampedReports'] = getTimestampedReports()?.toString()
        map['mutationThreshold'] = getMutationThreshold()?.toString()
        map['coverageThreshold'] = getCoverageThreshold()?.toString()
        map['mutationEngine'] = getMutationEngine()
        map['exportLineCoverage'] = getExportLineCoverage()?.toString()
        map['includeLaunchClasspath'] = Boolean.FALSE.toString()   //code to analyse is passed via classPath
        map['jvmPath'] = getJvmPath()?.path
        map['maxSurviving'] = getMaxSurviving()?.toString()
        map['features'] = getFeatures()?.join(',')
        map.putAll(prepareMapWithClasspathConfiguration())
        map.putAll(prepareMapWithIncrementalAnalysisConfiguration())

        return removeEntriesWithNullValue(map)
    }

    protected List<String> createListOfAllArgumentsForPit() {
        Map<String, String> taskArgumentsMap = createTaskArgumentMap()
        List<String> argsAsList = createArgumentsListFromMap(taskArgumentsMap)
        List<String> multiValueArgsAsList = createMultiValueArgsAsList()
        return concatenateTwoLists(argsAsList, multiValueArgsAsList)
    }

    protected Map<String, String> prepareMapWithClasspathConfiguration() {
        if (getUseClasspathFile()) {
            fillAdditionalClasspathFileWithClasspathElements()
            return [classPathFile: getAdditionalClasspathFile().absolutePath]
        } else {
            return [classPath: getAdditionalClasspath().files.join(',')]
        }
    }

    protected void fillAdditionalClasspathFileWithClasspathElements() {
        String classpathElementsAsFileContent = getAdditionalClasspath().files.collect { it.getAbsolutePath() }.join(System.lineSeparator())
        //"withWriter" as "file << content" works in append mode (instead of overwrite one)
        getAdditionalClasspathFile().withWriter() {
            it << classpathElementsAsFileContent
        }
    }

    protected Map<String, String> prepareMapWithIncrementalAnalysisConfiguration() {
        if (getEnableDefaultIncrementalAnalysis()) {
            return [historyInputLocation : getHistoryInputLocation()?.path ?: getDefaultFileForHistoryData().path,
                    historyOutputLocation: getHistoryOutputLocation()?.path ?: getDefaultFileForHistoryData().path]
        } else {
            return [historyInputLocation: getHistoryInputLocation()?.path,
                    historyOutputLocation: getHistoryOutputLocation()?.path]
        }
    }

    protected Map removeEntriesWithNullValue(Map map) {
        return map.findAll { it.value != null }
    }

    protected List<String> createArgumentsListFromMap(Map<String, String> taskArgumentsMap) {
        return taskArgumentsMap.collect { k, v ->
            "--$k=$v".toString()
        }
    }

    @PackageScope   //visible for testing
    List<String> createMultiValueArgsAsList() {
        //It is a duplication/special case handling, but a PoC implementation with emulated multimap was also quite ugly and in addition error prone
        return getPluginConfiguration()?.collect { k, v ->
            "$k=$v".toString()
        }?.collect {
            "--pluginConfiguration=$it".toString()
        } ?: [] as List<String>
    }

    //Workaround to keep compatibility with Gradle <2.8
    //[] + [] is compiled in Groovy 2.4.x as "List<T> plus(List<T> left, Collection<T> right)" which is unavailable in Groovy 2.3 and fails with Gradle <2.8
    protected List<String> concatenateTwoLists(List<String> argsAsList, List<String> multiValueArgsAsList) {
        List<String> allArgs = []
        allArgs.addAll(argsAsList)
        allArgs.addAll(multiValueArgsAsList)
        return allArgs
    }

    File getReportDir() {
        return reportDir.getOrNull()
    }

    void setReportDir(String reportDirAsString) {
        this.reportDir.set(new File(reportDirAsString))
    }

    void setReportDir(Provider<File> provider) {
        this.reportDir.set(provider)
    }

    Set<String> getTargetClasses() {
        return targetClasses.getOrNull()
    }

    void setTargetClasses(Set<String> targetClasses) {
        this.targetClasses.set(targetClasses)
    }

    void setTargetClasses(Provider<Set<String>> provider) {
        this.targetClasses.set(provider)
    }

    Set<String> getTargetTests() {
        return targetTests.isPresent() ? targetTests.get() : Collections.emptySet()
    }

    void setTargetTests(Set<String> targetClasses) {
        this.targetTests.set(targetClasses)
    }

    void setTargetTests(Provider<Set<String>> provider) {
        this.targetTests.set(provider)
    }

    Integer getDependencyDistance() {
        return dependencyDistance.getOrNull()
    }

    void setDependencyDistance(Integer value) {
        this.dependencyDistance.set(value)
    }

    void setDependencyDistance(Provider<Integer> provider) {
        this.dependencyDistance.set(provider)
    }

    Integer getThreads() {
        return threads.getOrNull()
    }

    void setThreads(Integer value) {
        this.threads.set(value)
    }

    void setThreads(Provider<Integer> provider) {
        this.threads.set(provider)
    }

    Boolean getMutateStaticInits() {
        return mutateStaticInits.getOrNull()
    }

    void setMutateStaticInits(Boolean value) {
        this.mutateStaticInits.set(value)
    }

    void setMutateStaticInits(Provider<Boolean> provider) {
        this.mutateStaticInits.set(provider)
    }

    Boolean getIncludeJarFiles() {
        return includeJarFiles.getOrNull()
    }

    void setIncludeJarFiles(Boolean value) {
        this.includeJarFiles.set(value)
    }

    void setIncludeJarFiles(Provider<Boolean> provider) {
        this.includeJarFiles.set(provider)
    }

    Set<String> getMutators() {
        return mutators.isPresent() ? mutators.get() : Collections.emptySet()
    }

    void setMutators(Set<String> value) {
        this.mutators.set(value)
    }

    void setMutators(Provider<Set<String>> provider) {
        this.mutators.set(provider)
    }

    Set<String> getExcludedMethods() {
        return excludedMethods.isPresent() ? excludedMethods.get() : Collections.emptySet()
    }

    void setExcludedMethods(Set<String> value) {
        this.excludedMethods.set(value)
    }

    void setExcludedMethods(Provider<Set<String>> provider) {
        this.excludedMethods.set(provider)
    }

    Set<String> getExcludedClasses() {
        return excludedClasses.isPresent() ? excludedClasses.get() : Collections.emptySet()
    }

    void setExcludedClasses(Set<String> value) {
        this.excludedClasses.set(value)
    }

    void setExcludedClasses(Provider<Set<String>> provider) {
        this.excludedClasses.set(provider)
    }

    Set<String> getAvoidCallsTo() {
        return avoidCallsTo.isPresent() ? avoidCallsTo.get() : Collections.emptySet()
    }

    void setAvoidCallsTo(Set<String> value) {
        this.avoidCallsTo.set(value)
    }

    void setAvoidCallsTo(Provider<Set<String>> provider) {
        this.avoidCallsTo.set(provider)
    }

    Boolean getVerbose() {
        return verbose.getOrNull()
    }

    void setVerbose(Boolean value) {
        this.verbose.set(value)
    }

    void setVerbose(Provider<Boolean> provider) {
        this.verbose.set(provider)
    }

    BigDecimal getTimeoutFactor() {
        return timeoutFactor.getOrNull()
    }

    void setTimeoutFactor(String value) {
        this.timeoutFactor.set(new BigDecimal(value))
    }

    void setTimeoutFactor(Provider<BigDecimal> provider) {
        this.timeoutFactor.set(provider)
    }

    Integer getTimeoutConstInMillis() {
        return timeoutConstInMillis.getOrNull()
    }

    void setTimeoutConstInMillis(Integer value) {
        this.timeoutConstInMillis.set(value)
    }

    void setTimeoutConstInMillis(Provider<Integer> provider) {
        this.timeoutConstInMillis.set(provider)
    }

    Integer getMaxMutationsPerClass() {
        return maxMutationsPerClass.getOrNull()
    }

    void setMaxMutationsPerClass(Integer value) {
        this.maxMutationsPerClass.set(value)
    }

    void setMaxMutationsPerClass(Provider<Integer> provider) {
        this.maxMutationsPerClass.set(provider)
    }

    List<String> getChildProcessJvmArgs() {
        return childProcessJvmArgs.isPresent() ? childProcessJvmArgs.get() : Collections.emptyList()
    }

    void setChildProcessJvmArgs(List<String> value) {
        this.childProcessJvmArgs.set(value)
    }

    void setChildProcessJvmArgs(Provider<List<String>> provider) {
        this.childProcessJvmArgs.set(provider)
    }

    Set<String> getOutputFormats() {
        return outputFormats.isPresent() ? outputFormats.get() : Collections.emptySet()
    }

    void setOutputFormats(Set<String> value) {
        this.outputFormats.set(value)
    }

    void setOutputFormats(Provider<Set<String>> provider) {
        this.outputFormats.set(provider)
    }


    Boolean getFailWhenNoMutations() {
        return failWhenNoMutations.getOrNull()
    }

    void setFailWhenNoMutations(Boolean value) {
        this.failWhenNoMutations.set(value)
    }

    void setFailWhenNoMutations(Provider<Boolean> provider) {
        this.failWhenNoMutations.set(provider)
    }

    Set<String> getIncludedGroups() {
        return includedGroups.isPresent() ? includedGroups.get() : Collections.emptySet()
    }

    void setIncludedGroups(Set<String> value) {
        this.includedGroups.set(value)
    }

    void setIncludedGroups(Provider<Set<String>> provider) {
        this.includedGroups.set(provider)
    }

    Set<String> getExcludedGroups() {
        return excludedGroups.isPresent() ? excludedGroups.get() : Collections.emptySet()
    }

    void setExcludedGroups(Set<String> value) {
        this.excludedGroups.set(value)
    }

    void setExcludedGroups(Provider<Set<String>> provider) {
        this.excludedGroups.set(provider)
    }

    Boolean getDetectInlinedCode() {
        return detectInlinedCode.getOrNull()
    }

    void setDetectInlineCode(Boolean value) {
        this.detectInlinedCode.set(value)
    }

    void setDetectInlineCode(Provider<Boolean> provider) {
        this.detectInlinedCode.set(provider)
    }


    Boolean getTimestampedReports() {
        return timestampedReports.getOrNull()
    }

    void setTimestampedReports(Boolean value) {
        this.timestampedReports.set(value)
    }

    void setTimestampedReports(Provider<Boolean> provider) {
        this.timestampedReports.set(provider)
    }

    File getHistoryInputLocation() {
        return historyInputLocation.getOrNull()
    }

    void setHistoryInputLocation(String historyInputLocationPath) {
        this.historyInputLocation.set(new File(historyInputLocationPath))
    }

    void setHistoryInputLocation(Provider<File> provider) {
        this.historyInputLocation.set(provider)
    }


    File getHistoryOutputLocation() {
        return historyOutputLocation.getOrNull()
    }

    void setHistoryOutputLocation(String historyOutputLocationPath) {
        this.historyOutputLocation.set(new File(historyOutputLocationPath))
    }

    void setHistoryOutputLocation(Provider<File> provider) {
        this.historyOutputLocation.set(provider)
    }

    Boolean getEnableDefaultIncrementalAnalysis() {
        return enableDefaultIncrementalAnalysis.getOrNull()
    }

    void setEnableDefaultIncrementalAnalysis(Boolean value) {
        this.enableDefaultIncrementalAnalysis.set(value)
    }

    void setEnableDefaultIncrementalAnalysis(Provider<Boolean> provider) {
        this.enableDefaultIncrementalAnalysis.set(provider)
    }

    Integer getMutationTreshold() {
        return mutationThreshold.getOrNull()
    }

    void setMutationTreshold(Integer value) {
        this.mutationThreshold.set(value)
    }

    void setMutationThreshold(Provider<Integer> provider) {
        this.mutationThreshold.set(provider)
    }

    Integer getCoverageTreshold() {
        return coverageThreshold.getOrNull()
    }

    void setCoverageTreshold(Integer value) {
        this.coverageThreshold.set(value)
    }

    void setCoverageThreshold(Provider<Integer> provider) {
        this.coverageThreshold.set(provider)
    }

    String getMutationEngine() {
        return mutationEngine.getOrNull()
    }

    void setMutationEngine(String value) {
        this.mutationEngine.set(value)
    }

    void setMutationEngine(Provider<String> provider) {
        this.mutationEngine.set(provider)
    }

    Boolean getExportLineCoverage() {
        return exportLineCoverage.getOrNull()
    }

    void setExportLineCoverage(Boolean value) {
        this.exportLineCoverage.set(value)
    }

    void setExportLineCoverage(Provider<Boolean> provider) {
        this.exportLineCoverage.set(provider)
    }

    File getJvmPath() {
        return jvmPath.getOrNull()
    }

    void setJvmPath(String jvmPathAsString) {
        this.jvmPath.set(new File(jvmPathAsString))
    }

    void setJvmPath(Provider<File> provider) {
        this.jvmPath.set(provider)
    }

    List<String> getMainProcessJvmArgs() {
        return mainProcessJvmArgs.isPresent() ? mainProcessJvmArgs.get() : Collections.emptyList()
    }

    void setMainJvmArgs(Provider<List<String>> provider) {
        this.mainProcessJvmArgs.set(provider)
    }

    Map<String, String> getPluginConfiguration() {
        return pluginConfiguration.isPresent() ? pluginConfiguration.get() : Collections.emptyMap()
    }

    void setPluginConfiguration(Map<String, String> value) {
        this.pluginConfiguration.set(value)
    }

    void setPluginConfiguration(Provider<Map<String, String>> provider) {
        this.pluginConfiguration.set(provider)
    }

    Integer getMaxSurviving() {
        return maxSurviving.getOrNull()
    }

    void setMaxSurviving(Integer value) {
        this.maxSurviving.set(value)
    }

    void setMaxSurviving(Provider<Integer> provider) {
        this.maxSurviving.set(provider)
    }

    FileCollection getLaunchClasspath() {
        return launchClasspath
    }

    void setLaunchClasspath(FileCollection value) {
        this.launchClasspath = value
    }

    List<String> getFeatures() {
        return features.getOrNull()
    }

    void setFeatures(List<String> value) {
        this.features.set(value)
    }

    void setFeatures(Provider<List<String>> provider) {
        this.features.set(provider)
    }

    Boolean getUseClasspathFile() {
        return this.useClasspathFile.get()
    }

    void setUseClasspathFile(Boolean value) {
        this.useClasspathFile.set(value)
    }

    void setUseClasspathFile(Provider<Boolean> provider) {
        this.useClasspathFile.set(provider)
    }
}
