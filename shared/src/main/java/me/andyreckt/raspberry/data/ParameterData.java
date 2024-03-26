package me.andyreckt.raspberry.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.andyreckt.raspberry.annotation.Param;

@Getter
@Accessors(fluent = true)
public class ParameterData implements IData {
    private final String name;
    private final String description;
    private final boolean wildcard;
    private final String baseValue;
    private final String[] tabComplete;
    private final Class<?> clazz;
    private final int place;
    private final String[] options;

    public ParameterData(Param param, Class<?> clazz, int place) {
        this.name = param.name();
        this.description = param.description();
        this.wildcard = param.wildcard();
        this.baseValue = param.baseValue();
        this.tabComplete = param.tabComplete();
        this.options = param.options();
        this.clazz = clazz;
        this.place = place;
    }
}
