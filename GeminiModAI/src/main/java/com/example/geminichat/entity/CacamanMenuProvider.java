package com.example.geminichat.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.example.geminichat.menu.CacamanMenu;

public class CacamanMenuProvider implements MenuProvider {
    
    private final CacamanEntity entity;
    
    public CacamanMenuProvider(CacamanEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Cacaman123");
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CacamanMenu(containerId, inventory, this.entity);
    }
}