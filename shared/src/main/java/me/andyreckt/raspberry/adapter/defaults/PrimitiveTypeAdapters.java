package me.andyreckt.raspberry.adapter.defaults;

import lombok.experimental.UtilityClass;
import me.andyreckt.raspberry.adapter.ParameterTypeAdapter;
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

    public ParameterTypeAdapter<Boolean> BOOLEAN = new ParameterTypeAdapter<Boolean>() {
        @Override
        public Boolean transform(CommandIssuer sender, String source) throws InvalidArgumentException {
            if (!BOOLEAN_MAP.containsKey(source.toLowerCase())) {
                throw new InvalidArgumentException("'" + source + "' is not a valid boolean. (try true/false)");
            }

            return BOOLEAN_MAP.get(source.toLowerCase());
        }

        @Override
        public List<String> complete(CommandIssuer sender, String source) {
            return BOOLEAN_MAP.keySet().stream().filter(s -> RaspberryUtils.startsWithIgnoreCase(s, source)).collect(Collectors.toList());
        }
    };

    public ParameterTypeAdapter<Double> DOUBLE = (sender, source) -> {
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

    public ParameterTypeAdapter<Float> FLOAT = (sender, source) -> {
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

    public ParameterTypeAdapter<Integer> INTEGER = (sender, source) -> {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public ParameterTypeAdapter<Long> LONG = (sender, source) -> {
        try {
            return Long.parseLong(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public ParameterTypeAdapter<Short> SHORT = (sender, source) -> {
        try {
            return Short.parseShort(source);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException("'" + source + "' is not a valid number.");
        }
    };

    public ParameterTypeAdapter<String> STRING = (sender, source) -> source;

    public ParameterTypeAdapter<Character> CHARACTER = (sender, source) -> {
        if (source.length() != 1) {
            throw new InvalidArgumentException("'" + source + "' is not a valid character.");
        }

        return source.charAt(0);
    };
}
