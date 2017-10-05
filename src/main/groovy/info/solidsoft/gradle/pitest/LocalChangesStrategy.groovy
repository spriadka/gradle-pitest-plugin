package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFile
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.command.status.StatusScmResult
import org.apache.maven.scm.manager.ScmManager

class LocalChangesScmStatistics extends AbstractScmChangeLogStatistics {

    LocalChangesScmStatistics(File scmRoot, ScmManager scmManager, Set<ScmFileStatus> includedFileStatuses, String repositoryUrl) {
        super(scmRoot, scmManager, includedFileStatuses, repositoryUrl)
    }

    @Override
    List<String> executeChangeLog() {
        StatusScmResult statusScmResult = scmManager.status(repository, scmRoot)
        for (ScmFile file : statusScmResult.changedFiles) {
            if (includingFileStatuses.contains(file.status)) {
                modifiedFileNames.add(file.path)
            }
        }
        return modifiedFileNames
    }
}
