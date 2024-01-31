package me.andyreckt.raspberry.adapter;

import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.exception.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public interface ParameterTypeAdapter<T> {
    List<String> EMPTY_LIST = new ArrayList<>();

    T transform(CommandIssuer sender, String source) throws InvalidArgumentException;

    default List<String> complete(CommandIssuer sender, String source) {
        return EMPTY_LIST;
    };
}
