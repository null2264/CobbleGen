package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.Comment;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static io.github.null2264.cobblegen.gametest.Constants.IS_GAMETEST_ENABLED;

public class ConfigMetaData implements Config {

    public static ConfigMetaData INSTANCE = new Factory().load();

    public static class Factory implements Config.Factory<ConfigMetaData> {

        private static final String NAME = "meta";
        private static final File PATH = new File(Config.path + File.separator + "cobblegen-meta.json5");

        @Override
        public ConfigMetaData load() {
            return ConfigHelper.loadConfig(
                NAME,
                false,
                PATH,
                null,
                ConfigMetaData::new,
                ConfigMetaData.class
            );
        }

        @Override
        public ConfigMetaData reload(ConfigMetaData workingConfig) {
            return ConfigHelper.loadConfig(
                NAME,
                true,
                PATH,
                workingConfig,
                ConfigMetaData::new,
                ConfigMetaData.class
            );
        }
    }

    @Comment(value="Enable Recipe Viewer support (EMI/REI/JEI)")
    @NotNull
    public Boolean enableRecipeViewer = true;
    
    @Comment(value="Enable Experimental Features")
    @NotNull
    public Boolean enableExperimentalFeatures = IS_GAMETEST_ENABLED;

    @Comment(value="Enable debug log, may spam your server console, but allows developer(s) to get extra context on what is going on")
    public Boolean debugLog = IS_GAMETEST_ENABLED;

    @Comment(value="Merge CobbleGen recipe categories into EMI's World Interaction category")
    @NotNull
    public Boolean mergeEMIRecipeCategory = true;

    @Comment(value="EMI related config, used when mergeEMIRecipeCategory is set to 'true'")
    public EMIData emi = new EMIData();

    @Comment(value="Create mod related config")
    public CreateData create = new CreateData();

    // TODO: Maybe allow this to be overwritten by generator config?
    @Comment(value="Whether or not to check other position for modifier block, default is 'false', meaning the mod will only check for modifier below the generated block")
    public Boolean lenientModifier = false;

    public static class EMIData {
        @Comment(value="Add CobbleGen tooltip")
        public Boolean addTooltip = true;

        @Comment(value="Remove overlapping recipe between CobbleGen and EMI")
        public Boolean removeOverlaps = true;

        @Comment(value="Invert input position")
        public Boolean invertInput = false;
    }

    public static class CreateData {
        @Comment(value="Load Create Integration")
        public Boolean loadIntegration = true;

        @Comment(value="Disable Create's pipe support")
        public Boolean disablePipe = false;
    }
}
