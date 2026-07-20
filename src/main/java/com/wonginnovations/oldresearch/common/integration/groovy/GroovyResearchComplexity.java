package com.wonginnovations.oldresearch.common.integration.groovy;

import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.wonginnovations.oldresearch.common.research.DefaultResearchComplexity;
import groovy.lang.Closure;
import net.minecraft.entity.player.EntityPlayer;

public class GroovyResearchComplexity extends DefaultResearchComplexity {

    Closure<Integer> function;

    public GroovyResearchComplexity(Closure<Integer> func) {
        function = func;
    }

    @Override
    public int calculateComplexity(EntityPlayer player, String key) {
        if (function == null) return super.calculateComplexity(player, key);
        int groovy = ClosureHelper.call(super.calculateComplexity(player, key), function, player, key);
        return groovy > 0 ? groovy : super.calculateComplexity(player, key);
    }

}
