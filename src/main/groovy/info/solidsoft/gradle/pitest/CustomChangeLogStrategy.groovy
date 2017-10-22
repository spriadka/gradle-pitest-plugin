package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmVersion
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository

class CustomChangeLogStrategy extends AbstractChangeLogStrategy {

    ScmVersion startScmVersion
    ScmVersion endScmVersion

    CustomChangeLogStrategy() {
        super()
    }

    CustomChangeLogStrategy(File scmRoot, ScmManager scmManager,
                            Set<String> includingFileStatuses, ScmRepository repository) {
        super(scmRoot, scmManager, includingFileStatuses, repository)
    }

    CustomChangeLogStrategy(File scmRoot, ScmManager scmManager,
                            Set<String> includingFileStatuses, ScmRepository repository,
                            ScmVersion startScmVersion, ScmVersion endScmVersion) {
        super(scmRoot, scmManager, includingFileStatuses, repository)
        this.startScmVersion = startScmVersion
        this.endScmVersion = endScmVersion
    }

    @Override
    List<String> executeChangeLog() {
        ChangeLogScmRequest request = new ChangeLogScmRequest(repository, scmRoot)
        request.setStartRevision(startScmVersion)
        request.setEndRevision(endScmVersion)
        ChangeLogScmResult result = scmManager.changeLog(request)
        if (!result.isSuccess()) {
            return Collections.emptyList()
        }
        result.changeLog.changeSets.each { ChangeSet changeSet ->
            changeSet.getFiles().each { ChangeFile changeFile ->
                if (includingFileStatuses.contains(changeFile.action.toString())) {
                    modifiedFileNames.add(changeFile.name)
                }
            }
        }
        return modifiedFileNames
    }
}