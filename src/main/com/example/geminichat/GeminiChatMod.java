package com.example.geminichat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.example.geminichat.command.GeminiCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GeminiChatMod.MODID)
public class GeminiChatMod {
    public static final String MODID = "geminichat";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiChatMod.MODID);

    public GeminiChatMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Gemini Chat Mod initialized!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GeminiCommand.register(event.getDispatcher());
    }
}