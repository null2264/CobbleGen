plugins {
    id("org.spongepowered.gradle.vanilla")
}

minecraft {
    version(project.ext["mcVersionStr"] as String)
}

repositories {
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    // Mostly for Mixin and Arch's dependant on Fabric's @Environment annotations
    compileOnly("net.fabricmc:fabric-loader:0.17.2")
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
