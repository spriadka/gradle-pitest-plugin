package info.solidsoft.gradle.pitest

class ScmPitestTaskConfigurationSpec extends BasicProjectBuilderSpec<ScmPitestPluginExtension> implements WithScmPitestTaskInitialization {

    def "should set start scm version correctly"() {
        given:
            project.scmPitest.startScmVersion = "1.0.0"
        expect:
            scmPitestTask.startScmVersion == "1.0.0"
    }

    def "should set start scm version type to '#versionType' correctly"() {
        given:
            project.scmPitest.startScmVersionType = versionType
        expect:
            scmPitestTask.startScmVersionType == versionType
        where:
            versionType << ["tag","revision","repository"]
    }

    def "should set end scm version correctly"() {
        given:
            project.scmPitest.startScmVersion = "release/1.2.2"
        expect:
            scmPitestTask.startScmVersion == "release/1.2.2"
    }

    def "should set end scm version type to '#versionType' correctly"() {
        given:
            project.scmPitest.startScmVersionType = versionType
        expect:
            scmPitestTask.startScmVersionType == versionType
        where:
            versionType << ["tag","revision","branch"]
    }

    def "should set scm root correctly" () {
        given:
            project.scmPitest.scmRoot = "$project.rootDir"
        expect:
            scmPitestTask.scmRoot == project.rootDir
    }

    def "should set connectionType to '#connectionType' correctly" () {
        given:
            project.scmPitest.connectionType = connectionType
        expect:
            scmPitestTask.connectionType == connectionType
        where:
            connectionType << ["connection", "developerConnection"]
    }

    def "should set goal to '#goal' correctly" () {
        given:
            project.scmPitest.goal = goal
        expect:
            scmPitestTask.goal == goal
        where:
            goal << ["lastCommit", "localChanges", "custom"]
    }

    def "should set includeFileStatuses correctly ('#fileStatuses')" () {
        given:
            project.scmPitest.includeFileStatuses = fileStatuses
        expect:
            scmPitestTask.includeFileStatuses == fileStatuses as Set
        where:
            fileStatuses << [["added","modified","deleted"],["added","added"],["deleted","unknown","added","modified"]]
    }
}
