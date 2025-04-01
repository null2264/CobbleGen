plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    //accessWideners(project(":mclib").file(""))
    version(project.ext["mcVersionStr"] as String)
}

repositories {
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    // In case I'll be doing mixin
    compileOnly("org.spongepowered:mixin:0.8.5")
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
    manifest {
        attributes["Contains-Sources"] = "java,class"
    }
}
