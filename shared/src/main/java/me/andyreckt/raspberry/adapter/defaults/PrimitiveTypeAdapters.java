package me.andyreckt.raspberry.adapter.defaults;

import lombok.experimental.UtilityClass;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;
import me.andyreckt.raspberry.util.RaspberryUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class PrimitiveTypeAdapters {
    private final Map<String, Boolean> BOOLEAN_MAP = new HashMap<String, Boolean>() {{
        put("true", true);
        put("on", true);
        put("yes", true);

        put("false", false);
        put("off", false);
        put("no", false);
    }};

    public RaspberryTypeAdapter<Boolean> BOOLEAN = new RaspberryTypeAdapter<Boolean>() {
        @Override
        public Boolean transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException {
            if (!BOOLEAN_MAP.containsKey(source.toLowerCase())) {
                throw new InvalidArgumentException("'" + source + "' is not a valid boolean. (try true/false)");
            }

            return BOOLEAN_MAP.get(source.toLowerCase());
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source, String... options) {
            return BOOLEAN_MAP.keySet().stream().filter(s -> RaspberryUtils.startsWithIgnoreCase(s, source)).collect(Collectors.toList());
        }
    };

    public RaspberryTypeAdapter<Double> DOUBLE = (sender, source, options) -> {
        if (source.toLowerCase().contains("e")) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }

        try {
            double parsed = Double.parseDouble(source);

            if (Double.isNaN(parsed) || !Double.isFinite(parsed)) {
                throw new InvalidArgumentException("'" + source + "' is not a valid number.");
            }

            return parsed;
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public RaspberryTypeAdapter<Float> FLOAT = (sender, source, options) -> {
        if (source.toLowerCase().contains("e")) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }

        try {
            float parsed = Float.parseFloat(source);

            if (Float.isNaN(parsed) || !Float.isFinite(parsed)) {
                throw new InvalidArgumentException("'" + source + "' is not a valid number.");
            }

            return parsed;
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public RaspberryTypeAdapter<Integer> INTEGER = (sender, source, options) -> {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public RaspberryTypeAdapter<Long> LONG = (sender, source, options) -> {
        try {
            return Long.parseLong(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public RaspberryTypeAdapter<Short> SHORT = (sender, source, options) -> {
        try {
            return Short.parseShort(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public RaspberryTypeAdapter<String> STRING = (sender, source, options) -> source;

    public RaspberryTypeAdapter<Character> CHARACTER = (sender, source, options) -> {
        if (source.length() != 1) {
            throw new InvalidArgumentException("'" + source + "' is not a valid character.");
        }

        return source.charAt(0);
    };
}
