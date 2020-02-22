package com.hadroncfy.bungeewhitelist.api;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Whitelist {

    private static Type WHITELIST_TYPE = new TypeToken<List<GameProfile>>(){}.getType();

    private File whitelistFile;
    private List<GameProfile> whitelist;

    public Whitelist(File whitelistFile){
        this.whitelistFile = whitelistFile;
    }

    public void loadWhitelist() throws IOException {
        if (!whitelistFile.exists()){
            whitelist = new LinkedList<>();
            saveWhitelist();
            return;
        }
        try (FileReader reader = new FileReader(whitelistFile)) {
            whitelist = GameProfile.GSON.fromJson(reader, WHITELIST_TYPE);
        }
    }

    public void saveWhitelist() throws IOException {
        try (FileWriter writer = new FileWriter(whitelistFile)){
            GameProfile.GSON.toJson(whitelist, writer);
        }
    }

    public boolean isAllow(UUID uuid){
        for (GameProfile profile : whitelist) {
            if(profile.uuid.equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public GameProfile update(GameProfile profile){
        for (GameProfile p : whitelist) {
            if(profile.uuid.equals(p.uuid)){
                whitelist.remove(p);
                whitelist.add(profile);
                return profile;
            }
        }
        whitelist.add(profile);
        return null;
    }

    private UUID getUUID(String name){
        for (GameProfile profile : whitelist) {
            if (profile.name.equalsIgnoreCase(name)){
                return profile.uuid;
            }
        }
        return null;
    }

    public GameProfile remove(UUID uuid){
        for (GameProfile profile : whitelist) {
            if(profile.uuid.equals(uuid)){
                whitelist.remove(profile);
                return profile;
            }
        }
        return null;
    }

    public GameProfile removeByName(String name) {
        for (GameProfile profile : whitelist) {
            if(profile.name.equals(name)){
                whitelist.remove(profile);
                return profile;
            }
        }
        return null;
    }

    public List<String> list() {
        List<String> ret = new ArrayList<>();
        for (GameProfile profile : whitelist) {
            ret.add(profile.name);
        }
        return ret;
    }
}
