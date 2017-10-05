package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFileStatus

class ScmPitestTaskConfigurationSpec extends BasicProjectBuilderSpec<ScmPitestPluginExtension> implements WithScmPitestTaskInitialization {

    def "should set basic parameters correctly"() {
        given:
            String[] targetClasses = ["sample", "another-sample"]
            project.scmPitest.targetClasses = targetClasses
        and:
            Map<String, String> configurationMap = scmPitestTask.createTaskArgumentMap()
        expect:
            configurationMap['targetClasses'] == "sample,another-sample"
    }

    def "should set start scm version correctly"() {
        given:
            project.scmPitest.startScmVersion = "1.0.0"
        expect:
            scmPitestTask.startScmVersion == "1.0.0"
    }

    def "should set start scm version type correctly"() {
        given:
            project.scmPitest.startScmVersionType = "tag"
        expect:
            scmPitestTask.startScmVersionType == "tag"
    }

    def "should set end scm version correctly"() {
        given:
            project.scmPitest.startScmVersion = "release/1.2.2"
        expect:
            scmPitestTask.startScmVersion == "release/1.2.2"
    }

    def "should set end scm version type correctly"() {
        given:
            project.scmPitest.startScmVersionType = "tag"
        expect:
            scmPitestTask.startScmVersionType == "tag"
    }

    def "should set scm root correctly" () {
        given:
            project.scmPitest.scmRoot = "$project.rootDir"
        expect:
            scmPitestTask.scmRoot == project.rootDir
    }

    def "should set include file statuses correctly"() {
        given:
            project.scmPitest.includeFileStatuses = ["added"]
        expect:
            scmPitestTask.includeFileStatuses == [ScmFileStatus.ADDED] as Set
    }
}
