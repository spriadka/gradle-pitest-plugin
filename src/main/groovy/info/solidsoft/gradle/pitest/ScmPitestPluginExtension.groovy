package info.solidsoft.gradle.pitest

import org.apache.maven.scm.manager.BasicScmManager
import org.apache.maven.scm.manager.ScmManager
import org.gradle.api.Project
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider

class ScmPitestPluginExtension extends PitestPluginExtension {

    final PropertyState<ScmManager> manager
    final PropertyState<ScmConnection> scm
    final PropertyState<ChangeLogStrategy> goal
    final PropertyState<File> scmRoot
    final PropertyState<String> startScmVersionType
    final PropertyState<String> startScmVersion
    final PropertyState<String> endScmVersionType
    final PropertyState<String> endScmVersion
    final PropertyState<Set<String>> includeFileStatuses
    final PropertyState<String> connectionType


    ScmPitestPluginExtension(Project project) {
        super(project)
        scm = project.property(ScmConnection.class)
        goal = project.property(ChangeLogStrategy.class)
        scmRoot = project.property(File.class)
        startScmVersionType = project.property(String.class)
        startScmVersion = project.property(String.class)
        endScmVersionType = project.property(String.class)
        endScmVersion = project.property(String.class)
        includeFileStatuses = project.property(Set.class)
        connectionType = project.property(String.class)
        manager = project.property(ScmManager.class)
        setManager(new BasicScmManager())
        setScm(new ScmConnection())
        setIncludeFileStatuses(Collections.emptySet())
    }

    Provider<ScmManager> getScmManagerProvider() {
        return manager
    }

    ScmManager getManager() {
        return manager.getOrNull()
    }

    void setManager(ScmManager scmManager) {
        this.manager.set(scmManager)
    }

    Provider<ScmConnection> getScmProvider() {
        return scm
    }

    ScmConnection getScm() {
        return scm.getOrNull()
    }

    void setScm(ScmConnection value) {
        this.scm.set(value)
    }

    Provider<ChangeLogStrategy> getGoalProvider() {
        return goal
    }

    void setGoal(String value) {
        this.goal.set(ChangeLogStrategyFactory.fromType(value))
    }

    void setGoal(ChangeLogStrategy value) {
        this.goal.set(value)
    }

    Provider<File> getScmRootProvider() {
        return scmRoot
    }

    File getScmRoot() {
        return scmRoot.getOrNull()
    }

    void setScmRoot(String pathToScmRoot) {
        this.scmRoot.set(new File(pathToScmRoot))
    }

    Provider<String> getStartScmVersionTypeProvider() {
        return startScmVersionType
    }

    void setStartScmVersionType(String value) {
        this.startScmVersionType.set(value)
    }

    Provider<String> getStartScmVersionProvider() {
        return startScmVersion
    }

    void setStartScmVersion(String value) {
        this.startScmVersion.set(value)
    }

    Provider<String> getEndScmVersionTypeProvider() {
        return endScmVersionType
    }

    void setEndScmvVersion(String value) {
        this.endScmVersionType.set(value)
    }

    Provider<String> getEndScmVersionProvider() {
        return endScmVersion
    }


    void setEndScmVersion(String value) {
        this.endScmVersion.set(value)
    }

    Provider<Set<String>> getIncludeFileStatusesProvider() {
        return includeFileStatuses
    }

    Set<String> getIncludeFileStatuses() {
        return includeFileStatuses.getOrNull()
    }

    void setIncludeFileStatuses(Set<String> value) {
        this.includeFileStatuses.set(value)
    }

    Provider<String> getConnectionTypeProvider() {
        return connectionType
    }

    void setConnectionType(String value) {
        this.connectionType.set(value)
    }
}
