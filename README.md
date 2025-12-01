# 🍓 Raspberry

An annotation-based command framework for Minecraft Bukkit/Spigot plugins that makes command creation simple and intuitive.

## 📝 Description

Raspberry is a powerful command API that streamlines the process of creating commands for Minecraft plugins. Instead of dealing with complex command registration and argument parsing, you can simply annotate your methods and let Raspberry handle the rest. It supports features like automatic tab completion, custom type adapters, flag parsing, permissions, async execution, and beautiful auto-generated help menus.

## 🚀 Getting Started

### Prerequisites

- Java 8 or higher
- A Bukkit/Spigot/Paper Minecraft server (1.8.8 - 1.20.4)
- Gradle (for building)

### Installation

1. **Add Raspberry to your project**

For Gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.AndyReckt:Raspberry:VERSION'
}
```

For Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.AndyReckt</groupId>
        <artifactId>Raspberry</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

Replace `VERSION` with the latest release version.

2. **Initialize Raspberry in your plugin**

```java
public class ExamplePlugin extends JavaPlugin {
    
    private Raspberry raspberry;
    
    @Override
    public void onEnable() {
        // For Bukkit/Spigot
        this.raspberry = new RaspberryBukkit(this);
        
        // For Paper (recommended for async tab completion)
        this.raspberry = new RaspberryPaper(this);
        
        // Register your commands
        this.raspberry.registerCommands(new CommandTwo());
        this.raspberry.registerCommands(new CommandThree());
    }
}
```

### 📚 Basic Usage

Create a command by annotating a method:

```java
public class CommandTwo {

    @Command(names = "two")
    public void commandTwo(Player sender) {
        sender.sendMessage("Hello from command two!");
    }

    @Command(names = "1")
    public void command1(CommandSender sender, @Flag(values = "f") boolean param0) {
        sender.sendMessage("Hello from command 1!");
        sender.sendMessage("param0: " + param0);
    }

    @Command(names = "2")
    public void command2(CommandSender sender, 
                        @Flag(values = "f") boolean param0, 
                        @Flag(values = "s", baseValue = true) boolean param1, 
                        @Param(name = "w", tabComplete = "@test") World param2) {
        sender.sendMessage("Hello from command 2!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
        sender.sendMessage("param2: " + param2.getName());
    }
}
```

### 🌳 Creating Subcommands

Use the `@Children` annotation with a parent `@Command`:

```java
@Command(names = "three")
public class CommandThree {

    @Children(names = {"one", "two"})
    public void childOne(CommandSender sender) {
        sender.sendMessage("Hello from child one & two!");
    }

    @Children(names = {"x", "y"})
    public void childTwo(ConsoleCommandSender sender, 
                        @Param(name = "z") String param0, 
                        @Flag(values = "f") boolean param1) {
        sender.sendMessage("Hello from child x & z!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
    }

    @Children(names = {"a", "b"})
    public void childThree(CommandSender sender, 
                          @Param(name = "c", wildcard = true, tabComplete = {"aze aze aze", "@players"}) String param0, 
                          @Flag(values = "f") boolean param1) {
        sender.sendMessage("Hello from child a & b!");
        sender.sendMessage("param0: " + param0);
        sender.sendMessage("param1: " + param1);
    }
}
```

### ⚙️ Features

- ✨ **Annotation-based** - Simple and clean command definitions
- 🔄 **Automatic tab completion** - Built-in completions for custom types
- 🎨 **Custom type adapters** - Register adapters for any type
- 🚩 **Flag support** - Easy boolean flags with `-f` syntax
- 🔐 **Permission handling** - Built-in permission checking
- ⚡ **Async execution** - Run commands asynchronously
- 📖 **Auto-generated help** - Beautiful help menus out of the box
- 🎯 **Wildcard parameters** - Capture remaining arguments
- 🔧 **Custom completions** - Register dynamic tab completions

### 🎓 Advanced Features

**Register custom type adapters:**
```java
raspberry.registerTypeAdapter(YourType.class, new YourTypeAdapter());
```

**Register custom tab completions:**
```java
raspberry.registerAsyncCompletion("players", (context) -> 
    Bukkit.getOnlinePlayers().stream()
        .map(HumanEntity::getName)
        .collect(Collectors.toList())
);
```

**Register conditions:**
```java
raspberry.registerCondition("admin", (issuer) -> {
    if (!issuer.hasPermission("admin")) {
        throw new ConditionFailedException("You must be an admin!");
    }
});
```

---

Made with 🍓 by [AndyReckt](https://github.com/AndyReckt)
