pluginManagement {
	plugins {
		id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.0.22"
	}
}

rootProject.name = "video-processor"

plugins {
	id("org.danilopianini.gradle-pre-commit-git-hooks")
}

gitHooks {
	commitMsg { conventionalCommits() }
	createHooks()
}
