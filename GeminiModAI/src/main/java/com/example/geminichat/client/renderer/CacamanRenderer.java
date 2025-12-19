package com.example.geminichat.client.renderer;

import com.example.geminichat.entity.CacamanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class CacamanRenderer extends MobRenderer<CacamanEntity, HumanoidModel<CacamanEntity>> {
    
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation("geminichat", "textures/entity/cacaman.png");
    
    public CacamanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, 
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()));
    }
    
    @Override
    public ResourceLocation getTextureLocation(CacamanEntity entity) {
        return TEXTURE;
    }
    
    @Override
    public void render(CacamanEntity entity, float entityYaw, float partialTicks, 
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Rendre le modèle de joueur
        this.model.young = false;
        this.model.riding = entity.isPassenger();
        this.model.crouching = entity.isCrouching();
        
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}