package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.ConfigHelper;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {
    @Inject(
            method = "tryReloadDataPacks(Ljava/util/Collection;Lnet/minecraft/server/command/ServerCommandSource;)V",
            at = @At("HEAD")
    )
    private static void reloadConfig(Collection<String> dataPacks, ServerCommandSource source, CallbackInfo ci) {
        try {
            Objects.requireNonNull(source.getPlayer()).sendMessage(Text.of("Reloading config file..."));
            ConfigHelper.load();
        } catch (Exception e) {
            Objects.requireNonNull(source.getPlayer()).sendMessage(Text.of("Failed to reload config file!"));
        }
    }
}