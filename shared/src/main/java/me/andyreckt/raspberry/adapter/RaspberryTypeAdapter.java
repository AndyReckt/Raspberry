package me.andyreckt.raspberry.adapter;

import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public interface RaspberryTypeAdapter<T> {
    List<String> EMPTY_LIST = new ArrayList<>();

    T transform(CommandIssuer sender, String source, String... options) throws InvalidArgumentException;

    default List<String> complete(CommandIssuer sender, String source, String... options) {
        return EMPTY_LIST;
    }
}
