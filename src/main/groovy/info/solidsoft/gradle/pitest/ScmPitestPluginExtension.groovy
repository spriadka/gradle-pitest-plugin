package info.solidsoft.gradle.pitest

import groovy.transform.CompileStatic

@CompileStatic
class ScmPitestPluginExtension extends PitestPluginExtension {

    ScmConnection scm = new ScmConnection()
    String goal
    File scmRoot
    String startScmVersionType
    String startScmVersion
    String endScmVersionType
    String endScmVersion
    Set<String> includeFileStatuses
    String connectionType

    void setScmRoot(String pathToScmRoot) {
        this.scmRoot = new File(pathToScmRoot)
    }
}
