package com.wonginnovations.oldresearch.common.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OldResearchPattParser {
    private static final Logger log = LogManager.getLogger("OldResearchPatterns");

    public static List<ResearchNotePattern> parse(ResourceLocation location) {
        JsonParser parser = new JsonParser();
        String s = "/assets/" + location.getNamespace() + "/" + location.getPath();
        List<ResearchNotePattern> patterns = new ArrayList<>();
        try (InputStream stream = OldResearchManager.class.getResourceAsStream(s)) {
            if (stream == null) {
                throw new RuntimeException("Input stream is null!");
            }
            InputStreamReader reader = new InputStreamReader(stream);
            JsonObject obj = parser.parse(reader).getAsJsonObject();
            JsonArray entries = obj.get("entries").getAsJsonArray();

            for (JsonElement element : entries) {
                JsonObject entry = element.getAsJsonObject();
                patterns.add(new ResearchNotePattern(
                    entry.get("target").getAsString(),
                    entry.get("note").getAsInt(),
                    parseAspects(entry.getAsJsonArray("aspects")),
                    entry.get("complexity").getAsInt(),
                    entry.has("hash-delta") ? entry.get("hash-delta").getAsInt() : 0
                ));
            }
        } catch (Exception e) {
            log.error("failed to parse file {}", location, e);
        }
        log.info("loaded {} note patterns from file {}", patterns.size(), location);
        return patterns;
    }

    private static AspectList parseAspects(JsonArray arr) {
        AspectList list = new AspectList();
        for (JsonElement element : arr) {
            String tag = element.getAsString();
            Aspect aspect = Aspect.getAspect(tag);
            if (aspect == null) {
                throw new IllegalStateException("unknown aspect [" + tag + "]");
            }
            list.add(aspect, 0);
        }
        return list;
    }
}
