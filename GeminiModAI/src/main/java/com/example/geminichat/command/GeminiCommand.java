package com.example.geminichat.command;

import com.example.geminichat.api.GeminiAPI;
import com.example.geminichat.util.CommandExecutor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GeminiCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ia")
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(GeminiCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String message = StringArgumentType.getString(context, "message");
        CommandSourceStack source = context.getSource();
        
        // Vérifier que c'est un joueur
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c[Cacaman123] Cette commande doit etre utilisee par un joueur !"));
            return 0;
        }
        
        // Message de chargement
        source.sendSuccess(() -> Component.literal("§7[Cacaman123] Traitement en cours..."), false);
        
        // Appel asynchrone pour ne pas bloquer le serveur
        new Thread(() -> {
            try {
                String response = GeminiAPI.sendMessage(message);
                
                // Exécuter les commandes contenues dans la réponse
                source.getServer().execute(() -> {
                    String finalResponse = CommandExecutor.executeCommandsFromResponse(
                        response, 
                        player, 
                        source.getServer()
                    );
                    
                    source.sendSuccess(() -> Component.literal("§b[Cacaman123] §f" + finalResponse), false);
                });
                
            } catch (Exception e) {
                source.getServer().execute(() -> {
                    source.sendFailure(Component.literal("§c[Cacaman123] Erreur: " + e.getMessage()));
                });
                e.printStackTrace();
            }
        }).start();
        
        return 1;
    }
}