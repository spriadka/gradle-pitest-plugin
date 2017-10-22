package info.solidsoft.gradle.pitest

import org.apache.maven.scm.ScmBranch
import org.apache.maven.scm.ScmRevision
import org.apache.maven.scm.ScmTag
import org.apache.maven.scm.ScmVersion
import org.apache.maven.scm.manager.ScmManager
import org.apache.maven.scm.provider.ScmProvider
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider

class ScmPitestTask extends AbstractPitestTask {

    final PropertyState<ScmConnection> scm
    final PropertyState<File> scmRoot
    final PropertyState<String> startScmVersionType
    final PropertyState<String> startScmVersion
    final PropertyState<String> endScmVersionType
    final PropertyState<String> endScmVersion
    final PropertyState<Set<String>> includeFileStatuses
    final PropertyState<ChangeLogStrategy> goal
    final PropertyState<String> connectionType
    final PropertyState<ScmManager> manager

    ScmPitestTask() {
        super()
        description = "Run PIT analysis for java classes using specified scm repository"
        scm = project.property(ScmConnection.class)
        scmRoot = project.property(File.class)
        startScmVersionType = project.property(String.class)
        startScmVersion = project.property(String.class)
        endScmVersionType = project.property(String.class)
        endScmVersion = project.property(String.class)
        includeFileStatuses = project.property(Set.class)
        goal = project.property(ChangeLogStrategy.class)
        connectionType = project.property(String.class)
        manager = project.property(ScmManager.class)
    }

    private void configureManager() {
        Map<String, ScmProvider> providerMap = new HashMap<>()
        providerMap.put("git", new GitExeScmProvider())
        providerMap.each { it ->
            this.manager.setScmProvider(it.key, it.value)
        }
    }

    @Override
    void exec() {
        configureManager()
        try {
            logger.info("SCM root is: $scmRoot.path")
            //ChangeLogStrategy changeLogExecutor = getExecutor()
            //setTargetClasses(changeLogExecutor.executeChangeLog() as Set)
            args = createListOfAllArgumentsForPit()
            jvmArgs = (getMainProcessJvmArgs() ?: getJvmArgs())
            main = "org.pitest.mutationtest.commandline.MutationCoverageReport"
            classpath = getLaunchClasspath()
            super.exec()
        } catch (ScmTaskException scme) {
            throw new ScmTaskException("Error when querying scm", scme)
        }
    }

    private static ScmVersion getScmVersion(String versionType, String version) {
        switch (versionType) {
            case "branch":
                return new ScmBranch(version)
            case "tag":
                return new ScmTag(version)
            case "revision":
                return new ScmRevision(version)
            default:
                throw new ScmTaskException("Unknown version type")
        }
    }

    private String getSCMRepository() {
        if (connectionType == null) {
            throw new ScmTaskException("Connection type must be specified")
        }
        final String connection = scm.connection
        if (this.connectionType.equalsIgnoreCase("connection")) {
            return connection
        }
        final String developerConnection = scm.developerConnection
        if (this.connectionType.equalsIgnoreCase("developerConnection")) {
            return developerConnection
        }
        throw new ScmTaskException("SCM Connection is not set, check your connection")
    }

    private void validateCustomStrategyArgumentsOrFail() {
        if ([startScmVersionType, startScmVersion, endScmVersionType, endScmVersion].every({it != null && !it.empty})) {
            return
        }
        throw new ScmTaskException("Invalid configuration for")
    }

    ScmConnection getScm() {
        return scm.getOrNull()
    }

    void setScm(ScmConnection value) {
        this.scm.set(value)
    }

    void setScm(Provider<ScmConnection> provider) {
        this.scm.set(provider)
    }

    ChangeLogStrategy getGoal() {
        return goal.getOrNull()
    }

    void setGoal(Provider<ChangeLogStrategy> provider) {
        this.goal.set(provider)
    }

    File getScmRoot() {
        return scmRoot.get()
    }

    void setScmRoot(String pathToScmRoot) {
        this.scmRoot.set(new File(pathToScmRoot))
    }

    void setScmRoot(Provider<File> provider) {
        this.scmRoot.set(provider)
    }

    String getStartScmVersionType() {
        return startScmVersionType.get()
    }

    void setStartScmVersionType(String value) {
        this.startScmVersionType.set(value)
    }

    void setStartScmVersionType(Provider<String> provider) {
        this.startScmVersionType.set(provider)
    }

    String getStartScmVersion() {
        return startScmVersion.getOrNull()
    }

    void setStartScmVersion(String value) {
        this.startScmVersion.set(value)
    }

    void setStartScmVersion(Provider<String> provider) {
        this.startScmVersion.set(provider)
    }

    String getEndScmVersionType() {
        return endScmVersionType.get()
    }

    void setEndScmVersionType(Provider<String> provider) {
        this.endScmVersionType.set(provider)
    }

    void setEndScmVersionType(String value) {
        this.endScmVersionType.set(value)
    }


    String getEndScmVersion() {
        return endScmVersion.get()
    }

    void setEndScmVersion(String value) {
        this.endScmVersion.set(value)
    }

    void setEndScmVersion(Provider<String> provider) {
        this.endScmVersion.set(provider)
    }

    Set<String> getIncludeFileStatuses() {
        return includeFileStatuses.isPresent() ? includeFileStatuses.get() : Collections.emptySet()
    }

    void setIncludeFileStatuses(Set<String> value) {
        this.includeFileStatuses.set(value)
    }

    void setIncludeFileStatuses(Provider<Set<String>> provider) {
        this.includeFileStatuses.set(provider)
    }

    String getConnectionType() {
        return connectionType.get()
    }

    void setConnectionType(String value) {
        this.connectionType.set(value)
    }

    void setConnectionType(Provider<String> provider) {
        this.connectionType.set(provider)
    }

    void setManager(Provider<ScmManager> provider) {
        this.manager.set(provider)
    }

    ScmManager getManager() {
        return manager.getOrNull()
    }
}
