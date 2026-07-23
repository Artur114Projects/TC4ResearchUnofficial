package com.wonginnovations.oldresearch.common.integration.groovy;

import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.wonginnovations.oldresearch.common.research.DefaultResearchComplexity;
import groovy.lang.Closure;

public class GroovyResearchComplexity extends DefaultResearchComplexity {

    Closure<Integer> function;

    public GroovyResearchComplexity(Closure<Integer> func) {
        function = func;
    }

    @Override
    public int calculateComplexity(String key) {
        if (function == null) return super.calculateComplexity(key);
        int groovy = ClosureHelper.call(super.calculateComplexity(key), function, key);
        return groovy > 0 ? groovy : super.calculateComplexity(key);
    }

}
