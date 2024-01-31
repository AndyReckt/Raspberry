package me.andyreckt.raspberry.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class RaspberryConstant {
    public Pattern FLAG_PATTERN = Pattern.compile("(?:.-)?(-)([a-zA-Z])(\\w*)?");
}
