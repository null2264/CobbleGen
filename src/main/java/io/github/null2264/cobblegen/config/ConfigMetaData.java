package io.github.null2264.cobblegen.config;

import blue.endless.jankson.Comment;

public class ConfigMetaData implements Config
{
    @Comment(value="Enable Experimental Features")
    public Boolean enableExperimentalFeatures = false;

    @Comment(value="Merge CobbleGen recipe categories into EMI's World Interaction category")
    public Boolean mergeEMIRecipeCategory = true;
}