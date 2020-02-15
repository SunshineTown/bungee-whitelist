package com.hadroncfy.bungeewhitelist;

import com.hadroncfy.bungeewhitelist.api.GameProfile;
import com.hadroncfy.bungeewhitelist.api.MinecraftAPI;
import com.hadroncfy.bungeewhitelist.api.Whitelist;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;

public class WhitelistPlugin extends Plugin implements Listener {
    public boolean enabled = false;
    public String kickMessage = "Vous n'étes pas dans la liste blanche!";

    private Whitelist whitelist;

    public Whitelist getWhitelist() {
        return whitelist;
    }

    @Override
    public void onEnable(){
        this.whitelist = new Whitelist(new File(getDataFolder(), "whitelist.json"));
        try {
            loadConfig();
            getLogger().info("Loaded config file");
        } catch (Exception e) {
            e.printStackTrace();
            getProxy().stop("Failed load configuration: " + e.getMessage());
        }
        try {
            whitelist.loadWhitelist();
            getLogger().info("Loaded config file");
        }catch (IOException e){
            e.printStackTrace();
            getProxy().stop("Failed load whitelist: " + e.getMessage());
        }
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            whitelist.saveWhitelist();
            saveConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
        File configFile = new File(getDataFolder(), "config.yml");
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()){
            saveConfig();
            return;
        }

        Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
        enabled = config.getBoolean("enabled");
        kickMessage = config.getString("kick-message");
    }
 
    private void saveConfig(){
        File configFile = new File(getDataFolder(), "config.yml");
        configFile.getParentFile().mkdirs();
        try {
            Configuration config = new Configuration();
            config.set("enabled", enabled);
            config.set("kick-message", kickMessage);
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config, configFile);
        }
        catch (Exception e) {
            getLogger().severe("Failed to save configuration!");
        }
    }

    @EventHandler
    public void onNetworkJoin(LoginEvent e) {
        PendingConnection p = e.getConnection();
        if (enabled){
            if (whitelist.isAllow(p.getUniqueId())){
                whitelist.update(new GameProfile(p.getUniqueId(), p.getName()));
            } else {
                p.disconnect(new TextComponent(kickMessage));
                getLogger().info("Disconnected non-whitelisted player " + p.getName());
            }
        }
    }

    public GameProfile createUUID(String name) throws IOException{
        return getProxy().getConfig().isOnlineMode() ? MinecraftAPI.getUUIDByName(name) : MinecraftAPI.getOfflineUUID(name);
    }

    public void broadcast(BaseComponent c){
        for (ProxiedPlayer player: getProxy().getPlayers()){
            player.sendMessage(ChatMessageType.CHAT, c);
        }
    }
}