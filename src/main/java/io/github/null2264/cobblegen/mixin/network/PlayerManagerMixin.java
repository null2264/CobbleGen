package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.SERVER;

@Environment(SERVER)
@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin
{
    @Inject(
            method = "reloadResources",
            at = @At(value = "INVOKE", target = "net/minecraft/network/protocol/game/ClientboundUpdateTagsPacket.<init>(Ljava/util/Map;)V")
    )
    public void syncOnReload(CallbackInfo ci) {
        //noinspection DataFlowIssue
        for (ServerPlayer player : ((PlayerList) (Object) this).getPlayers()) {
            CGServerPlayNetworkHandler.trySync(player.connection, true);
        }
    }
}