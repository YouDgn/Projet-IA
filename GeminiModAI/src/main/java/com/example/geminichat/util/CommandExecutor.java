package com.example.geminichat.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {
    
    // Détecte si la réponse de l'IA contient des commandes à exécuter
    public static String executeCommandsFromResponse(String response, ServerPlayer player, MinecraftServer server) {
        
        // Pattern pour détecter les commandes entre balises [CMD]...[/CMD]
        Pattern pattern = Pattern.compile("\\[CMD\\](.*?)\\[/CMD\\]");
        Matcher matcher = pattern.matcher(response);
        
        StringBuilder cleanResponse = new StringBuilder();
        int lastEnd = 0;
        
        while (matcher.find()) {
            // Ajoute le texte avant la commande
            cleanResponse.append(response.substring(lastEnd, matcher.start()));
            
            // Récupère la commande
            String command = matcher.group(1).trim();
            
            // Exécute la commande
            try {
                // Remplace {player} par le nom du joueur
                command = command.replace("{player}", player.getName().getString());
                
                // Exécute la commande depuis le serveur
                CommandSourceStack source = server.createCommandSourceStack();
                server.getCommands().performPrefixedCommand(source, command);
                
                cleanResponse.append("§a[Commande executee]§r");
            } catch (Exception e) {
                cleanResponse.append("§c[Erreur execution]§r");
                e.printStackTrace();
            }
            
            lastEnd = matcher.end();
        }
        
        // Ajoute le reste du texte
        cleanResponse.append(response.substring(lastEnd));
        
        return cleanResponse.toString();
    }
}