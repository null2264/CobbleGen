package io.github.null2264.cobblegen.mixin;

import com.mojang.brigadier.CommandDispatcher;
import io.github.null2264.cobblegen.CobbleGen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerCustomCommands(
            Commands.CommandSelection commandSelection,
            //#if MC>1.18.2
            //$$ net.minecraft.commands.CommandBuildContext commandBuildContext,
            //#endif
            CallbackInfo ci
    ) {
        CobbleGen.initCommands(this.dispatcher);
    }
}