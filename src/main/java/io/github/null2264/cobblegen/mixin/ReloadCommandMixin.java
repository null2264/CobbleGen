package io.github.null2264.cobblegen.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.null2264.cobblegen.config.ConfigHelper;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin
{
    @Inject(method = "tryReloadDataPacks(Ljava/util/Collection;Lnet/minecraft/server/command/ServerCommandSource;)V", at = @At("HEAD"))
    private static void reloadConfig(
            Collection<String> dataPacks,
            ServerCommandSource source,
            CallbackInfo ci
    ) throws CommandSyntaxException {
        try {
            source.getPlayer().sendMessage(Text.of("Reloading config file..."), false);
            ConfigHelper.load(true);
        } catch (Exception e) {
            source.getPlayer().sendMessage(Text.of("Failed to reload config file!"), false);
        }
    }
}