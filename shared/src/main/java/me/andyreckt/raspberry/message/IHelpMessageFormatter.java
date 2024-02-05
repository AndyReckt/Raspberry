package me.andyreckt.raspberry.message;

import me.andyreckt.raspberry.help.HelpBuilder;
import me.andyreckt.raspberry.help.HelpLine;
import me.andyreckt.raspberry.util.ClickablePart;

import java.util.ArrayList;
import java.util.List;

public interface IHelpMessageFormatter {

    String getHeaderFormat(HelpBuilder builder);

    String getCommandFormat(HelpLine line);
    String getCommandFormatWithDescription(HelpLine line);
    List<ClickablePart> getFooterFormat(HelpBuilder builder);

    String getNextPageHover();
    String getNextPageFormat();

    String getPreviousPageFormat();
    String getPreviousPageHover();


    class Default implements IHelpMessageFormatter {

        @Override
        public String getHeaderFormat(HelpBuilder builder) {
            if (builder.getCommand().getDescription() != null && !builder.getCommand().getDescription().isEmpty())
                return "&6&l┃ &e&lHelp &7» &f/" + builder.getCommand().getName() + " &7(" + builder.getCommand().getDescription() + ")";
            return "&6&l┃ &e&lHelp &7» &f/" + builder.getCommand().getName();
        }

        @Override
        public String getCommandFormat(HelpLine line) {
            return " &7▪ &e/" + line.getParent() + " " + line.getCommand() + " &f" + line.getUsage();
        }

        @Override
        public String getCommandFormatWithDescription(HelpLine line) {
            return " &7▪ &e/" + line.getParent() + " " + line.getCommand() + " &f" + line.getUsage() + " &7(" + line.getDescription() + ")";
        }

        @Override
        public List<ClickablePart> getFooterFormat(HelpBuilder builder) {
            List<ClickablePart> parts = new ArrayList<>();
            parts.add(new ClickablePart("&7&l┃ &fShowing page "));

            if (builder.getCurrentPage() > 1) {
                parts.add(new ClickablePart(this.getPreviousPageFormat(), this.getPreviousPageHover(), null, builder.getPreviousPage()));
            }

            parts.add(new ClickablePart("&e" + builder.getCurrentPage() + " &fof &e" + builder.getTotalPages() + " "));

            if (builder.getCurrentPage() < builder.getTotalPages()) {
                parts.add(new ClickablePart(this.getNextPageFormat(), this.getNextPageHover(), null, builder.getNextPage()));
            }

            parts.add(new ClickablePart("&7(" + builder.getResultsAmount() + " &7results)"));

            return parts;
        }

        @Override
        public String getNextPageHover() {
            return "&fClick to go to the next page.";
        }

        @Override
        public String getNextPageFormat() {
            return "&6» ";
        }

        @Override
        public String getPreviousPageFormat() {
            return "&6« ";
        }

        @Override
        public String getPreviousPageHover() {
            return "&fClick to go to the previous page.";
        }
    }
}
