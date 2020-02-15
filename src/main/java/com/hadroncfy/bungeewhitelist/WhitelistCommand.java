package com.hadroncfy.bungeewhitelist;

import com.hadroncfy.bungeewhitelist.api.GameProfile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhitelistCommand extends Command implements TabExecutor {

    private WhitelistPlugin plugin;

    public WhitelistCommand(WhitelistPlugin plugin) {
        super("bwhitelist", "bungeewhitelist.use");
        this.plugin = plugin;
    }

    private static List<String> filterStart(List<String> l, String prefix){
        List<String> ret = new ArrayList<>();
        for (String s: l){
            if (s.startsWith(prefix)){
                ret.add(s);
            }
        }
        return ret;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 0 || args.length == 1){
            ret.add("on");
            ret.add("off");
            ret.add("list");
            ret.add("reload");
            ret.add("remove");
            ret.add("add"); 
            if (args.length == 1){
                ret = filterStart(ret, args[0]);
            }
        }
        else if (args.length == 2){
            if (args[0].equals("remove")){
                ret = filterStart(plugin.getWhitelist().list(), args[1]);
            }
        }

        return ret;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "on":
                    plugin.enabled = true;
                    plugin.broadcast(new TextComponent("White list is now enabled"));
                    return;
                case "off":
                    plugin.enabled = false;
                    plugin.broadcast(new TextComponent("White list is now disabled"));
                    return;
                case "reload":
                    try {
                        plugin.getWhitelist().loadWhitelist();
                        plugin.broadcast(new TextComponent("Reloaded whitelist"));
                    } catch (IOException e) {
                        plugin.broadcast(new TextComponent("Failed reload whitelist"));
                        e.printStackTrace();
                    }
                    return;
                case "list":
                    List<String> players = plugin.getWhitelist().list();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < players.size(); i++) {
                        if (i > 0)
                            sb.append(", ");
                        sb.append(players.get(i));
                    }
                    if (players.size() == 0) {
                        sender.sendMessage(new TextComponent("There're no players in the white list."));
                    } else {
                        sender.sendMessage(new TextComponent(sb.toString()));
                    }
                    return;
            }
        } else if (args.length == 2) {
            switch (args[0]){
                case "add":
                    new Thread(() -> {
                        try {
                            GameProfile profile = plugin.createUUID(args[1]);
                            if (profile != null){
                                plugin.getWhitelist().update(profile);
                                sender.sendMessage(new TextComponent("Done"));
                                plugin.broadcast(new TextComponent("Added " + profile.name + "(" + profile.uuid + ") to white list."));
                            } else {
                                sender.sendMessage(new TextComponent("Player " + args[1] + " not found."));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(new TextComponent("Failed to retrieve UUID."));
                        }
                    }).start();
                    return;
                case "remove":
                    GameProfile profile = plugin.getWhitelist().removeByName(args[1]);
                    if (profile != null){
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(profile.uuid);
                        if(player != null){
                            player.disconnect(new TextComponent(plugin.kickMessage));
                        }
                        plugin.broadcast(new TextComponent("Removed " + args[1] + " from white list."));
                    }
                    else {
                        sender.sendMessage(new TextComponent("Player not in the white list."));
                    }
                    return;
            }
        }
        sender.sendMessage(new TextComponent("Incorrect arguments."));
    }

}