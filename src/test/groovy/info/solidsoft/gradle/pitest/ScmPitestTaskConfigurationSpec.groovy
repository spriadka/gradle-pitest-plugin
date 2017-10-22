package info.solidsoft.gradle.pitest

import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider

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
            project.scmPitest.scmRoot = "."
            project.scmPitest.manager = createManagerDouble()
            project.scmPitest.scm.url = "scm:git:git@github/hal/testsuite"
            project.scmPitest.includeFileStatuses = ["added"] as Set
            project.scmPitest.goal = goal
            Map<String, Class<ChangeLogStrategy>> goalMap = new HashMap<>()
            goalMap.put("lastCommit", LastCommitStrategy.class)
            goalMap.put("localChanges", LocalChangesStrategy.class)
            goalMap.put("custom", CustomChangeLogStrategy.class)
        expect:
            scmPitestTask.goal.class == goalMap.get(goal)
        where:
            goal << ["lastCommit", "localChanges", "custom"]
    }

    private ScmManager createManagerDouble() {
        ScmManager manager = new BasicScmManager()
        manager.setScmProvider("git", new GitExeScmProvider())
        return manager
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
