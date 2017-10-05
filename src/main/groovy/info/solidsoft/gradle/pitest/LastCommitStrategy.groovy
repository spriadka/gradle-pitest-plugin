package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.manager.ScmManager

class LastCommitStrategy extends AbstractChangeLogStrategy {
    LastCommitStrategy(File scmRoot, ScmManager scmManager, Set<ScmFileStatus> includingFileStatuses, String repository) {
        super(scmRoot, scmManager, includingFileStatuses, repository)
    }

    @Override
    List<String> executeChangeLog() {
        ChangeLogScmRequest request = new ChangeLogScmRequest(repository, scmRoot)
        request.setLimit(1)
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
}
