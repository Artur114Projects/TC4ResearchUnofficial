package com.wonginnovations.oldresearch.integration.groovy;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.wonginnovations.oldresearch.OldResearch;
import org.jetbrains.annotations.NotNull;

public class GroovyScriptModule implements GroovyPlugin {

    public final GroovyRegistry registry = new GroovyRegistry();

    @Override
    public @NotNull String getModId() {
        return OldResearch.MODID;
    }

    @Override
    public @NotNull String getContainerName() {
        return getModId();
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        container.addPropertiesOfFields(this, false);
    }
}
