package info.solidsoft.gradle.pitest

class ScmConnection {
    String connection
    String developerConnection
    String tag
    String url

    void setUrl(String url) {
        this.url = url
    }
}
