package me.andyreckt.raspberry.command;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.raspberry.Raspberry;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.adapter.defaults.PrimitiveTypeAdapters;
import me.andyreckt.raspberry.annotation.Children;
import me.andyreckt.raspberry.annotation.Command;
import me.andyreckt.raspberry.annotation.Flag;
import me.andyreckt.raspberry.annotation.Param;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.completions.CommandCompletionAction;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.data.FlagData;
import me.andyreckt.raspberry.data.IData;
import me.andyreckt.raspberry.data.ParameterData;
import me.andyreckt.raspberry.help.HelpBuilder;
import me.andyreckt.raspberry.util.ClickablePart;
import me.andyreckt.raspberry.util.RaspberryConstant;
import me.andyreckt.raspberry.util.RaspberryUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class RaspberryCommandHandler {

    private final Raspberry raspberry;

    private final HashMap<Class<?>, RaspberryTypeAdapter<?>> typeAdapters = new HashMap<Class<?>, RaspberryTypeAdapter<?>>() {{
        put(boolean.class, PrimitiveTypeAdapters.BOOLEAN);
        put(Boolean.class, PrimitiveTypeAdapters.BOOLEAN);
        put(double.class, PrimitiveTypeAdapters.DOUBLE);
        put(Double.class, PrimitiveTypeAdapters.DOUBLE);
        put(float.class, PrimitiveTypeAdapters.FLOAT);
        put(Float.class, PrimitiveTypeAdapters.FLOAT);
        put(int.class, PrimitiveTypeAdapters.INTEGER);
        put(Integer.class, PrimitiveTypeAdapters.INTEGER);
        put(long.class, PrimitiveTypeAdapters.LONG);
        put(Long.class, PrimitiveTypeAdapters.LONG);
        put(short.class, PrimitiveTypeAdapters.SHORT);
        put(Short.class, PrimitiveTypeAdapters.SHORT);
        put(String.class, PrimitiveTypeAdapters.STRING);
        put(char.class, PrimitiveTypeAdapters.CHARACTER);
        put(Character.class, PrimitiveTypeAdapters.CHARACTER);
    }};

    @Getter
    @Setter
    private String noPermissionMessage = "You do not have permission to execute this command.";
    @Getter
    @Setter
    private String unknownCommandMessage;

    private final HashMap<String, CommandCompletionAction> completions = new HashMap<>();

    public RaspberryCommandHandler(Raspberry raspberry) {
        this.raspberry = raspberry;
        this.unknownCommandMessage = raspberry.getDefaultUnknownCommandMessage();
    }

    public RaspberryTypeAdapter<?> getTypeAdapter(Class<?> clazz) {
        return typeAdapters.get(clazz);
    }

    public Arguments processArguments(String[] toProcess) {
        List<String> args = new ArrayList<>();
        List<String> flags = new ArrayList<>();

        if (toProcess == null)
            throw new IllegalArgumentException("Arguments cannot be null.");

        for (String argument : toProcess) {
            if (argument == null || argument.isEmpty()) continue;

            boolean flag = RaspberryConstant.FLAG_PATTERN.matcher(argument).matches();

            if (flag) flags.add(argument.replaceFirst("-", ""));
            else args.add(argument);
        }

        return new Arguments(args, flags);
    }

    public List<RaspberryCommand> processCommand(Method method, Object instance) {
        if (!method.isAnnotationPresent(Command.class) && !method.isAnnotationPresent(Children.class)) {
            return null;
        }

        if (method.getParameterCount() < 1)
            throw new IllegalArgumentException(
                    "Method " + method.getName() + " in class "
                            + method.getDeclaringClass().getName() + " does not have an Issuer parameter."
            );

        if (!raspberry.isCommandIssuer(method.getParameterTypes()[0]))
            throw new IllegalArgumentException(
                    "Sender " + (method.getParameterTypes()[0].getName()) +
                            " of method " + method.getName() + " in class "
                            + method.getDeclaringClass().getName() + " cannot be assigned to CommandIssuer."
            );

        List<CommandData> commandDatas = new ArrayList<>();
        if (method.isAnnotationPresent(Command.class)) {
            commandDatas.add(new CommandData(method.getAnnotation(Command.class)));
        }
        if (method.getDeclaringClass().isAnnotationPresent(Command.class) && method.isAnnotationPresent(Children.class)) {
            Command parent = method.getDeclaringClass().getAnnotation(Command.class);
            commandDatas.add(new CommandData(method.getAnnotation(Children.class), new CommandData(parent)));
        }

        if (commandDatas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Method " + method.getName() + " in class "
                            + method.getDeclaringClass().getName() + " has a Children annotation" +
                            " but the class does not have a Command annotation."
            );
        }

        Class<?> owningClass = instance == null ? method.getDeclaringClass() : null;

        List<IData> parameters = new ArrayList<>();

        if (method.getParameterCount() > 1) {
            for (int place = 1; place < method.getParameterCount(); place++) {
                Parameter parameter = method.getParameters()[place];

                if (!parameter.isAnnotationPresent(Param.class) && !parameter.isAnnotationPresent(Flag.class))
                    throw new IllegalArgumentException(
                            "Parameter " + parameter.getName() + " of method " + method.getName() + " in class "
                            + method.getDeclaringClass().getName() + " does not have a Param or Flag annotation."
                    );

                if (parameter.isAnnotationPresent(Param.class)) {
                    Class<?> type = parameter.getType();

                    if (!typeAdapters.containsKey(type))
                        throw new IllegalArgumentException(
                                "Class " + type.getName() + " does not have a registered ParameterTypeAdapter."
                        );

                    Param param = parameter.getAnnotation(Param.class);
                    ParameterData parameterData = new ParameterData(param, type, place);
                    parameters.add(parameterData);
                }
                else {
                    if (parameter.getType() != boolean.class && parameter.getType() != Boolean.class)
                        throw new IllegalArgumentException(
                                "Flag " + parameter.getName() + " of method " + method.getName() + " in class "
                                + method.getDeclaringClass().getName() + " is not a boolean."
                        );

                    Flag flag = parameter.getAnnotation(Flag.class);
                    FlagData flagData = new FlagData(flag, place);
                    parameters.add(flagData);
                }
            }
        }

        return this.createCommand(instance, owningClass, commandDatas, method, parameters);
    }

    public List<RaspberryCommand> createCommand(
            Object instance, Class<?> owningClass,
            List<CommandData> commandDatas, Method method,
            List<IData> parameters) {
        List<RaspberryCommand> commands = new ArrayList<>();

        for (CommandData commandData : commandDatas) {
            if (commandData.parent() != null) {
                RaspberryCommand parent = raspberry.getRootCommand().findChild(commandData.parent().name());

                if (parent == null) {
                    parent = raspberry.createCommand(commandData.parent());
                    parent.setOwningClass(owningClass);
                    parent.setOwningInstance(instance);
                    raspberry.getRootCommand().registerChildren(parent);
                }

                RaspberryCommand child = raspberry.createCommand(commandData);
                child.setMethod(method);
                child.setOwningInstance(instance);
                child.setOwningClass(owningClass);
                child.setParameters(parameters);

                parent.registerChildren(child);
                commands.add(parent);
                continue;
            }

            RaspberryCommand command = raspberry.createCommand(commandData);
            command.setMethod(method);
            command.setOwningInstance(instance);
            command.setOwningClass(owningClass);
            command.setParameters(parameters);

            raspberry.getRootCommand().registerChildren(command);
            commands.add(command);
        }

        return commands;
    };

    public void sendHelp(RaspberryCommand command, CommandIssuer<?> issuer, int page) {
        HelpBuilder builder = new HelpBuilder(issuer, command, page);

        builder.build().forEach(issuer::sendClickable);
    }

    public void sendHelp(List<List<ClickablePart>> parts, CommandIssuer<?> issuer) {
        parts.forEach(issuer::sendClickable);
    }

    public List<String> getCompletions(RaspberryCommand command, CommandIssuer issuer, String[] args) {
        Set<String> completions = new HashSet<>();

        Arguments arguments = this.processArguments(args);

        RaspberryCommand node = command.findCommand(arguments);
        if (!node.canUse(issuer)) return new ArrayList<>(completions);

        List<String> realArgs = arguments.getArgs();

        int index = realArgs.size() - 1;
        if (index < 0) index = 0;
        if (args[args.length -1].equalsIgnoreCase(" ")) index++;

        if (node.hasChild()) {
            String name = realArgs.isEmpty() ? "" : realArgs.get(realArgs.size() - 1);
            completions.addAll(node.getChildren().values().stream()
                    .filter(it -> node.getName() != null && node.canUse(issuer) &&
                            (RaspberryUtils.startsWithIgnoreCase(node.getName(), name) || name == null || name.isEmpty()))
                    .map(RaspberryCommand::getName)
                    .collect(Collectors.toList()));


            if (!completions.isEmpty()) {
                return new ArrayList<>(completions);
            }
        }

        if (args[args.length - 1].equalsIgnoreCase(node.getName()) && !args[args.length -1].equalsIgnoreCase(" ")) {
            return new ArrayList<>(completions);
        }

        List<FlagData> possibleFlags = node.getParameters().stream()
                .filter(data -> data instanceof FlagData)
                .map(data -> (FlagData) data)
                .collect(Collectors.toList());

        if (!possibleFlags.isEmpty()) {
            for (FlagData flag : possibleFlags) {
                String arg = args[args.length - 1];
                if (RaspberryConstant.FLAG_PATTERN.matcher(arg).matches()
                        || arg.startsWith("-") && (RaspberryUtils.startsWithIgnoreCase(flag.values()[0], arg.substring(1)))
                        || arg.equals("-")
                        || arg.isEmpty() || arg.equals(" ")
                ) {
                    completions.add("-" + flag.values()[0]);
                }
            }
        }

        try {
            List<ParameterData> params = node.getParameters().stream()
                    .filter(param -> param instanceof ParameterData)
                    .map(param -> (ParameterData) param)
                    .collect(Collectors.toList());

            int fixed = Math.max(0, index - 1);

            if (params.isEmpty()) {
                return new ArrayList<>(completions);
            }

            ParameterData data = params.get(fixed);

            RaspberryTypeAdapter<?> parameterType = this.getTypeAdapter(data.clazz());

            if (parameterType != null) {
                if (index < realArgs.size() && args[index].equalsIgnoreCase(node.getName())) {
                    realArgs.add("");
                    ++index;
                }
                String argumentBeingCompleted = (index >= realArgs.size() || realArgs.isEmpty()) ? "" : realArgs.get(index).trim();

                String[] tabCompleteFlags = data.tabComplete();

                for (String flag : tabCompleteFlags) {
                    if (flag.startsWith("@")) {
                        CommandCompletionAction<?> action = this.completions.get(flag);

                        if (action != null) {
                            Collection<String> suggestions = action.get(raspberry.getCommandCompletionContext(node, issuer, argumentBeingCompleted));
                            completions.addAll(suggestions.stream()
                                    .filter(s -> RaspberryUtils.startsWithIgnoreCase(s, argumentBeingCompleted))
                                    .collect(Collectors.toList()));
                        }
                        continue;
                    }
                    if (RaspberryUtils.startsWithIgnoreCase(flag, argumentBeingCompleted)) {
                        completions.add(flag);
                    }
                }

                List<String> suggested = parameterType.complete(issuer, argumentBeingCompleted);
                completions.addAll(suggested.stream()
                        .filter(s -> RaspberryUtils.startsWithIgnoreCase(s, argumentBeingCompleted))
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(completions);
    }

    public void registerTypeAdapter(Class<?> clazz, RaspberryTypeAdapter<?> adapter) {
        typeAdapters.put(clazz, adapter);
    }

    private String prepareCompletionId(String id) {
        return (id.startsWith("@") ? "" : "@") + id.toLowerCase();
    }

    public void registerAsyncCompletion(String id, CommandCompletionAction action) {
        this.completions.put(prepareCompletionId(id), action);
    }

    public void registerCompletion(String id, List<String> completions) {
        this.completions.put(prepareCompletionId(id), x -> completions);
    }
}
