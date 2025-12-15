package com.example.geminichat.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import net.minecraftforge.fml.loading.FMLPaths;

public class GeminiAPI {
    
    private static final String API_KEY = loadApiKey();
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    
    // Prompt système pour définir le comportement de l'IA
    private static final String SYSTEM_PROMPT = "Tu es Cacaman123, une IA specialisee pour aider les joueurs de Minecraft. " +
            "Tu dois TOUJOURS repondre sans emojis, sans caracteres speciaux et SANS ACCENTS. " +
            "Remplace tous les accents par des lettres normales (e au lieu de é/è/ê, a au lieu de à, etc.). " +
            "Sois concis, direct et utile pour tout ce qui concerne Minecraft. " +
            "Exemple: 'Voila comment creer une table de craft' au lieu de 'Voilà comment créer une table de craft'.";

    private static String loadApiKey() {
        Path keyPath = FMLPaths.CONFIGDIR.get().resolve("gemini_key.txt");

        try {
            if (Files.exists(keyPath)) {
                return Files.readString(keyPath, StandardCharsets.UTF_8).trim();
            } else {
                System.err.println("ERREUR GRAVE: Fichier de clé API manquant. Veuillez créer 'gemini_key.txt' ici : " + keyPath.toAbsolutePath());
                return "";
            }
        } catch (IOException e) {
            System.err.println("ERREUR: Impossible de lire la clé API. Vérifiez les permissions.");
            e.printStackTrace();
            return ""; 
        }
    }

    public static String sendMessage(String message) throws Exception {
        
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "ERREUR : La clé Gemini n'a pas pu être chargée. Vérifiez 'gemini_key.txt' et son contenu.";
        }
        
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Construction du JSON avec le prompt système
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        
        // Ajout du prompt système
        JsonObject systemContent = new JsonObject();
        JsonArray systemParts = new JsonArray();
        JsonObject systemPart = new JsonObject();
        systemPart.addProperty("text", SYSTEM_PROMPT);
        systemParts.add(systemPart);
        systemContent.add("parts", systemParts);
        systemContent.addProperty("role", "user");
        contents.add(systemContent);
        
        // Ajout du message utilisateur
        JsonObject userContent = new JsonObject();
        JsonArray userParts = new JsonArray();
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", message);
        userParts.add(userPart);
        userContent.add("parts", userParts);
        userContent.addProperty("role", "user");
        contents.add(userContent);
        
        requestBody.add("contents", contents);

        // Envoi de la requête
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Lecture de la réponse
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            
            if (jsonResponse.has("candidates")) {
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    JsonObject contentObj = candidate.getAsJsonObject("content");
                    JsonArray partsArray = contentObj.getAsJsonArray("parts");
                    if (partsArray.size() > 0) {
                        return partsArray.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
            
            return "Aucune réponse reçue de Cacaman123.";
            
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            throw new Exception("Erreur API (code " + responseCode + "): " + errorResponse.toString());
        }
    }
}