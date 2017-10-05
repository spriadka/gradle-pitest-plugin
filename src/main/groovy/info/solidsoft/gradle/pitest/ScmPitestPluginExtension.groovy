package info.solidsoft.gradle.pitest

import groovy.transform.CompileStatic
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.manager.ScmManager

import java.util.function.Function
import java.util.stream.Collectors

@CompileStatic
class ScmPitestPluginExtension extends PitestPluginExtension {

    ScmConnection scm = new ScmConnection()
    ChangeLogExecutor strategy
    File scmRoot
    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion
    Set<ScmFileStatus> includeFileStatuses

    void setIncludeFileStatuses(List<String> fileStatuses) {
        this.includeFileStatuses = fileStatuses.stream().map(new Function<String,ScmFileStatus> (){
            @Override
            ScmFileStatus apply(String s) {
                return getFileStatus(s)
            }
        }).collect(Collectors.toSet())
    }

    void setScmRoot(String pathToScmRoot) {
        this.scmRoot = new File(pathToScmRoot)
    }

    void setStrategy(String goalStrategy) {

    }

    private ScmFileStatus getFileStatus(String status) {
        switch (status) {
            case "added":
                return ScmFileStatus.ADDED
            case "modified":
                return ScmFileStatus.MODIFIED
            default:
                return ScmFileStatus.UNKNOWN
        }
    }
}
