package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import net.fabricmc.api.Environment;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.SERVER;

@Environment(SERVER)
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin
{
    @Inject(
            method = "onDataPacksReloaded",
            at = @At(value = "INVOKE", target = "net/minecraft/network/packet/s2c/play/SynchronizeTagsS2CPacket.<init>(Ljava/util/Map;)V")
    )
    public void syncOnReload(CallbackInfo ci) {
        //noinspection DataFlowIssue
        for (ServerPlayerEntity player : ((PlayerManager) (Object) this).getPlayerList()) {
            CGServerPlayNetworkHandler.trySync(player.networkHandler);
        }
    }
}