package io.github.null2264.cobblegen.fabric.mixin.core;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.fabric.util.PluginFinderFabric;
import io.github.null2264.cobblegen.forge.compat.LoaderCompatFabric;
import io.github.null2264.cobblegen.mixin.core.CobbleGenMixinPlugin;
import io.github.null2264.cobblegen.util.PluginFinder;

public class CobbleGenMixinPluginFabric extends CobbleGenMixinPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        LoaderCompat.init(new LoaderCompatFabric());
        PluginFinder.init(new PluginFinderFabric());
        super.onLoad(mixinPackage);
    }
}
