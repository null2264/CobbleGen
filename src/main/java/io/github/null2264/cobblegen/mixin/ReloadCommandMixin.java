package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.ConfigHelper;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(
            method = "tryReloadDataPacks(Ljava/util/Collection;Lnet/minecraft/server/command/ServerCommandSource;)V",
            at = @At("HEAD")
    )
    private static void reloadConfig(Collection<String> dataPacks, ServerCommandSource source, CallbackInfo ci) {
        try {
            ConfigHelper.load(false);
        } catch (Exception e) {
            LOGGER.error("Something happened when trying to reload config!", e);
        }
    }
}