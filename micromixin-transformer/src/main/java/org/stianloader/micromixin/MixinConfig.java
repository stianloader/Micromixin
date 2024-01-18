package org.stianloader.micromixin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A Mixin configuration file.
 * <a href="https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files">Upstream documentation link.</a>
 */
public class MixinConfig {

    public static class InvalidMixinConfigException extends Exception {

        private static final long serialVersionUID = 1708886499084998432L;

        public InvalidMixinConfigException(String message) {
            super(message);
        }
    }

    public final boolean required; // IMPLEMENT "required" in mixin config
    @Nullable
    public final String minVersion; // IMPLEMENT "minVersion" in mixin config
    @NotNull
    public final String mixinPackage;
    @Nullable
    public final String target; // IMPLEMENT "target" in mixin config
    @Nullable
    public final String compatibiltyLevel; // IMPLEMENT "compatibiltyLevel" in mixin config
    @NotNull
    public final Collection<String> mixins;
    public final int priority;
    public final boolean verbose; // IMPLEMENT "verbose" in mixin config
    @Nullable
    public final String refmap; // IMPLEMENT "refmap" in mixin config
    public final boolean setSourceFile; // IMPLEMENT "setSourceFile" in mixin config
    @NotNull
    public final Collection<String> client; // IMPLEMENT "client" in mixin config properly
    @NotNull
    public final Collection<String> server; // IMPLEMENT "server" in mixin config properly

    @NotNull
    public static MixinConfig fromString(@NotNull String s) throws InvalidMixinConfigException {
        try {
            return MixinConfig.fromJson(new JSONObject(s));
        } catch (JSONException e) {
            InvalidMixinConfigException throwEx = new InvalidMixinConfigException("The mixin contents do not follow the expected format!");
            throwEx.initCause(e);
            throw throwEx;
        }
    }

    @NotNull
    public static MixinConfig fromJson(@NotNull JSONObject object) throws InvalidMixinConfigException {
        // TODO we may need to bind a mixin package to a classloader or something else in that area
        //      in order to reduce (or even outright prevent) mod conflicts.
        String mixinPackage = object.optString("package");
        if (mixinPackage == null) {
            throw new InvalidMixinConfigException("The required field \"package\" is missing from the mixin configuration.");
        }
        mixinPackage = mixinPackage.replace('.', '/');
        Set<String> mixinList = new HashSet<String>();
        JSONArray mixins = object.optJSONArray("mixins");
        JSONArray client = object.optJSONArray("client");
        JSONArray server = object.optJSONArray("server");
        if (mixins != null) {
            for (Object o : mixins) {
                mixinList.add(o.toString().replace('.', '/'));
            }
        }
        // TODO get rid of that hack
        if (client != null) {
            for (Object o : client) {
                mixinList.add(o.toString().replace('.', '/'));
            }
        }
        if (server != null) {
            for (Object o : server) {
                mixinList.add(o.toString().replace('.', '/'));
            }
        }

        return new MixinConfig(
                object.optBoolean("required", true),
                object.optString("minVersion"),
                mixinPackage,
                object.optString("target"),
                object.optString("compatibilityLevel"),
                Collections.unmodifiableCollection(mixinList),
                object.optInt("priority", 0),
                object.optBoolean("verbose", false),
                object.optString("refmap"),
                object.optBoolean("setSourceFile", false),
                Collections.<String>emptyList(),
                Collections.<String>emptyList());
    }

    protected MixinConfig(boolean required, @Nullable String minVersion, @NotNull String mixinPackage,
            @Nullable String target, @Nullable String compatibilityLevel, @NotNull Collection<String> mixins,
            int priority, boolean verbose, @Nullable String refmap, boolean setSourceFile,
            @NotNull Collection<String> client, @NotNull Collection<String> server) {
        this.required = required;
        this.minVersion = minVersion;
        this.mixinPackage = mixinPackage;
        this.target = target;
        this.compatibiltyLevel = compatibilityLevel;
        this.mixins = mixins;
        this.priority = priority;
        this.verbose = verbose;
        this.refmap = refmap;
        this.setSourceFile = setSourceFile;
        this.client = client;
        this.server = server;
    }
}
