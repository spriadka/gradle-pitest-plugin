package info.solidsoft.gradle.pitest

import info.solidsoft.gradle.pitest.scm.ChangeLogException
import info.solidsoft.gradle.pitest.scm.LastCommitStrategy
import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.ScmResult
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.command.changelog.ChangeLogSet
import org.apache.maven.scm.manager.NoSuchScmProviderException
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.repository.ScmRepositoryException
import spock.lang.Specification

class LastCommitStrategyTest extends Specification {

    def managerMock = Mock(ScmManager)

    def "should throw exception when failure" () {
        given:
            LastCommitStrategy strategy = new LastCommitStrategy()
            managerMock.changeLog(_ as ChangeLogScmRequest) >> createFailingChangeLog()
        when:
            strategy.getModifiedFilenames(managerMock, null, null)
        then:
            thrown ChangeLogException
    }

    def "should throw exception with invalid url" () {
        given:
            LastCommitStrategy strategy = new LastCommitStrategy()
            managerMock.makeScmRepository(_) >> {throw new ScmRepositoryException("invalid url")}
        when:
            strategy.getModifiedFilenames(managerMock, null, null)
        then:
            thrown ChangeLogException
    }

    def "should throw exception with invalid provider" () {
        given:
            LastCommitStrategy strategy = new LastCommitStrategy()
            managerMock.makeScmRepository(_) >> {throw new NoSuchScmProviderException("invalid provider")}
        when:
            strategy.getModifiedFilenames(managerMock, null, null)
        then:
            thrown ChangeLogException
    }

    def "should return empty collection when no last commit present" () {
        given:
            managerMock.changeLog(_ as ChangeLogScmRequest) >> createChangeLogResult(Collections.emptyList())
            LastCommitStrategy strategy = new LastCommitStrategy()
        when:
            def result = strategy.getModifiedFilenames(managerMock, ['added'] as Set, null)
        then:
            result.isEmpty()
    }

    def "should return last commit files" () {
        given:
            LastCommitStrategy strategy = new LastCommitStrategy()
            managerMock.changeLog(_ as ChangeLogScmRequest) >> createChangeLogResult(
                Arrays.asList(createChangeSet(Arrays.asList(createChangeFile("custom",ScmFileStatus.ADDED))),
                createChangeSet(Arrays.asList(createChangeFile("precedingCommit", ScmFileStatus.ADDED)))))
        when:
            def result = strategy.getModifiedFilenames(managerMock, ['added'] as Set, null)
        then:
            result == ['custom']
    }

    private ChangeLogScmResult createChangeLogResult(List<ChangeSet> changeSets) {
        ChangeLogSet changeLogSet = new ChangeLogSet(changeSets, new Date(), new Date())
        ChangeLogScmResult result = new ChangeLogScmResult(changeLogSet, new ScmResult(null,
            null, null, true))
        return result
    }

    private ChangeSet createChangeSet(List<ChangeFile> files) {
        def result = new ChangeSet()
        result.setFiles(files)
        return result
    }

    private ChangeFile createChangeFile(String name, ScmFileStatus status) {
        def result = new ChangeFile(name)
        result.setAction(status)
        return result
    }

    private static ChangeLogScmResult createFailingChangeLog() {
        return new ChangeLogScmResult(null,null,null,false)
    }
}
