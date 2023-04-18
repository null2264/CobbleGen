package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.CobbleGen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;

public class CGJEIPlugin implements IModPlugin
{
    @Override
    public net.minecraft.resources.ResourceLocation getPluginUid() {
        return new net.minecraft.resources.ResourceLocation(CobbleGen.MOD_ID, "plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }
}