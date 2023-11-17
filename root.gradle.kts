plugins {
    id("com.github.null2264.preprocess")
}

preprocess {
    val neo12002 = createNode("1.20.2-neoforge", 12002, "mojang")
    val forge12002 = createNode("1.20.2-forge", 12002, "mojang")
    val fabric12002 = createNode("1.20.2-fabric", 12002, "mojang")
    val forge12001 = createNode("1.20.1-forge", 12001, "mojang")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "mojang")
    val forge11904 = createNode("1.19.4-forge", 11904, "mojang")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "mojang")
    val forge11902 = createNode("1.19.2-forge", 11902, "mojang")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "mojang")
    val forge11802 = createNode("1.18.2-forge", 11802, "mojang")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "mojang")

    neo12002.link(fabric12002)

    forge12002.link(fabric12002)
    forge12001.link(fabric12001)
    forge11904.link(fabric11904)
    forge11902.link(fabric11902)
    forge11802.link(fabric11802)

    fabric12002.link(fabric12001)
    fabric12001.link(fabric11904)
    fabric11904.link(fabric11902)
    fabric11902.link(fabric11802)
}