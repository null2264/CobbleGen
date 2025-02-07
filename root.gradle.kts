plugins {
    id("io.github.null2264.preprocess")
}

preprocess {
    strictExtraMappings.set(true)

    val neo12103 = createNode("1.21.3-neoforge", 12102, "mojang")
    val fabric12103 = createNode("1.21.3-fabric", 12102, "mojang")
    val neo12101 = createNode("1.21.1-neoforge", 12100, "mojang")
    val fabric12101 = createNode("1.21.1-fabric", 12100, "mojang")
    val neo12006 = createNode("1.20.6-neoforge", 12005, "mojang")
    val fabric12006 = createNode("1.20.6-fabric", 12005, "mojang")
    val neo12004 = createNode("1.20.4-neoforge", 12004, "mojang")  // Special split for Neo, since they refactor Network API again on newer version.
    val neo12002 = createNode("1.20.2-neoforge", 12002, "mojang")
    val fabric12002 = createNode("1.20.2-fabric", 12002, "mojang")
    val forge12001 = createNode("1.20.1-forge", 12001, "mojang")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "mojang")
    val forge11904 = createNode("1.19.4-forge", 11904, "mojang")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "mojang")
    val forge11902 = createNode("1.19.2-forge", 11902, "mojang")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "mojang")
    val forge11802 = createNode("1.18.2-forge", 11802, "mojang")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "mojang")
    val forge11605 = createNode("1.16.5-forge", 11605, "mojang")
    val fabric11605 = createNode("1.16.5-fabric", 11605, "mojang")

    neo12103.link(fabric12103)
    neo12101.link(fabric12101)
    neo12006.link(fabric12006)
    neo12004.link(neo12002)  // Special split for Neo, since they refactor Network API again on newer version.
    neo12002.link(fabric12002)

    forge12001.link(fabric12001)
    forge11904.link(fabric11904)
    forge11902.link(fabric11902)
    forge11802.link(fabric11802)
    forge11605.link(fabric11605)

    fabric12103.link(fabric12101, file("versions/mapping-fabric-1.21.3-1.21(.1).txt"))
    fabric12101.link(fabric12006)
    fabric12006.link(fabric12002)
    fabric12002.link(fabric12001)
    fabric12001.link(fabric11904)
    fabric11904.link(fabric11902)
    fabric11902.link(fabric11802)
    fabric11802.link(fabric11605)
}

subprojects {
    val loader = when {
        name.endsWith("-forge") -> "forge"
        name.endsWith("-fabric") -> "fabric"
        name.endsWith("-neoforge") -> "neoforge"
        else -> ""
    }
    if (loader == "") throw java.lang.IllegalStateException("Invalid modloader")
    extra.set("loom.platform", loader)
}
