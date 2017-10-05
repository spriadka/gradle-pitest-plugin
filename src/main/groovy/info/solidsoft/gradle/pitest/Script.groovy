package info.solidsoft.gradle.pitest


import org.apache.maven.scm.ScmFileStatus
import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider

import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

void printFiles(List<String> modifiedClassFiles) {
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
}

String scmRoot = "/home/spriadka/Documents/School/gradle-pitest-plugin"
String scmUrl = "scm:git:git@github.com/szpak/gradle-pitest-plugin.git"
GitExeScmProvider gitExeScmProvider = new GitExeScmProvider()
ScmManager manager = new BasicScmManager()
manager.setScmProvider("git", gitExeScmProvider)
CustomChangeLogStrategy statistics = new CustomChangeLogStrategy(new File(scmRoot), manager, [ScmFileStatus.ADDED] as Set,  scmUrl)
statistics.setStartScmVersion("1.0.0")
statistics.setStartScmVersionType("tag")
statistics.setEndScmVersionType("tag")
statistics.setEndScmVersion("release/1.2.2")
List<String> modifiedClassFiles = statistics.executeChangeLog()
println "CUSTOM::::::::::::::"
printFiles(modifiedClassFiles)
println "LOCAL CHANGES:::::::"
printFiles(new LocalChangesStrategy(new File(scmRoot), manager, [ScmFileStatus.ADDED] as Set, scmUrl).executeChangeLog())
println "LAST COMMIT::::::::::"
printFiles(new LastCommitStrategy(new File(scmRoot), manager, [ScmFileStatus.ADDED] as Set, scmUrl).executeChangeLog())

