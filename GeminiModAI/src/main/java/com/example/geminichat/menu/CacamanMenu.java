package com.example.geminichat.menu;

import com.example.geminichat.entity.CacamanEntity;
import com.example.geminichat.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CacamanMenu extends AbstractContainerMenu {
    
    private final CacamanEntity entity;
    
    public CacamanMenu(int containerId, Inventory playerInventory, CacamanEntity entity) {
        super(ModMenus.CACAMAN_MENU.get(), containerId);
        this.entity = entity;
    }
    
    public CacamanEntity getEntity() {
        return this.entity;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return this.entity.isAlive() && 
               this.entity.distanceTo(player) < 8.0F;
    }
}