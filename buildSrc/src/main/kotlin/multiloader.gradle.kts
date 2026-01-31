plugins {
    id("java-library")
}

dependencies {
    compileOnly(project(":mclib"))
}

tasks.compileJava {
    source(project(":mclib").sourceSets.main.get().allSource)
}

//tasks.sourcesJar {
//    from(project(":mclib").sourceSets.main.get().allJava)
//}

tasks.processResources {
    from(project(":mclib").sourceSets.main.get().resources)
}
