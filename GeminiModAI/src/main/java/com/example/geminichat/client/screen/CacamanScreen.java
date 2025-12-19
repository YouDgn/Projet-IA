package com.example.geminichat.client.screen;

import com.example.geminichat.menu.CacamanMenu;
import com.example.geminichat.network.ModNetwork;
import com.example.geminichat.network.packet.CacamanActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CacamanScreen extends AbstractContainerScreen<CacamanMenu> {
    
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation("geminichat", "textures/gui/cacaman_gui.png");
    
    private EditBox chatBox;
    private Button discussButton;
    private Button followButton;
    private Button specialActionButton;
    
    public CacamanScreen(CacamanMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Zone de texte pour discuter
        this.chatBox = new EditBox(this.font, x + 10, y + 30, 156, 20, Component.literal(""));
        this.chatBox.setMaxLength(256);
        this.chatBox.setHint(Component.literal("Pose une question a Cacaman123..."));
        this.addRenderableWidget(this.chatBox);
        
        // Bouton Discuter
        this.discussButton = Button.builder(
            Component.literal("Discuter"),
            button -> this.sendChatMessage()
        )
        .bounds(x + 10, y + 55, 156, 20)
        .build();
        this.addRenderableWidget(this.discussButton);
        
        // Bouton Suis-moi
        this.followButton = Button.builder(
            Component.literal("Suis-moi"),
            button -> this.toggleFollow()
        )
        .bounds(x + 10, y + 80, 156, 20)
        .build();
        this.addRenderableWidget(this.followButton);
        
        // Bouton Action Spéciale
        this.specialActionButton = Button.builder(
            Component.literal("Action Speciale"),
            button -> this.openSpecialActionMenu()
        )
        .bounds(x + 10, y + 105, 156, 20)
        .build();
        this.addRenderableWidget(this.specialActionButton);
        
        this.updateFollowButtonLabel();
    }
    
    private void sendChatMessage() {
        String message = this.chatBox.getValue().trim();
        if (!message.isEmpty()) {
            ModNetwork.sendToServer(new CacamanActionPacket(
                this.menu.getEntity().getId(),
                "chat",
                message
            ));
            this.chatBox.setValue("");
        }
    }
    
    private void toggleFollow() {
        String currentBehavior = this.menu.getEntity().getBehavior();
        String newBehavior = currentBehavior.equals("follow") ? "idle" : "follow";
        
        ModNetwork.sendToServer(new CacamanActionPacket(
            this.menu.getEntity().getId(),
            "follow",
            newBehavior
        ));
        
        this.updateFollowButtonLabel();
    }
    
    private void updateFollowButtonLabel() {
        if (this.menu.getEntity().getBehavior().equals("follow")) {
            this.followButton.setMessage(Component.literal("Arreter de suivre"));
        } else {
            this.followButton.setMessage(Component.literal("Suis-moi"));
        }
    }
    
    private void openSpecialActionMenu() {
        ModNetwork.sendToServer(new CacamanActionPacket(
            this.menu.getEntity().getId(),
            "special_menu",
            ""
        ));
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
    // Correction ici : on retire mouseX, mouseY et partialTick
    this.renderBackground(graphics); 
    
    super.render(graphics, mouseX, mouseY, partialTick);
    this.renderTooltip(graphics, mouseX, mouseY);
    
    // Titre
    graphics.drawString(this.font, "Cacaman123 - Assistant IA", 
        this.leftPos + 8, this.topPos + 6, 0x404040, false);
}
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter ou Numpad Enter
            this.sendChatMessage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}