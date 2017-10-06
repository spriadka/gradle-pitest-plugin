package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmBranch
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.ScmRevision
import org.apache.maven.scm.ScmTag
import org.apache.maven.scm.ScmVersion
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository

class CustomChangeLogStrategy extends AbstractChangeLogStrategy {

    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion

    CustomChangeLogStrategy(File scmRoot, ScmManager scmManager, Set<ScmFileStatus> includingFileStatuses, ScmRepository repository) {
        super(scmRoot, scmManager, includingFileStatuses, repository)
    }

    @Override
    List<String> executeChangeLog() {
        ChangeLogScmRequest request = new ChangeLogScmRequest(repository, scmRoot)
        request.setStartRevision(getScmVersion(startScmVersionType, startScmVersion))
        request.setEndRevision(getScmVersion(endScmVersionType, endScmVersion))
        ChangeLogScmResult result = scmManager.changeLog(request)
        if (!result.isSuccess()) {
            return Collections.emptyList()
        }
        result.changeLog.changeSets.each { ChangeSet changeSet ->
            changeSet.getFiles().each { ChangeFile changeFile ->
                if (includingFileStatuses.contains(changeFile.action)) {
                    modifiedFileNames.add(changeFile.name)
                }
            }
        }
        return modifiedFileNames
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
                throw new UnsupportedScmVersionException("Unknown version type")
        }
    }
}
