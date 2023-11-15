package io.github.null2264.cobblegen.mixin.network;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
//#if MC>=1.20.2
//$$ import net.minecraft.network.chat.Component;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
//#endif

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    //#if MC>=1.20.2
    //$$ @Shadow public abstract boolean isConnected();

    //$$ @Inject(method = "disconnect", at = @At("TAIL"))
    //$$ private void disconnect(Component component, CallbackInfo ci) {
    //$$     if (this.isConnected())
    //$$         FLUID_INTERACTION.disconnect();
    //$$ }
    //#endif
}