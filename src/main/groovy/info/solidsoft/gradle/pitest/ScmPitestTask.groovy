package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmException
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.repository.ScmRepository

import javax.inject.Inject

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

    ScmManager manager

    ScmPitestTask(ScmManager manager) {
        this.manager = manager
    }

    @Inject
    ScmPitestTask() {
        this.manager = new BasicScmManager()
        configureManager()
    }

    private void configureManager() {
        Map<String, ScmProvider> providerMap = new HashMap<>()
        providerMap.put("git", new GitExeScmProvider())
        providerMap.each { it ->
            this.manager.setScmProvider(it.key, it.value)
        }
    }

    @Override
    protected void executePitestReport() {
        try {
            ScmRepository scmRepository = manager.makeScmRepository(getSCMRepository())
            ChangeLogExecutor changeLogExecutor = getExecutor(scmRepository)
            changeLogExecutor.executeChangeLog()
            args = createListOfAllArgumentsForPit()
            jvmArgs = (getMainProcessJvmArgs() ?: getJvmArgs())
            main = "org.pitest.mutationtest.commandline.MutationCoverageReport"
            classpath = getLaunchClasspath()
        } catch (ScmException scme) {
            throw new ScmTaskException("Error when querying scm", scme)
        }
    }

    private ChangeLogExecutor getExecutor(ScmRepository repository) {
        switch (goal) {
            case "localChanges":
                return new LocalChangesStrategy(scmRoot, manager, includeFileStatuses, repository)
            case "lastCommit":
                return new LastCommitStrategy(scmRoot, manager, includeFileStatuses, repository)
            case "custom":
                return new CustomChangeLogStrategy(scmRoot, manager, includeFileStatuses, repository)
            default:
                throw new ScmTaskException("Invalid value received for 'goal', " +
                    "expected one of ['lastCommit','localChanges','custom'], got $goal")
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
}
