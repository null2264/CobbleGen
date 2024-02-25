plugins {
    id("io.github.null2264.preprocess")
}

preprocess {
    val neo12002 = createNode("1.20.2-neoforge", 12002, "mojang")
    val fabric12002 = createNode("1.20.2-fabric", 12002, "mojang")
    val forge12001 = createNode("1.20.1-forge", 12001, "mojang")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "mojang")
    val forge11902 = createNode("1.19.2-forge", 11902, "mojang")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "mojang")
    val forge11802 = createNode("1.18.2-forge", 11802, "mojang")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "mojang")
    val forge11605 = createNode("1.16.5-forge", 11605, "mojang")
    val fabric11605 = createNode("1.16.5-fabric", 11605, "mojang")

    neo12002.link(fabric12002)

    forge12001.link(fabric12001)
    forge11902.link(fabric11902)
    forge11802.link(fabric11802)
    forge11605.link(fabric11605)

    fabric12002.link(fabric12001)
    fabric12001.link(fabric11902)
    fabric11902.link(fabric11802)
    fabric11802.link(fabric11605)
}