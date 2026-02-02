package io.github.null2264.cobblegen.forge.mixin.core;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.forge.compat.LoaderCompatForge;
import io.github.null2264.cobblegen.forge.util.PluginFinderForge;
import io.github.null2264.cobblegen.mixin.core.CobbleGenMixinPlugin;
import io.github.null2264.cobblegen.util.PluginFinder;

public class CobbleGenMixinPluginForge extends CobbleGenMixinPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        LoaderCompat.init(new LoaderCompatForge());
        PluginFinder.init(new PluginFinderForge());
        super.onLoad(mixinPackage);
    }
}
