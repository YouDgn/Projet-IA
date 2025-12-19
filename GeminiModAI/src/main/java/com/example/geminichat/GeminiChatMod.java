package com.example.geminichat;

import com.example.geminichat.command.SpawnCacamanCommand;
import com.example.geminichat.init.ModEntities;
import com.example.geminichat.init.ModMenus;
import com.example.geminichat.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.example.geminichat.command.GeminiCommand;
import com.example.geminichat.entity.CacamanEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GeminiChatMod.MODID)
public class GeminiChatMod {
    public static final String MODID = "geminichat";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiChatMod.MODID);

    public GeminiChatMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Enregistrer les registres
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onEntityAttributeCreation);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Gemini Chat Mod initialized!");
        
        event.enqueueWork(() -> {
            ModNetwork.registerPackets();
        });
    }
    
    @SubscribeEvent
    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CACAMAN.get(), CacamanEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GeminiCommand.register(event.getDispatcher());
        SpawnCacamanCommand.register(event.getDispatcher());
    }
}