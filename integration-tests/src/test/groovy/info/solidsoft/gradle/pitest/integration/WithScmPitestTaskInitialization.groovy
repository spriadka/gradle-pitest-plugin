package info.solidsoft.gradle.pitest.integration

import groovy.transform.SelfType
import info.solidsoft.gradle.pitest.task.ScmPitestTask

@SelfType(BasicProjectBuilderSpec)
trait WithScmPitestTaskInitialization {

    ScmPitestTask scmPitestTask

    def setup() {
        scmPitestTask = getJustOneScmPitestTaskOrFail()
    }
}
