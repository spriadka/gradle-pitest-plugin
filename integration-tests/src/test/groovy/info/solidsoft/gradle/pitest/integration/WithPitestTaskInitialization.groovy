package info.solidsoft.gradle.pitest.integration

import groovy.transform.SelfType
import info.solidsoft.gradle.pitest.task.PitestTask

@SelfType(BasicProjectBuilderSpec)
trait WithPitestTaskInitialization {

    PitestTask task

    def setup() {
        task = getJustOnePitestTaskOrFail()
    }
}
