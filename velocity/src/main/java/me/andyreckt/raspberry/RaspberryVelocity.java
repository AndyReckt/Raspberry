package me.andyreckt.raspberry;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;
import me.andyreckt.raspberry.adapter.defaults.VelocityTypeAdapters;
import me.andyreckt.raspberry.command.CommandIssuer;
import me.andyreckt.raspberry.command.RaspberryCommand;
import me.andyreckt.raspberry.command.RaspberryVelocityCommand;
import me.andyreckt.raspberry.command.VelocityCommandIssuer;
import me.andyreckt.raspberry.completions.CommandCompletionContext;
import me.andyreckt.raspberry.data.CommandData;
import me.andyreckt.raspberry.data.IData;
import me.andyreckt.raspberry.util.Clickable;
import me.andyreckt.raspberry.util.ClickablePart;
import me.andyreckt.raspberry.velocity.VelocityRaspberryCommand;
import me.andyreckt.raspberry.velocity.completion.VelocityCommandCompletionContext;

import java.lang.reflect.Method;
import java.util.List;

@Getter
public class RaspberryVelocity extends Raspberry {
    @Getter
    private static RaspberryVelocity velocityInstance;

    private final ProxyServer proxy;

    public RaspberryVelocity(ProxyServer proxy) {
        super(new RaspberryVelocityCommand());
        velocityInstance = this;

        this.proxy = proxy;

        this.registerTypeAdapter(Player.class, VelocityTypeAdapters.PLAYER);
        this.registerTypeAdapter(ServerInfo.class, VelocityTypeAdapters.SERVER_INFO);
        this.registerTypeAdapter(RegisteredServer.class, VelocityTypeAdapters.REGISTERED_SERVER);
    }

    @Override
    public void injectCommand(RaspberryCommand command) {
        if (!(command instanceof RaspberryVelocityCommand))
            throw new IllegalArgumentException("Command must be an instance of RaspberryVelocityCommand.");

        RaspberryVelocityCommand node = (RaspberryVelocityCommand) command;
        CommandMeta meta = proxy.getCommandManager().metaBuilder(node.getName())
                .aliases(node.getAliases().toArray(new String[]{}))
                .build();

        VelocityRaspberryCommand velocityCommand = new VelocityRaspberryCommand(node);
        proxy.getCommandManager().register(meta, velocityCommand);
    }

    @Override
    public boolean isCommandIssuer(Class<?> issuer) {
        return CommandSource.class.isAssignableFrom(issuer);
    }

    @Override
    public CommandIssuer<?> getCommandIssuer(Object issuer) {
        if (!(issuer instanceof CommandSource))
            throw new IllegalArgumentException("Issuer must be an instance of CommandSender.");

        return new VelocityCommandIssuer((CommandSource) issuer);
    }

    @Override
    public CommandCompletionContext getCommandCompletionContext(RaspberryCommand command, CommandIssuer<?> issuer, String input) {
        return new VelocityCommandCompletionContext((RaspberryVelocityCommand) command, input, (VelocityCommandIssuer) issuer);
    }

    @Override
    public void sendClickable(CommandIssuer<?> issuer, List<ClickablePart> parts) {
        VelocityCommandIssuer velocityIssuer = (VelocityCommandIssuer) issuer;
        velocityIssuer.sendClickable(parts);
    }

    @Override @SuppressWarnings("unchecked")
    public RaspberryVelocityCommand createCommand(CommandData data) {
        return new RaspberryVelocityCommand(data);
    }

    @Override
    public String getDefaultUnknownCommandMessage() {
        return "This command does not exist.";
    }
}