package info.solidsoft.gradle.pitest

class ChangeLogStrategyFactory {
    static ChangeLogStrategy fromType(String value) {
        switch (value) {
            case 'lastCommit':
                return new LastCommitStrategy()
            case 'localChanges':
                return new LocalChangesStrategy()
            case 'custom':
                return new CustomChangeLogStrategy()
            default:
                throw new Exception()
        }
    }
}
