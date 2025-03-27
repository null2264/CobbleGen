package io.github.null2264.cobblegen.data;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;

public class CGRange
{
    private final CGRange.Value min;
    private final CGRange.Value max;

    public CGRange(CGRange.Value min, CGRange.Value max) {
        this.min = min;
        this.max = max;
    }

    public CGRange.Value getMin() {
        return min;
    }

    public CGRange.Value getMax() {
        return max;
    }

    public static class Value {
        private final boolean inclusive;
        private final int value;

        public Value(int value, boolean isInclusive) {
            this.value = value;
            this.inclusive = isInclusive;
        }

        public int value() {
            return value;
        }

        public boolean inclusive() {
            return inclusive;
        }

        public boolean lt(int value) {
            if (inclusive) {
                return value <= this.value;
            } else {
                return value < this.value;
            }
        }

        public boolean gt(int value) {
            if (inclusive) {
                return value >= this.value;
            } else {
                return value > this.value;
            }
        }
    }

    public static CGRange fromString(String str) {
        return new CGRange(
            new Value(Integer.parseInt(str.substring(1, str.indexOf(','))), str.charAt(0) == '['),
            new Value(Integer.parseInt(str.substring(str.indexOf(',') + 1, str.length() - 1)), str.charAt(str.length() - 1) == ']')
        );
    }

    @Serializer
    public JsonElement toJson() {
        return JsonPrimitive.of(
            (min.inclusive() ? "[" : "(") +
            min.value() +
            "," +
            max.value() +
            (min.inclusive() ? "]" : ")")
        );
    }

    @Deserializer
    public static CGRange fromJson(JsonPrimitive json) {
        return fromString(json.asString());
    }
}