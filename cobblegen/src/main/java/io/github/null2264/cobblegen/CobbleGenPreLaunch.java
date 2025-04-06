#if FABRIC
package io.github.null2264.cobblegen;

public class CobbleGenPreLaunch implements net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint {
    public CobbleGenPreLaunch() {
        #if MC>=12105
        io.github.null2264.cobblegen.gametest.CobbleGenTestLoader.init();
        #endif
    }

    @Override
    public void onPreLaunch() {}
}
#endif
