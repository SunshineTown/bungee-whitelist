package com.hadroncfy.bungeewhitelist.api;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MinecraftAPI {
    private static final String HOST = "api.mojang.com";
    private static final String UUID_ENDPOINT = "/users/profiles/minecraft/";

    public static GameProfile getUUIDByName(String name) throws IOException {
        URL u = new URL("https", HOST, UUID_ENDPOINT + name);
        String responseText = new String(Resources.toByteArray(u),  StandardCharsets.UTF_8);
        if (responseText.isEmpty()){
            return null;
        }
        return GameProfile.GSON.fromJson(responseText, GameProfile.class);
    }

    public static GameProfile getOfflineUUID(String name){
        UUID uuid =  UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        return new GameProfile(uuid, name);
    }
}