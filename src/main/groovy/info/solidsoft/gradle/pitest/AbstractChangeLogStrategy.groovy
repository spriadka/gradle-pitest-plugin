package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository

abstract class AbstractScmChangeLogStatistics implements ChangeLogExecutor {

    protected ScmFileSet scmRoot
    protected ScmManager scmManager
    protected ScmRepository repository
    protected Set<ScmFileStatus> includingFileStatuses
    protected List<String> modifiedFileNames

    protected AbstractScmChangeLogStatistics(File scmRoot, ScmManager scmManager, Set<ScmFileStatus> includingFileStatuses, String repository) {
        this.scmRoot = new ScmFileSet(scmRoot)
        this.scmManager = scmManager
        this.includingFileStatuses = includingFileStatuses
        this.repository = scmManager.makeScmRepository(repository)
        this.modifiedFileNames = new ArrayList<>()
    }
}
