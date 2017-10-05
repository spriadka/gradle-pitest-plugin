package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider

import javax.inject.Inject

class ScmPitestTask extends AbstractPitestTask {

    ScmConnection scm = new ScmConnection()
    File scmRoot
    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion
    Set<ScmFileStatus> includeFileStatuses
    ChangeLogExecutor strategy

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
        args = createListOfAllArgumentsForPit()
        jvmArgs = (getMainProcessJvmArgs() ?: getJvmArgs())
        main = "org.pitest.mutationtest.commandline.MutationCoverageReport"
        classpath = getLaunchClasspath()
    }
}
