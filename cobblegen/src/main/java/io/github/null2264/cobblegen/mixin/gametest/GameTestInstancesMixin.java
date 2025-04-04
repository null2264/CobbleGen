#if MC>=12105
package io.github.null2264.cobblegen.mixin.gametest;

import io.github.null2264.cobblegen.gametest.CobbleGenTestFunctions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.gametest.framework.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameTestInstances.class)
public interface GameTestInstancesMixin {

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void injectInstance(BootstrapContext<GameTestInstance> bootstrapContext, CallbackInfo ci) {
        CobbleGenTestFunctions.injectInstance(bootstrapContext);
    }
}
#endif
