package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.CobbleGen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class CGJEIPlugin implements IModPlugin
{
    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier(CobbleGen.MOD_ID, "plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }
}