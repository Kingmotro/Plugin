package net.minepos.plugin.core.handlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.minepos.plugin.core.framework.Command;
import net.minepos.plugin.core.objects.enums.CommandsEnum;
import net.minepos.plugin.core.storage.yaml.Commands;
import net.minepos.plugin.core.storage.yaml.Lang;
import net.minepos.plugin.core.utils.string.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2018
// https://www.piggypiglet.me
// ------------------------------
@Singleton
public final class CommandHandler implements CommandExecutor {
    @Inject private Commands commands;
    @Inject private Lang lang;

    @Getter private Map<CommandsEnum, Command> commandsMap;

    public CommandHandler() {
        commandsMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] badArgs) {
        if (badArgs.length >= 1) {
            String msg = String.join(" ", badArgs);
            CommandsEnum cmdEnum;

            try {
                cmdEnum = CommandsEnum.valueOf(badArgs[0].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(lang.get("ingame.unknown-command", msg));
                return true;
            }

            String name = commands.getCommand(cmdEnum);

            if (commandsMap.containsKey(cmdEnum)) {
                if (StringUtils.startsWith(msg, name)) {
                    Command cmd = commandsMap.get(cmdEnum);
                    String permission = commands.getPermission(cmdEnum);
                    String usage = commands.getUsage(cmdEnum);
                    String[] args = msg.toLowerCase().replaceFirst(name.toLowerCase(), "").trim().split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (sender.hasPermission(permission)) {
                        boolean run = cmd.run(sender, args[0].isEmpty() ? new String[]{} : args);

                        if (!run) sender.sendMessage(lang.get("ingame.invalid-command-usage", usage));
                    } else {
                        sender.sendMessage(lang.get("ingame.no-permission", msg));
                    }

                    return true;
                }
            }

            sender.sendMessage(lang.get("ingame.unknown-command", msg));
            return true;
        }

        sender.sendMessage(lang.get("ingame.no-arguments-supplied"));
        return true;
    }
}
