plugins {
    id("yalmm")
    `maven-publish`
}

val minecraftVersion = yalmm.Constants.getMinecraftVersion(project)
version = "$minecraftVersion+build.${System.getenv().getOrDefault("BUILD_NUMBER", "local")}"
base.archivesName.set("mappings")

repositories {
    maven {
        name = "Quilt Maven"
        url = uri("https://maven.quiltmc.org/repository/release/")
    }
}

dependencies {
    intermediaryMappings("net.fabricmc:intermediary:$minecraftVersion")

    enigmaRuntime("cuchaz:enigma-gui:2.5.2")
    enigmaRuntime("org.ow2.asm:asm:9.7.1")
    enigmaRuntime("org.quiltmc:quilt-json5:1.0.3")
}
