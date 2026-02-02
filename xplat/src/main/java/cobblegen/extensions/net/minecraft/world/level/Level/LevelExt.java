package cobblegen.extensions.net.minecraft.world.level.Level;

#if MC>12101
import manifold.ext.rt.api.This;
import net.minecraft.world.level.Level;
#endif

@manifold.ext.rt.api.Extension
public final class LevelExt {
    #if MC>12101
    public static int getMinBuildHeight(@This Level thiz) {
        return thiz.getMinY();
    }

    public static int getMaxBuildHeight(@This Level thiz) {
        return thiz.getMaxY();
    }
    #endif
}
