package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmBranch
import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.ScmRevision
import org.apache.maven.scm.ScmTag
import org.apache.maven.scm.ScmVersion
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.repository.ScmRepository

class ScmPitestTask extends AbstractPitestTask {

    ScmConnection scm = new ScmConnection()
    String scmRoot
    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion

    private void analyse() {
        ScmManager scmManager = new BasicScmManager()
        GitExeScmProvider gitExeScmProvider = new GitExeScmProvider()
        scmManager.setScmProvider("git", gitExeScmProvider)
        ScmRepository scmRepository = scmManager.makeScmRepository(scm.getUrl())
        ChangeLogScmRequest changeLogScmRequest = new ChangeLogScmRequest(scmRepository, new ScmFileSet(new File(scmRoot)))
        changeLogScmRequest.setStartRevision(getScmVersion(startScmVersionType, startScmVersion))
        changeLogScmRequest.setEndRevision(getScmVersion(endScmVersionType, endScmVersion))
        ChangeLogScmResult result = scmManager.changeLog(changeLogScmRequest)
        List<String> modifiedClasses = new ArrayList<>()
        for (ChangeSet set : result.getChangeLog().getChangeSets()) {
            for (ChangeFile file : set.getFiles()) {
                if (file.action in [ScmFileStatus.ADDED, ScmFileStatus.MODIFIED] ) {
                    modifiedClasses.add(file.getName())
                }
            }
        }
        targetClasses = modifiedClasses as Set
    }

    private ScmVersion getScmVersion(String versionType, String version) {
        switch (versionType) {
            case "branch":
                return new ScmBranch(version)
            case "tag":
                return new ScmTag(version)
            case "revision":
                return new ScmRevision(version)
            default:
                throw new UnsupportedScmVersionException("Unknown version type")
        }
    }

    @Override
    protected void executePitestReport() {
        analyse()
        args = createListOfAllArgumentsForPit()
        jvmArgs = (getMainProcessJvmArgs() ?: getJvmArgs())
        main = "org.pitest.mutationtest.commandline.MutationCoverageReport"
        classpath = getLaunchClasspath()
    }
}
