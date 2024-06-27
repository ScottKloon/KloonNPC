package net.jabux.jabuxnpc.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinFetcher {
    public static Property fetchSkinProperty(String playerName) {
        try {
            // Primeiro, obtenha o UUID do jogador
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                // Verifique se a resposta HTTP não é 200 OK
                System.err.println("Erro ao obter UUID para jogador: " + playerName);
                return null;
            }

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            if (!jsonElement.isJsonObject()) {
                System.err.println("Resposta inválida ao obter UUID para jogador: " + playerName);
                return null;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String uuid = jsonObject.get("id").getAsString();

            // Agora, obtenha as propriedades da skin usando o UUID
            URL sessionUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            connection = (HttpURLConnection) sessionUrl.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                // Verifique se a resposta HTTP não é 200 OK
                System.err.println("Erro ao obter propriedades da skin para UUID: " + uuid);
                return null;
            }

            jsonElement = jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            if (!jsonElement.isJsonObject()) {
                System.err.println("Resposta inválida ao obter propriedades da skin para UUID: " + uuid);
                return null;
            }

            jsonObject = jsonElement.getAsJsonObject();
            JsonObject properties = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject();
            String value = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();
            return new Property("textures", value, signature);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
