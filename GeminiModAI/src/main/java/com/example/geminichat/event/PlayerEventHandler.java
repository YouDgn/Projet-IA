package com.example.geminichat.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.geminichat.GeminiChatMod;

@Mod.EventBusSubscriber(modid = GeminiChatMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventHandler {
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Envoyer le message de bienvenue avec le nom du joueur
            String playerName = player.getName().getString();
            String welcomeMessage = "§bCoucou " + playerName + ", c'est moi Cacaman123 ! Si t'as besoin d'aide pour Minecraft, n'hesite surtout pas a me parler via la commande /ia :)";
            
            player.sendSystemMessage(Component.literal(welcomeMessage));
        }
    }
}