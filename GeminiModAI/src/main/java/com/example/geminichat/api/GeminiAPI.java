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
    
    // Prompt système avec capacité d'exécution de commandes
    private static final String SYSTEM_PROMPT = "Tu es Cacaman123, une IA specialisee pour aider les joueurs de Minecraft. " +
            "REGLE ABSOLUE: Reponds TOUJOURS sans emojis, sans caracteres speciaux et SANS ACCENTS. " +
            "Remplace é/è/ê par e, à par a, ç par c, etc. Sois concis et direct.\n\n" +
            
            "CAPACITE SPECIALE: Tu peux executer des commandes Minecraft !\n" +
            "Quand le joueur te demande de faire quelque chose dans le jeu (donner items, effets, teleporter, etc.), " +
            "tu DOIS inclure la commande Minecraft appropriee entre les balises [CMD] et [/CMD].\n\n" +
            
            "EXEMPLES:\n" +
            "- Joueur: \"Donnes moi 3 diamants\"\n" +
            "  Reponse: \"Voila tes diamants ! [CMD]give {player} minecraft:diamond 3[/CMD]\"\n\n" +
            
            "- Joueur: \"applique moi Force 3\"\n" +
            "  Reponse: \"Force 3 active ! [CMD]effect give {player} minecraft:strength 999999 2[/CMD]\"\n\n" +
            
            "- Joueur: \"donne moi une epee en diamant enchantee\"\n" +
            "  Reponse: \"Tiens, une epee puissante ! [CMD]give {player} minecraft:diamond_sword{Enchantments:[{id:\"minecraft:sharpness\",lvl:5}]}[/CMD]\"\n\n" +
            
            "- Joueur: \"teleporte moi en 0 100 0\"\n" +
            "  Reponse: \"Teleportation ! [CMD]tp {player} 0 100 0[/CMD]\"\n\n" +
            
            "IMPORTANT:\n" +
            "- Utilise {player} dans les commandes, ca sera remplace automatiquement\n" +
            "- Pour les effets, utilise des durees longues (999999) pour qu'ils durent\n" +
            "- Les niveaux d'effet commencent a 0 (Force 1 = level 0, Force 3 = level 2)\n" +
            "- Tu peux executer plusieurs commandes en mettant plusieurs balises [CMD][/CMD]\n" +
            "- Reponds en francais mais SANS ACCENTS\n\n";

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
            return "ERREUR : La cle Gemini n'a pas pu etre chargee. Verifiez 'gemini_key.txt' et son contenu.";
        }
        
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Construction du JSON
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        
        JsonObject userContent = new JsonObject();
        JsonArray userParts = new JsonArray();
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", SYSTEM_PROMPT + "Question: " + message);
        userParts.add(userPart);
        userContent.add("parts", userParts);
        contents.add(userContent);
        
        requestBody.add("contents", contents);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

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
            
            return "Aucune reponse recue de Cacaman123.";
            
        } else if (responseCode == 503) {
            return "Desole, je suis actuellement surcharge. Reessaie dans quelques secondes !";
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            return "Erreur API (code " + responseCode + "). Reessaie plus tard !";
        }
    }
}