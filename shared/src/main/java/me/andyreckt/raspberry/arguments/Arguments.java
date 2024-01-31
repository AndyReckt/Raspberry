package me.andyreckt.raspberry.arguments;

import lombok.Getter;
import java.util.List;

@Getter
public class Arguments {
    private final List<String> args;
    private final List<String> flags;

    public Arguments(List<String> args, List<String> flags) {
        this.args = args;
        this.flags = flags;
    }

    public String join(int from, int to, char delimiter) {
        to = Math.min(to, this.args.size() - 1);
        if (to < 1) {
            to = this.args.size() - 1;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = from; i <= to; i++) {
            builder.append(this.args.get(i));
            if (i != to) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    public String join(int from, char delimiter) {
        return this.join(from, -1, delimiter);
    }

    public String join(int from) {
        return this.join(from, ' ');
    }

    public String join(char delimiter) {
        return this.join(0, delimiter);
    }

    public String join() {
        return this.join(' ');
    }
}
