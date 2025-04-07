import java.io.File
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtVariant
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.add
import net.benwoodworth.knbt.addNbtCompound
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.encodeToStream
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtList

fun generateStructure(binaryForm: Boolean): NbtCompound {
    return buildNbtCompound {
        put("DataVersion", 2730)
        putNbtList<NbtInt>("size") {
            add(8)
            add(8)
            add(8)
        }
        putNbtList("data") {
            for (i in 0..7) {
                for (j in 0..7) {
                    for (k in 0..7) {
                        addNbtCompound {
                            putNbtList("pos") {
                                add(i)
                                add(j)
                                add(k)
                            }
                            if (!binaryForm)
                                put("state", "minecraft:air")
                            else
                                put("state", 0)
                        }
                    }
                }
            }
        }
        putNbtList<NbtString>("entities") {}
        if (!binaryForm)
            putNbtList("palette") {
                add("minecraft:air")
            }
        else
            putNbtList("palette") {
                addNbtCompound {
                    put("Name", "minecraft:air")
                }
            }
    }
}

fun File.writeStructureAsSnbt(nbt: NbtCompound) {
    writeText(StringifiedNbt.encodeToString(NbtCompound.serializer(), nbt))
}

fun File.writeStructureAsNbt(nbt: NbtCompound) {
    outputStream().use { output ->
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.Gzip
        }.encodeToStream(buildNbtCompound { put("", nbt) }, output)
    }
}
