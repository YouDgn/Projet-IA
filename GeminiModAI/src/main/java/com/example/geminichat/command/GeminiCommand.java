package com.example.geminichat.command;

import com.example.geminichat.api.GeminiAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GeminiCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ia")
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(GeminiCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String message = StringArgumentType.getString(context, "message");
        CommandSourceStack source = context.getSource();
        
        // Message de chargement
        source.sendSuccess(() -> Component.literal("§7[Gemini] Traitement en cours..."), false);
        
        // Appel asynchrone pour ne pas bloquer le serveur
        new Thread(() -> {
            try {
                String response = GeminiAPI.sendMessage(message);
                
                // Envoyer la réponse au joueur
                source.getServer().execute(() -> {
                    source.sendSuccess(() -> Component.literal("§b[Gemini] §f" + response), false);
                });
                
            } catch (Exception e) {
                source.getServer().execute(() -> {
                    source.sendFailure(Component.literal("§c[Gemini] Erreur: " + e.getMessage()));
                });
                e.printStackTrace();
            }
        }).start();
        
        return 1;
    }
}