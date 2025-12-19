package com.example.geminichat.client;

import com.example.geminichat.GeminiChatMod;
import com.example.geminichat.client.renderer.CacamanRenderer;
import com.example.geminichat.client.screen.CacamanScreen;
import com.example.geminichat.init.ModEntities;
import com.example.geminichat.init.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GeminiChatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.CACAMAN_MENU.get(), CacamanScreen::new);
        });
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CACAMAN.get(), CacamanRenderer::new);
    }
}