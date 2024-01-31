package me.andyreckt.raspberry.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.andyreckt.raspberry.annotation.Flag;

@Getter
@Accessors(fluent = true)
public class FlagData implements IData {
    private final String[] values;
    private final String description;
    private final boolean baseValue;
    private final int place;

    public FlagData(Flag flag, int place) {
        this.values = flag.values();
        this.description = flag.description();
        this.baseValue = flag.baseValue();
        this.place = place;
    }
}
