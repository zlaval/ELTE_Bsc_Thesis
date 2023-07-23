rootProject.name = "user-service"

pluginManagement {

    val preCommitHooks: String by settings
    val jibVersion: String by settings

    plugins {
        id("maven-publish")
        id("com.google.cloud.tools.jib") version jibVersion
        id("org.danilopianini.gradle-pre-commit-git-hooks") version preCommitHooks
    }
}


plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks")
}

gitHooks {
    commitMsg { conventionalCommits() }
    createHooks()
}