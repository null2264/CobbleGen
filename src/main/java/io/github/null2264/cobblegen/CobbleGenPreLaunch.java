package io.github.null2264.cobblegen;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class CobbleGenPreLaunch implements PreLaunchEntrypoint
{
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}