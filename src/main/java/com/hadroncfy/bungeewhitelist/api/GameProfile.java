package com.hadroncfy.bungeewhitelist.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

public class GameProfile {

    public static Gson GSON = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(UUID.class, new TypeAdapter<UUID>() {
                @Override
                public void write(JsonWriter out, UUID value) throws IOException {
                    if(value == null){
                        out.nullValue();
                    }else {
                        out.value(value.toString());
                    }
                }

                @Override
                public UUID read(JsonReader in) throws IOException {
                    if(in.peek() == JsonToken.NULL){
                        in.nextNull();
                        return null;
                    }
                    String stringUUID = in.nextString();
                    if(stringUUID.length() == 32){
                        return UUID.fromString(stringUUID.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                    } else {
                        return UUID.fromString(stringUUID);
                    }
                }
            })
            .create();

    @SerializedName(value = "uuid", alternate = "id")
    public final UUID uuid;
    public final String name;

    public GameProfile(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }
}