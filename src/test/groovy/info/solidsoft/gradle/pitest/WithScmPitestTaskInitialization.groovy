package info.solidsoft.gradle.pitest

import groovy.transform.SelfType

@SelfType(BasicProjectBuilderSpec)
trait WithScmPitestTaskInitialization {

    ScmPitestTask scmPitestTask

    def setup() {
        scmPitestTask = getJustOneScmPitestTaskOrFail()
    }
}
