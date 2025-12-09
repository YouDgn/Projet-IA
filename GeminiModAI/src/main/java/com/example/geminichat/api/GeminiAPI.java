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
// Imports pour la lecture de fichier sécurisée
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
// Import spécifique à Forge pour trouver le répertoire du jeu
import net.minecraftforge.fml.loading.FMLPaths;

public class GeminiAPI {
    
    // La clé API est chargée au démarrage depuis un fichier, évitant la fuite (leaked).
    private static final String API_KEY = loadApiKey();
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    /**
     * Charge la clé API depuis un fichier texte dans le répertoire du jeu (GAMEDIR).
     * @return La clé API si trouvée, ou une chaîne vide en cas d'erreur.
     */
    private static String loadApiKey() {
        // APRÈS (cherche dans le dossier config/)
        Path keyPath = FMLPaths.CONFIGDIR.get().resolve("gemini_key.txt");

        try {
            if (Files.exists(keyPath)) {
                // Lecture du fichier, suppression des espaces blancs (trim) et retour de la clé.
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
        
        // Vérification de sécurité avant de tenter l'appel réseau
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "ERREUR : La clé Gemini n'a pas pu être chargée. Vérifiez 'gemini_key.txt' et son contenu.";
        }
        
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Construction du JSON pour l'API Gemini
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        
        part.addProperty("text", message);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
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

            // Parse la réponse JSON
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
            
            return "Aucune réponse reçue de Gemini.";
            
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