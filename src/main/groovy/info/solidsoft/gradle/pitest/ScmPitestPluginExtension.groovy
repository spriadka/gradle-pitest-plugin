package info.solidsoft.gradle.pitest

class ScmPitestPluginExtension extends PitestPluginExtension {
    ScmConnection scm

    ScmPitestPluginExtension() {
        scm = new ScmConnection()
    }
}
