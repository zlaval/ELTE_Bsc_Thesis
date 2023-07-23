rootProject.name = "api-gateway"

pluginManagement {

    val springBootVersion: String by settings
    val springDependencyManagement: String by settings
    val jibVersion: String by settings
    val kotlinVersion: String by settings
    val preCommitHooks: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagement
        id("com.google.cloud.tools.jib") version jibVersion
        id("maven-publish")
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
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
