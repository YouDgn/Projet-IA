package com.example.geminichat.network.packet;

import com.example.geminichat.api.GeminiAPI;
import com.example.geminichat.entity.CacamanEntity;
import com.example.geminichat.util.CommandExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CacamanActionPacket {
    
    private final int entityId;
    private final String actionType;
    private final String data;
    
    public CacamanActionPacket(int entityId, String actionType, String data) {
        this.entityId = entityId;
        this.actionType = actionType;
        this.data = data;
    }
    
    public CacamanActionPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.actionType = buf.readUtf();
        this.data = buf.readUtf();
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.actionType);
        buf.writeUtf(this.data);
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            
            Entity entity = player.level().getEntity(this.entityId);
            if (!(entity instanceof CacamanEntity cacaman)) return;
            
            switch (this.actionType) {
                case "chat" -> handleChat(cacaman, player, this.data);
                case "follow" -> handleFollow(cacaman, player, this.data);
                case "special" -> handleSpecialAction(cacaman, player, this.data);
                case "special_menu" -> handleSpecialMenu(cacaman, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
    
    private void handleChat(CacamanEntity cacaman, ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal("§7[Cacaman123] Traitement en cours..."));
        
        new Thread(() -> {
            try {
                String response = GeminiAPI.sendMessage(message);
                
                player.getServer().execute(() -> {
                    String finalResponse = CommandExecutor.executeCommandsFromResponse(
                        response, 
                        player, 
                        player.getServer()
                    );
                    
                    player.sendSystemMessage(Component.literal("§b[Cacaman123] §f" + finalResponse));
                });
                
            } catch (Exception e) {
                player.getServer().execute(() -> {
                    player.sendSystemMessage(Component.literal("§c[Cacaman123] Erreur: " + e.getMessage()));
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void handleFollow(CacamanEntity cacaman, ServerPlayer player, String behavior) {
        cacaman.setBehavior(behavior);
        cacaman.setOwner(player);
        
        if (behavior.equals("follow")) {
            player.sendSystemMessage(Component.literal("§b[Cacaman123] §fJe te suis maintenant !"));
        } else {
            player.sendSystemMessage(Component.literal("§b[Cacaman123] §fJ'arrete de te suivre."));
        }
    }
    
    private void handleSpecialAction(CacamanEntity cacaman, ServerPlayer player, String action) {
        player.sendSystemMessage(Component.literal("§7[Cacaman123] Analyse de la demande..."));
        
        new Thread(() -> {
            try {
                String prompt = switch (action) {
                    case "mine" -> "Le joueur te demande de miner les blocs proches. Explique ce que tu vas faire et donne la commande pour donner au joueur une pioche en diamant efficacite 5.";
                    case "build" -> "Le joueur te demande de construire une structure. Propose-lui quelques idees de constructions simples (maison, tour, pont, etc.) et demande-lui ce qu'il prefere.";
                    case "analyze" -> buildAnalysisPrompt(cacaman, player);
                    case "collect" -> "Le joueur te demande de collecter les items au sol. Explique que tu vas l'aider et donne une commande pour lui donner un coffre.";
                    default -> action;
                };
                
                String response = GeminiAPI.sendMessage(prompt);
                
                player.getServer().execute(() -> {
                    String finalResponse = CommandExecutor.executeCommandsFromResponse(
                        response, 
                        player, 
                        player.getServer()
                    );
                    
                    player.sendSystemMessage(Component.literal("§b[Cacaman123] §f" + finalResponse));
                });
                
            } catch (Exception e) {
                player.getServer().execute(() -> {
                    player.sendSystemMessage(Component.literal("§c[Cacaman123] Erreur: " + e.getMessage()));
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private String buildAnalysisPrompt(CacamanEntity cacaman, ServerPlayer player) {
        BlockPos pos = cacaman.blockPosition();
        StringBuilder analysis = new StringBuilder("Analyse de la zone autour de la position ");
        analysis.append(pos.getX()).append(" ").append(pos.getY()).append(" ").append(pos.getZ()).append(":\n");
        
        // Analyser les blocs dans un rayon de 5 blocs
        int radius = 5;
        int blockCount = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    Block block = cacaman.level().getBlockState(checkPos).getBlock();
                    if (block != Blocks.AIR) {
                        blockCount++;
                    }
                }
            }
        }
        
        analysis.append("Il y a environ ").append(blockCount).append(" blocs non-air dans un rayon de 5 blocs. ");
        analysis.append("Fais un resume de ce que tu vois et suggere au joueur ce qu'il pourrait faire ici.");
        
        return analysis.toString();
    }
    
    private void handleSpecialMenu(CacamanEntity cacaman, ServerPlayer player) {
        // Le menu spécial sera géré côté client via l'écran
    }
}