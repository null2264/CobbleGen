package io.github.null2264.cobblegen.data;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;

import java.util.Objects;

public class CGSemVer
{
    private final int major;
    private final int minor;
    private final int patch;

    public CGSemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public CGSemVer(int major, int minor) {
        this(major, minor, 0);
    }

    public static CGSemVer fromString(String str) {
        String[] str2 = str.split("\\.");
        int major = str2.length >= 1 ? Integer.parseInt(str2[0]) : 0;
        int minor = str2.length >= 2 ? Integer.parseInt(str2[1]) : 0;
        int patch = str2.length >= 3 ? Integer.parseInt(str2[2]) : 0;
        return new CGSemVer(major, minor, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int patch() {
        return patch;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CGSemVer)) return false;
        CGSemVer other = (CGSemVer) obj;
        return this.major() == other.major() && this.minor() == other.minor() && this.patch() == other.patch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    public boolean lt(CGSemVer other) {
        if (this.major() != other.major()) return this.major() < other.major();
        if (this.minor() != other.minor()) return this.minor() < other.minor();
        return this.patch() < other.patch();
    }

    public boolean lte(CGSemVer other) {
        if (this.major() != other.major()) return this.major() < other.major();
        if (this.minor() != other.minor()) return this.minor() < other.minor();
        return this.patch() <= other.patch();
    }

    public boolean gt(CGSemVer other) {
        if (this.major() != other.major()) return this.major() > other.major();
        if (this.minor() != other.minor()) return this.minor() > other.minor();
        return this.patch() > other.patch();
    }

    public boolean gte(CGSemVer other) {
        if (this.major() != other.major()) return this.major() > other.major();
        if (this.minor() != other.minor()) return this.minor() > other.minor();
        return this.patch() >= other.patch();
    }

    @Serializer
    public JsonElement toJson() {
        return JsonPrimitive.of(major + "." + minor + "." + patch);
    }

    @Deserializer
    public static CGSemVer fromJson(JsonPrimitive json) {
        return CGSemVer.fromString(json.asString());
    }
}