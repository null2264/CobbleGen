plugins {
    id("com.github.null2264.preprocess")
}

preprocess {
    val fabric12001 = createNode("1.20.1-fabric", 12001, "mojang")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "mojang")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "mojang")
    val forge11902 = createNode("1.19.2-forge", 11902, "mojang")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "mojang")
    val forge11802 = createNode("1.18.2-forge", 11802, "mojang")

    fabric12001.link(fabric11904)
    fabric11904.link(fabric11902)
    fabric11902.link(forge11902)
    forge11902.link(fabric11802)
    fabric11802.link(forge11802)
}