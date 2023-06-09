plugins {
    id("xyz.deftu.gradle.preprocess-root")
}

preprocess {
    val fabric12000 = createNode("1.20-fabric", 12000, "mojmap")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "mojmap")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "mojmap")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "mojmap")
    val fabric11901 = createNode("1.19.1-fabric", 11901, "mojmap")
    val fabric11900 = createNode("1.19-fabric", 11900, "mojmap")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "mojmap")
    val forge11802 = createNode("1.18.2-forge", 11802, "mojmap")

    fabric12000.link(fabric11904)
    fabric11904.link(fabric11903)
    fabric11903.link(fabric11902)
    fabric11902.link(fabric11901)
    fabric11901.link(fabric11900)
    fabric11900.link(fabric11802)
    fabric11802.link(forge11802)
}