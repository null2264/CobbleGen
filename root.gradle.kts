plugins {
    id("xyz.deftu.gradle.multiversion-root") version("1.17.0")
}

preprocess {
    val fabric12000 = createNode("1.20-fabric", 12000, "yarn")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")

    fabric12000.link(fabric11904)
    fabric11904.link(fabric11903)
    fabric11903.link(fabric11902)
    fabric11902.link(fabric11802)
}
