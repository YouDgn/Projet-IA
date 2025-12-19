package com.example.geminichat.network;

import com.example.geminichat.GeminiChatMod;
import com.example.geminichat.network.packet.CacamanActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(GeminiChatMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    public static void registerPackets() {
        CHANNEL.messageBuilder(CacamanActionPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(CacamanActionPacket::encode)
            .decoder(CacamanActionPacket::new)
            .consumerMainThread(CacamanActionPacket::handle)
            .add();
    }
    
    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
    
    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}