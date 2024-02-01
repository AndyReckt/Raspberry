package me.andyreckt.raspberry;

import lombok.Getter;
import lombok.SneakyThrows;
import me.andyreckt.raspberry.adapter.ParameterTypeAdapter;
import me.andyreckt.raspberry.annotation.Children;
import me.andyreckt.raspberry.annotation.Command;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.command.RaspberryCommandHandler;
import me.andyreckt.raspberry.completions.CommandCompletionAction;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.data.IData;
import me.andyreckt.raspberry.util.RaspberryUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


@Getter
public abstract class Raspberry {

    @Getter
    private static Raspberry instance;

    protected final RaspberryCommand rootCommand;
    private final RaspberryCommandHandler commandHandler;
    private final Executor executor = Executors.newFixedThreadPool(2);
    protected Logger logger = Logger.getLogger("Raspberry");

    private boolean debugMode = false;

    protected Raspberry(RaspberryCommand rootCommand) {
        instance = this;
        this.rootCommand = rootCommand;
        this.commandHandler = new RaspberryCommandHandler(this);
    }

    /**
     * Enable debug mode.
     * This will print out extra information when an error occurs.
     *
     * @return the current instance of Raspberry
     */
    @Deprecated
    public Raspberry debug() {
        this.debugMode = true;
        return this;
    }


    /**
     * Scans all the static methods in a class and register all commands.
     *
     * @param clazz The class to scan
     */
    @Deprecated
    public void registerCommands(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (!method.isAnnotationPresent(Command.class) && !method.isAnnotationPresent(Children.class)) continue;
            RaspberryCommand command = commandHandler.processCommand(method, null);
            injectCommand(command);
        }
    }


    /**
     * Scans all the methods in a class and register all commands.
     *
     * @param instance The instance of the class to scan
     */
    public void registerCommands(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Command.class) && !method.isAnnotationPresent(Children.class)) continue;
            RaspberryCommand command = commandHandler.processCommand(method, instance);
            injectCommand(command);
        }
    }

    /**
     * Scans all the methods in a package and register all commands.
     *
     * @param instance Your plugin instance
     * @param packageName The package to scan
     * @param create Whether to create a new instance of each class found in the package
     */
    @SneakyThrows
    public void registerCommands(Object instance, String packageName, boolean create) {
        for (Class<?> clazz : RaspberryUtils.getClassesInPackage(instance.getClass(), packageName)) {
            if (create) registerCommands(clazz.newInstance());
            else registerCommands(clazz);
        }
    }

    /**
     * Scans all the static methods in a package and register all commands.
     *
     * @param instance Your plugin instance
     * @param packageName The package to scan
     */
    public void registerCommands(Object instance, String packageName) {
        registerCommands(instance, packageName, false);
    }

    /**
     * Scans all the static methods in a plugin and register all commands.
     *
     * @param instance Your plugin instance
     */
    public void registerCommandOfPlugin(Object instance) {
        for (Class<?> clazz : RaspberryUtils.getClassesInPackage(instance.getClass(), instance.getClass().getPackage().getName())) {
            registerCommands(clazz);
        }
    }

    public abstract void injectCommand(RaspberryCommand commands);

    public abstract boolean isCommandIssuer(Class<?> issuer);
    public abstract CommandIssuer<?> getCommandIssuer(Object issuer);
    public abstract CommandCompletionContext getCommandCompletionContext(RaspberryCommand command, CommandIssuer<?> issuer, String input);

    public abstract RaspberryCommand createCommand(
            Object instance, Class<?> owningClass,
            CommandData commandData, Method method,
            List<IData> parameters
    );

    /**
     * Gets the default message sent to a user when they do not have permission to execute a command.
     *
     * @return the default message as configured in your environment.
     */
    public abstract String getDefaultUnknownCommandMessage();

    /**
     * Customize the message sent to a user when they execute an unknown command.
     *
     * @param message the message to send to the user.
     */
    public void setUnknownCommandMessage(String message) {
        commandHandler.setUnknownCommandMessage(message);
    }

    /**
     * Customize the message sent to a user when they do not have permission to execute a command.
     *
     * @param message the message to send to the user.
     */
    public void setNoPermissionMessage(String message) {
        commandHandler.setNoPermissionMessage(message);
    }

    /**
     * Register a Type adapter.
     *
     * @param clazz the class to return when transformed. (IE: <code>int.class</code>)
     * @param adapter the ParameterTypeAdapter object to register from. (IE: <code>new IntTypeAdapter()</code> or <code>new ParameterTypeAdapter< Object >()</code>)
     */
    public void registerTypeAdapter(Class<?> clazz, ParameterTypeAdapter<?> adapter) {
        commandHandler.registerTypeAdapter(clazz, adapter);
    }

    /**
     * Register a tab completion.
     *
     * @param id the id of the completion.
     * @param suggestions the list of suggestions.
     */
    @Deprecated
    public void registerCompletion(String id, List<String> suggestions) {
        commandHandler.registerCompletion(id, suggestions);
    }

    /**
     * Register a tab completion.
     *
     * @param id the id of the completion.
     * @param suggestions the list of suggestions.
     */
    public void registerAsyncCompletion(String id, CommandCompletionAction<?> suggestions) {
        commandHandler.registerAsyncCompletion(id, suggestions);
    }
}
