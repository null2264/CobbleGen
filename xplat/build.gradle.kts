import dependencies.jei
import dependencies.minecraft as MC

plugins {
    id("java-library")
    id("org.spongepowered.gradle.vanilla")
}

val mcVersion: CGVer by extra

minecraft {
    version(MC.version(mcVersion))
    if (mcVersion.code >= 12105) {
        accessWideners(project.file("src/main/resources/cobblegen.accesswidener"))
    }
}

repositories {
    maven("https://repo.spongepowered.org/maven/")
}

configurations {
    register("xplatJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("xplatResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

dependencies {
    compileOnly(project(":stubs"))

    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")!!)

    compileOnly(jei(mcVersion, "", common = true, api = true).versioned(CGVer.wildcard()))
}

artifacts {
    add("xplatJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("xplatResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            artifactId = "MCLib"
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}

tasks.jar {
    manifest.attributes(mapOf(
        "Contains-Sources" to "java,class",
    ))
}
