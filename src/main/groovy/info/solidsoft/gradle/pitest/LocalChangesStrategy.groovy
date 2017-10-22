package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFile
import org.apache.maven.scm.command.status.StatusScmResult
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository

class LocalChangesStrategy extends AbstractChangeLogStrategy {

    LocalChangesStrategy() {
        super()
    }

    LocalChangesStrategy(File scmRoot, ScmManager scmManager,
                         Set<String> includedFileStatuses, ScmRepository repositoryUrl) {
        super(scmRoot, scmManager, includedFileStatuses, repositoryUrl)
    }

    @Override
    List<String> executeChangeLog() {
        StatusScmResult statusScmResult = scmManager.status(repository, scmRoot)
        for (ScmFile file : statusScmResult.changedFiles) {
            if (includingFileStatuses.contains(file.status.toString())) {
                modifiedFileNames.add(file.path)
            }
        }
        return modifiedFileNames
    }
}
