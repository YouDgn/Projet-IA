package com.example.geminichat.command;

import com.example.geminichat.entity.CacamanEntity;
import com.example.geminichat.init.ModEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;

public class SpawnCacamanCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawncacaman")
            .requires(source -> source.hasPermission(2))
            .executes(SpawnCacamanCommand::execute));
    }
    
    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§cCette commande doit etre executee par un joueur !"));
            return 0;
        }
        
        ServerLevel level = player.serverLevel();
        
        // Créer l'entité Cacaman
        CacamanEntity cacaman = ModEntities.CACAMAN.get().create(level);
        if (cacaman != null) {
            cacaman.moveTo(
                player.getX() + 2, 
                player.getY(), 
                player.getZ(), 
                player.getYRot(), 
                0
            );
            cacaman.setOwner(player);
            cacaman.finalizeSpawn(level, level.getCurrentDifficultyAt(cacaman.blockPosition()), 
                MobSpawnType.COMMAND, null, null);
            
            level.addFreshEntity(cacaman);
            
            source.sendSuccess(() -> 
                Component.literal("§b[Cacaman123] §fSalut ! Je suis la pour t'aider !"), false);
            return 1;
        }
        
        source.sendFailure(Component.literal("§cImpossible de spawn Cacaman123 !"));
        return 0;
    }
}