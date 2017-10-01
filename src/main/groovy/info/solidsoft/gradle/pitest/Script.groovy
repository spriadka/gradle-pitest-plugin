package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ChangeFile
import org.apache.maven.scm.ChangeSet
import org.apache.maven.scm.ScmFileSet
import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.ScmTag
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest
import org.apache.maven.scm.command.changelog.ChangeLogScmResult
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.apache.maven.scm.repository.ScmRepository

import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

GitExeScmProvider gitExeScmProvider = new GitExeScmProvider()
ScmManager manager = new BasicScmManager()
manager.setScmProvider("git", gitExeScmProvider)
ScmTag startVersion = new ScmTag("1.0.0")
ScmTag endVersion = new ScmTag("release/1.2.2")
ScmRepository repository = manager.makeScmRepository("scm:git:git@github.com/szpak/gradle-pitest-plugin.git")
ChangeLogScmRequest request = new ChangeLogScmRequest(repository, new ScmFileSet(new File("/home/spriadka/Documents/School/gradle-pitest-plugin")))
request.setStartRevision(startVersion)
request.setEndRevision(endVersion)
ChangeLogScmResult result = manager.changeLog(request)
List<String> modifiedClassFiles = new ArrayList<>()
for (ChangeSet changeSet : result.changeLog.changeSets) {
    for (ChangeFile file : changeSet.getFiles()) {
        if (file.action == ScmFileStatus.ADDED)
        modifiedClassFiles.add(file.name)
    }
}
modifiedClassFiles.stream().filter(new Predicate<String>() {
    @Override
    boolean test(String s) {
        return s.indexOf(".java") != -1
    }
}).collect(Collectors.toList()).forEach(new Consumer() {
    @Override
    void accept(Object o) {
        println(o.toString())
    }
})
