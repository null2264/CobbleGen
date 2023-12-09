package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.Comment;
import org.jetbrains.annotations.NotNull;

public class ConfigMetaData implements Config
{
    @Comment(value="Enable Recipe Viewer support (EMI/REI/JEI)")
    @NotNull
    public Boolean enableRecipeViewer = true;
    
    @Comment(value="Enable Experimental Features")
    @NotNull
    public Boolean enableExperimentalFeatures = false;

    @Comment(value="Merge CobbleGen recipe categories into EMI's World Interaction category")
    @NotNull
    public Boolean mergeEMIRecipeCategory = true;

    @Comment(value="EMI related config, used when mergeEMIRecipeCategory is set to 'true'")
    public EMIData emi = new EMIData();

    @Comment(value="Create mod related config")
    public CreateData create = new CreateData();

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