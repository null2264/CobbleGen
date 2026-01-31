plugins {
    id("java-library")
}

val commonJava by configurations.creating {
    isCanBeResolved = true
}
val commonResources by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":xplat"))
    commonJava(project(":xplat", "commonJava"))
    commonResources(project(":xplat", "commonResources"))
}

tasks.compileJava {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava)
    dependsOn(commonResources)
    from(commonJava)
    from(commonResources)
}

tasks.processResources {
    dependsOn(commonResources)
    from(commonResources)
}
