plugins {
    id("java-library")
}

val xplatJava by configurations.creating {
    isCanBeResolved = true
}
val xplatResources by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":xplat"))
    xplatJava(project(":xplat", "xplatJava"))
    xplatResources(project(":xplat", "xplatResources"))
}

tasks.compileJava {
    dependsOn(xplatJava)
    source(xplatJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(xplatJava)
    dependsOn(xplatResources)
    from(xplatJava)
    from(xplatResources)
}

tasks.processResources {
    dependsOn(xplatResources)
    from(xplatResources)
}
