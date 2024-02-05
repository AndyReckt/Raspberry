package me.andyreckt.raspberry.message;

public interface IMessageFormatter {

    IHelpMessageFormatter getHelpMessageFormatter();
    IErrorMessageFormatter getErrorMessageFormatter();

    class Default implements IMessageFormatter {
        private final IHelpMessageFormatter helpFormatter = new IHelpMessageFormatter.Default();
        private final IErrorMessageFormatter errorFormatter = new IErrorMessageFormatter.Default();

        @Override
        public IHelpMessageFormatter getHelpMessageFormatter() {
            return helpFormatter;
        }

        @Override
        public IErrorMessageFormatter getErrorMessageFormatter() {
            return errorFormatter;
        }
    }
}
