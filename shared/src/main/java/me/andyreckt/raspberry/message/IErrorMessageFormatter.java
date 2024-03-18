package me.andyreckt.raspberry.message;

public interface IErrorMessageFormatter {

    String consoleOnly();
    String playerOnly();

    String invalidArgumentPrefix();
    String usagePrefix();

    String internalError();

    String conditionFailedPrefix();


    class Default implements IErrorMessageFormatter {

        @Override
        public String consoleOnly() {
            return "&4&l┃ &cThis command can only be executed by the console.";
        }

        @Override
        public String playerOnly() {
            return "&4&l┃ &cThis command can only be executed by a player.";
        }

        @Override
        public String invalidArgumentPrefix() {
            return "&4&l┃ &c";
        }

        @Override
        public String usagePrefix() {
            return "&4&l┃ &cUsage: &7";
        }

        @Override
        public String internalError() {
            return "&4&l┃ &cAn internal error occurred while executing this command. Please contact an administrator if this issue persists.";
        }

        @Override
        public String conditionFailedPrefix() {
            return "&4&l┃ &c";
        }
    }
}
