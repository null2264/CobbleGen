//#if MC>=1.20.2
//$$ package io.github.null2264.cobblegen.mixin.network;

//$$ import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
//$$ import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//$$ @Mixin(ServerConfigurationPacketListenerImpl.class)
//$$ public abstract class ServerConfigurationPacketListenerMixin {
//$$     @Inject(method = "startConfiguration", at = @At("HEAD"))
//$$     private void syncCG(CallbackInfo ci) {
//$$         CGServerPlayNetworkHandler.trySync((ServerConfigurationPacketListenerImpl) (Object) this);
//$$     }
//$$ }
//#endif