/* Copyright (c) 2017 Marcin Zajączkowski
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.solidsoft.gradle.pitest.integration

import groovy.transform.PackageScope
import info.solidsoft.gradle.pitest.PluginConstants
import info.solidsoft.gradle.pitest.extension.PitestPluginExtension
import info.solidsoft.gradle.pitest.extension.ScmPitestPluginExtension
import info.solidsoft.gradle.pitest.task.PitestTask
import info.solidsoft.gradle.pitest.task.ScmPitestTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * @see WithPitestTaskInitialization
 */
@PackageScope
abstract class BasicProjectBuilderSpec extends Specification {

    @Rule
    public TemporaryFolder tmpProjectDir = new TemporaryFolder()

    protected Project project
    protected PitestPluginExtension pitestConfig
    protected ScmPitestPluginExtension scmPitestConfig

    //TODO: There is a regression in 2.14.1 with API jar regeneration for every test - https://discuss.gradle.org/t/performance-regression-in-projectbuilder-in-2-14-and-3-0/18956
    //https://github.com/gradle/gradle/commit/3216f07b3acb4cbbb8241d8a1d50b8db9940f37e
    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.apply(plugin: "java")   //to add SourceSets
        project.apply(plugin: "info.solidsoft.pitest")

        pitestConfig = project.getExtensions().getByType(PitestPluginExtension)
        scmPitestConfig = project.extensions.getByType(ScmPitestPluginExtension)
        project.group = 'test.group'
    }

    protected PitestTask getJustOnePitestTaskOrFail() {
        Set<Task> tasks = project.getTasksByName(PluginConstants.PITEST_TASK_NAME, false) //forces "afterEvaluate"
        assert tasks?.size() == 1 : "Expected tasks: '$PluginConstants.PITEST_TASK_NAME', All tasks: ${project.tasks}"
        assert tasks[0] instanceof PitestTask
        return (PitestTask)tasks[0]
    }

    protected ScmPitestTask getJustOneScmPitestTaskOrFail() {
        Set<Task> tasks = project.getTasksByName(PluginConstants.SCM_PITEST_TASK_NAME, false)
        assert tasks?.size() == 1 : "Expected tasks: '$PluginConstants.SCM_PITEST_TASK_NAME', All tasks: ${project.tasks}"
        assert tasks[0] instanceof ScmPitestTask
        return (ScmPitestTask)tasks[0]
    }
}
