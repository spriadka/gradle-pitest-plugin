package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepository

abstract class AbstractChangeLogStrategy implements ChangeLogExecutor {

    protected ScmFileSet scmRoot
    protected ScmManager scmManager
    protected ScmRepository repository
    protected Set<String> includingFileStatuses
    protected List<String> modifiedFileNames

    AbstractChangeLogStrategy(File scmRoot, ScmManager scmManager, Set<String> includingFileStatuses, ScmRepository repository) {
        this.scmRoot = new ScmFileSet(scmRoot)
        this.scmManager = scmManager
        this.includingFileStatuses = includingFileStatuses
        this.repository = repository
        this.modifiedFileNames = new ArrayList<>()
    }
}
