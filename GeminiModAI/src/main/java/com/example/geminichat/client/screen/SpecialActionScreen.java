package com.example.geminichat.client.screen;

import com.example.geminichat.menu.CacamanMenu;
import com.example.geminichat.network.ModNetwork;
import com.example.geminichat.network.packet.CacamanActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SpecialActionScreen extends AbstractContainerScreen<CacamanMenu> {
    
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation("geminichat", "textures/gui/cacaman_gui.png");
    
    public SpecialActionScreen(CacamanMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Bouton Miner
        this.addRenderableWidget(Button.builder(
            Component.literal("Miner les blocs proches"),
            button -> this.sendSpecialAction("mine")
        )
        .bounds(x + 10, y + 30, 156, 20)
        .build());
        
        // Bouton Construire
        this.addRenderableWidget(Button.builder(
            Component.literal("Construire une structure"),
            button -> this.sendSpecialAction("build")
        )
        .bounds(x + 10, y + 55, 156, 20)
        .build());
        
        // Bouton Analyser
        this.addRenderableWidget(Button.builder(
            Component.literal("Analyser la zone"),
            button -> this.sendSpecialAction("analyze")
        )
        .bounds(x + 10, y + 80, 156, 20)
        .build());
        
        // Bouton Collecter
        this.addRenderableWidget(Button.builder(
            Component.literal("Collecter les items"),
            button -> this.sendSpecialAction("collect")
        )
        .bounds(x + 10, y + 105, 156, 20)
        .build());
        
        // Bouton Retour
        this.addRenderableWidget(Button.builder(
            Component.literal("Retour"),
            button -> this.onClose()
        )
        .bounds(x + 10, y + 130, 156, 20)
        .build());
    }
    
    private void sendSpecialAction(String action) {
        ModNetwork.sendToServer(new CacamanActionPacket(
            this.menu.getEntity().getId(),
            "special",
            action
        ));
        this.onClose();
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        
        // Titre
        graphics.drawString(this.font, "Actions Speciales", 
            this.leftPos + 8, this.topPos + 6, 0x404040, false);
    }
}