import dependencies.minecraft as MC

plugins {
    id("org.spongepowered.gradle.vanilla")
}

minecraft {
    //accessWideners(project(":mclib").file(""))
    version(MC.version(project.ext["mcVersion"] as Int))
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
            artifactId = "CGStubs"
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
