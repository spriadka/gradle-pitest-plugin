package info.solidsoft.gradle.pitest

class ScmPitestTaskConfigurationSpec extends BasicProjectBuilderSpec implements WithScmPitestTaskInitialization {
    def "should set scm repository correctly" () {
        given:
            project.scmPitest.scm.url = "https://hello-world.com"
        expect:
            scmPitestTask.scm.url == "https://hello-world.com"
    }
}
