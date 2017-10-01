package info.solidsoft.gradle.pitest

import groovy.transform.CompileStatic

@CompileStatic
class ScmPitestPluginExtension extends PitestPluginExtension {

    ScmConnection scm = new ScmConnection()
}
