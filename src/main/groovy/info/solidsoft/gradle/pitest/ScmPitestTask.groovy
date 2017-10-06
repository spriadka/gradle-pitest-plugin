package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmBranch
import org.apache.maven.scm.ScmException
import org.apache.maven.scm.ScmRevision
import org.apache.maven.scm.ScmTag
import org.apache.maven.scm.ScmVersion
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.repository.ScmRepository

class ScmPitestTask extends AbstractPitestTask {

    ScmConnection scm = new ScmConnection()
    File scmRoot
    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion
    Set<String> includeFileStatuses
    String goal
    String connectionType

    ScmManager manager = new BasicScmManager()
    ChangeLogExecutor strategy

    private void configureManager() {
        Map<String, ScmProvider> providerMap = new HashMap<>()
        providerMap.put("git", new GitExeScmProvider())
        providerMap.each { it ->
            this.manager.setScmProvider(it.key, it.value)
        }
    }

    @Override
    void exec() {
        configureManager()
        try {
            logger.info("SCM root is: $scmRoot.path")
            ChangeLogExecutor changeLogExecutor = getExecutor()
            targetClasses = changeLogExecutor.executeChangeLog()
            args = createListOfAllArgumentsForPit()
            jvmArgs = (getMainProcessJvmArgs() ?: getJvmArgs())
            main = "org.pitest.mutationtest.commandline.MutationCoverageReport"
            classpath = getLaunchClasspath()
            super.exec()
        } catch (ScmTaskException scme) {
            throw new ScmTaskException("Error when querying scm", scme)
        }
    }

    private ChangeLogExecutor getExecutor() {
        ScmRepository repository = this.manager.makeScmRepository(getSCMRepository())
        switch (this.goal) {
            case "localChanges":
                return new LocalChangesStrategy(scmRoot, manager, includeFileStatuses, repository)
            case "lastCommit":
                return new LastCommitStrategy(scmRoot, manager, includeFileStatuses, repository)
            case "custom":
                validateCustomStrategyArgumentsOrFail()
                ScmVersion startVersion = getScmVersion(startScmVersionType, startScmVersion)
                ScmVersion endVersion = getScmVersion(endScmVersionType, endScmVersion)
                return new CustomChangeLogStrategy(scmRoot, manager, includeFileStatuses, repository, startVersion, endVersion)
            default:
                throw new ScmTaskException("Invalid value received for 'goal', " +
                    "expected one of ['lastCommit','localChanges','custom'], got $goal")
        }
    }

    private static ScmVersion getScmVersion(String versionType, String version) {
        switch (versionType) {
            case "branch":
                return new ScmBranch(version)
            case "tag":
                return new ScmTag(version)
            case "revision":
                return new ScmRevision(version)
            default:
                throw new ScmTaskException("Unknown version type")
        }
    }

    private String getSCMRepository() {
        if (connectionType == null) {
            throw new ScmTaskException("Connection type must be specified")
        }
        final String connection = scm.connection
        if (this.connectionType.equalsIgnoreCase("connection")) {
            return connection
        }
        final String developerConnection = scm.developerConnection
        if (this.connectionType.equalsIgnoreCase("developerConnection")) {
            return developerConnection
        }
        throw new ScmTaskException("SCM Connection is not set, check your connection")
    }

    private void validateCustomStrategyArgumentsOrFail() {
        if ([startScmVersionType, startScmVersion, endScmVersionType, endScmVersion].every({it != null && !it.empty})) {
            return
        }
        throw new ScmTaskException("Invalid configuration for")
    }
}
