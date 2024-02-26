package me.andyreckt.raspberry.command;


import lombok.Getter;
import lombok.Setter;
import me.andyreckt.raspberry.Raspberry;
import me.andyreckt.raspberry.adapter.RaspberryTypeAdapter;
import me.andyreckt.raspberry.arguments.Arguments;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.data.FlagData;
import me.andyreckt.raspberry.data.IData;
import me.andyreckt.raspberry.data.ParameterData;
import me.andyreckt.raspberry.exception.*;
import me.andyreckt.raspberry.help.HelpBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class RaspberryCommand {

    private String name;
    private String description;
    private String permission;

    private List<String> aliases = new ArrayList<>();
    private String usage = "";
    private String helpCommand = "";
    private boolean autoHelp = true;
    private boolean async = false;
    private boolean hidden = false;
    private List<IData> parameters = new ArrayList<>();

    private Class<?> owningClass;
    private Object owningInstance;
    private Method method;

    private Map<String, RaspberryCommand> children = new HashMap<>();
    private RaspberryCommand parent;

    protected RaspberryCommand(String name, String description, String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    protected RaspberryCommand(CommandData command) {
        this(command.aliases().get(0), command.description(), command.permission());

        aliases = command.aliases();
        usage = command.usage();
        helpCommand = command.helpCommand();
        autoHelp = command.autoHelp();
        async = command.async();
        hidden = command.hidden();
    }

    protected RaspberryCommand() {
        this(null, null, "");
    }

    protected RaspberryCommand(Class<?> owningClass, Object instance) {
        this();
        this.owningClass = owningClass;
        this.owningInstance = instance;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public boolean hasChild(String name) {
        return children.containsKey(name.toLowerCase());
    }

    public RaspberryCommand getChild(String name) {
        return children.get(name.toLowerCase());
    }

    public void registerChildren(RaspberryCommand command) {
        if ((command.getName() == null || command.getName().isEmpty()) && command.getAliases().isEmpty())
            throw new IllegalArgumentException("Command name and aliases cannot be null or empty.");

        if (command.getName() != null && !command.getName().isEmpty()) {
            children.put(command.getName(), command);
        }

        command.getAliases().forEach(alias -> children.put(alias, command));

        command.setParent(this);
    }

    public RaspberryCommand findChild(String name) {
        if (name.isEmpty()) return this;

        return children.get(name.toLowerCase());

//        if (hasChild(subCommand)) {
//            return getChild(subCommand).findChild(Arrays.copyOfRange(name, 1, name.length));
//        }
//
//        return null;
    }

    public RaspberryCommand findCommand(Arguments arguments) {
        if (arguments.getArgs().isEmpty()) return this;

        String subCommand = arguments.getArgs().get(0);

        if ("help".equalsIgnoreCase(subCommand) && autoHelp) {
            return null;
        }

        if (hasChild(subCommand)) {
            arguments.getArgs().remove(0);
            return getChild(subCommand).findCommand(arguments);
        }

        return this;
    }

    public List<String> getRealAliases() {
        List<String> realAliases = new ArrayList<>(aliases);
        realAliases.remove(0);
        return realAliases;
    }

    public String getUsageText() {
        if (this.usage != null && !this.usage.isEmpty()) {
            return this.usage;
        }

        String usageText = getFullLabel() + " " + getParametersUsage();

        return usageText.trim();
    }

    public String getParametersUsage() {
        if (this.usage != null && !this.usage.isEmpty()) {
            return this.usage.replace(getFullLabel(), "").trim();
        }

        StringBuilder builder = new StringBuilder();
        List<FlagData> flags = parameters.stream()
                .filter(data -> data instanceof FlagData)
                .map(data -> (FlagData) data)
                .collect(Collectors.toList());

        if (!flags.isEmpty()) {
            boolean firstFlag = true;

            builder.append("(");
            for (FlagData flag : flags) {
                String name = flag.values()[0];

                if (!firstFlag) {
                    builder.append(" | ");
                }

                firstFlag = false;
                builder.append("-").append(name);
            }

            builder.append(") ");
        }

        List<ParameterData> parameters = this.parameters.stream()
                .filter(data -> data instanceof ParameterData)
                .map(data -> (ParameterData) data)
                .collect(Collectors.toList());

        if (!parameters.isEmpty()) {
            for (ParameterData parameter : parameters) {
                if (parameter.baseValue().isEmpty()) {
                    if (parameter.wildcard()) {
                        builder.append("<").append(parameter.name()).append("...> ");
                    } else {
                        builder.append("<").append(parameter.name()).append("> ");
                    }
                } else {
                    if (parameter.wildcard()) {
                        builder.append("[").append(parameter.name()).append("...] ");
                    } else {
                        builder.append("[").append(parameter.name()).append("] ");
                    }
                }
            }
        }
        return builder.toString().trim();
    }

    public boolean canUse(CommandIssuer<?> issuer) {
        if (parent != null && !parent.canUse(issuer)) return false;
        if (permission == null || permission.isEmpty()) return true;

        return issuer.hasPermission(permission);
    }

    public void sendHelp(CommandIssuer<?> issuer) {
        HelpBuilder builder = new HelpBuilder(issuer, this);
        builder.build().forEach(issuer::sendClickable);
    }

    public String getFullLabel() {
        List<String> labels = new ArrayList<>();
        RaspberryCommand command = this;

        while (command != null && command != Raspberry.getInstance().getRootCommand()) {
            labels.add(command.getName());
            command = command.getParent();
        }

        Collections.reverse(labels);
        return String.join(" ", labels).trim();
    }

    protected abstract Class<?> consoleClass();
    protected abstract Class<?> playerClass();

    private boolean isConsoleOnly() {
        return method != null && method.getParameterTypes()[0].equals(consoleClass());
    }

    private boolean isPlayerOnly() {
        return method != null && method.getParameterTypes()[0].equals(playerClass());
    }

    public void invoke(CommandIssuer<?> issuer, Arguments arguments) {
        if (!canUse(issuer)) {
            throw new NoPermissionException();
        }

        if (method == null) {
            if (hasChild()) {
                if (hidden) throw new UnknownCommandException(false);
                else throw new UnknownCommandException(true);
            }
            else throw new UnknownCommandException(false);
        }

        if (isConsoleOnly() && issuer.isPlayer()) {
            throw new InvalidExecutorException(true);
        }

        if (isPlayerOnly() && !issuer.isPlayer()) {
            throw new InvalidExecutorException(false);
        }

        List<Object> parameters = new ArrayList<>(method.getParameterCount());
        parameters.add(issuer.getIssuer());

        int index = 0;
        for (IData data : this.parameters) {
            if (data instanceof FlagData) {
                FlagData flagData = (FlagData) data;
                boolean value = flagData.baseValue();

                for (String name : flagData.values()) {
                    if (arguments.getFlags().contains(name)) {
                        value = !value;
                        break;
                    }
                }

                parameters.add(flagData.place(), value);
            }
            else {
                if (!(data instanceof ParameterData)) continue;
                ParameterData parameterData = (ParameterData) data;
                String argument;

                if (index < arguments.getArgs().size()) {
                    argument = arguments.getArgs().get(index);
                } else {
                    argument = parameterData.baseValue();
                    if (argument == null || argument.isEmpty()) {
                        throw new InvalidArgumentException("Missing argument for parameter " + parameterData.name() + ".");
                    }
                }

                if (parameterData.wildcard() && !(argument.isEmpty() || argument.equalsIgnoreCase(parameterData.baseValue()))) {
                    argument = arguments.join(index);
                }

                RaspberryTypeAdapter<?> adapter = Raspberry.getInstance().getCommandHandler().getTypeAdapter(parameterData.clazz());

                if (adapter == null) {
                    throw new IllegalArgumentException("No adapter found for class " + parameterData.clazz().getName() + ".");
                }

                Object value;
                try {
                    value = adapter.transform(issuer, argument);
                } catch (InvalidArgumentException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new InvalidArgumentException("Invalid argument for parameter " + parameterData.name() + ".");
                }

                if (value == null) {
                    throw new InvalidArgumentException("Invalid argument for parameter " + parameterData.name() + ".");
                }

                parameters.add(parameterData.place(), value);
                index++;
            }
        }

        try {
            long start = System.currentTimeMillis();

            method.invoke(owningInstance, parameters.toArray());

            long end = System.currentTimeMillis();

            if (Raspberry.getInstance().isDebugMode() && end - start >= 300L) {
                Raspberry.getInstance().getLogger().warning("Command '/" + getFullLabel() + "' took " + (end - start) + "ms! (async: " + async + ")");
            }
        } catch (Exception thrown) {
            Throwable ex = thrown instanceof InvocationTargetException ? thrown.getCause() : thrown;
            if (ex instanceof InvalidArgumentException) throw (InvalidArgumentException) ex;
            else if (ex instanceof IllegalAccessException) throw new CommandProcessException("There was a problem while accessing the command.", false);
            else throw new MethodFailedException("An error occurred while processing method.", ex);
        }
    }



}
