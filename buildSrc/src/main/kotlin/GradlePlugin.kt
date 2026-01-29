sealed class GradlePlugin(val id: String, val isLegacy: Boolean) {
    object LegacyLoom : GradlePlugin("net.fabricmc.fabric-loom-remap", true)
    object Loom : GradlePlugin("net.fabricmc.fabric-loom", false)
    object ArchLoom : GradlePlugin("io.github.null2264.architectury-loom", true)
    object MDG : GradlePlugin("net.neoforged.moddev", false)
    object LegacyMDG : GradlePlugin("net.neoforged.moddev.legacyforge", true)

    fun isLoom() = this is LegacyLoom || this is Loom || this is ArchLoom
    fun isLegacyLoom() = isLoom() && isLegacy
}
