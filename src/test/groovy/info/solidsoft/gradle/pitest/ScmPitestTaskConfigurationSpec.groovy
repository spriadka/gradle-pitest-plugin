package info.solidsoft.gradle.pitest

class ScmPitestTaskConfigurationSpec extends BasicProjectBuilderSpec<ScmPitestPluginExtension> implements WithScmPitestTaskInitialization {
    def "should set scm repository correctly" () {
        given:
            project.scmPitest.scm.url = "https://hello-world.com"
        expect:
            scmPitestTask.scm.url == "https://hello-world.com"
    }

    def "should set basic parameters correctly" () {
        given:
            String[] targetClasses = ["sample","another-sample"]
            project.scmPitest.targetClasses = targetClasses
        and:
            Map<String, String> configurationMap = scmPitestTask.createTaskArgumentMap()
        expect:
            configurationMap['targetClasses'] == "sample,another-sample"
    }
}
