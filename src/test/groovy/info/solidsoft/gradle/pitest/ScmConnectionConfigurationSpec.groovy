package info.solidsoft.gradle.pitest

class ScmConnectionConfigurationSpec extends BasicProjectBuilderSpec<ScmPitestPluginExtension> implements WithScmPitestTaskInitialization{
    def "should set url correctly" () {
        given:
            project.scmPitest.scm.url = "https://hello-world.com"
        expect:
            scmPitestTask.scm.url == "https://hello-world.com"
    }

    def "should set tag correctly" () {
        given:
            project.scmPitest.scm.tag = "HEAD"
        and:
            ScmConnection scmConnection = scmPitestTask.scm
        expect:
            scmConnection.tag == "HEAD"
    }

    def "should set connection correctly" () {
        given:
            project.scmPitest.scm.connection = "scm:git:fancy-connection"
        and:
            ScmConnection scmConnection = scmPitestTask.scm
        expect:
            scmConnection.connection == "scm:git:fancy-connection"
    }

    def "should set developer connection correctly" () {
        given:
            project.scmPitest.scm.developerConnection = "scm:git:fancy-developer-connection"
        and:
            ScmConnection scmConnection = scmPitestTask.scm
        expect:
            scmConnection.developerConnection == "scm:git:fancy-developer-connection"
    }
}
