package com.example.geminichat.entity;

import com.example.geminichat.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class CacamanEntity extends PathfinderMob {
    
    private static final EntityDataAccessor<String> BEHAVIOR = 
        SynchedEntityData.defineId(CacamanEntity.class, EntityDataSerializers.STRING);
    
    private UUID ownerUUID;
    private Player cachedOwner;
    
    public CacamanEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEHAVIOR, "idle");
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Ouvrir le menu GUI
            NetworkHooks.openScreen(serverPlayer, 
                new CacamanMenuProvider(this), 
                buf -> buf.writeInt(this.getId()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }
    
    public void setBehavior(String behavior) {
        this.entityData.set(BEHAVIOR, behavior);
    }
    
    public String getBehavior() {
        return this.entityData.get(BEHAVIOR);
    }
    
    public void setOwner(Player player) {
        this.ownerUUID = player.getUUID();
        this.cachedOwner = player;
    }
    
    public Player getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        }
        if (this.ownerUUID != null && this.level() != null) {
            this.cachedOwner = this.level().getPlayerByUUID(this.ownerUUID);
            return this.cachedOwner;
        }
        return null;
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Behavior", this.getBehavior());
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Behavior")) {
            this.setBehavior(tag.getString("Behavior"));
        }
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }
    
    // Goal personnalisé pour suivre le propriétaire
    private static class FollowOwnerGoal extends Goal {
        private final CacamanEntity entity;
        private Player owner;
        private final double speedModifier = 1.0D;
        private final float stopDistance = 3.0F;
        private final float startDistance = 10.0F;
        
        public FollowOwnerGoal(CacamanEntity entity) {
            this.entity = entity;
        }
        
        @Override
        public boolean canUse() {
            Player owner = this.entity.getOwner();
            if (owner == null || owner.isSpectator()) {
                return false;
            }
            if (!this.entity.getBehavior().equals("follow")) {
                return false;
            }
            if (this.entity.distanceToSqr(owner) < (double)(this.stopDistance * this.stopDistance)) {
                return false;
            }
            this.owner = owner;
            return true;
        }
        
        @Override
        public boolean canContinueToUse() {
            if (this.entity.getNavigation().isDone()) {
                return false;
            }
            if (!this.entity.getBehavior().equals("follow")) {
                return false;
            }
            return this.entity.distanceToSqr(this.owner) > (double)(this.stopDistance * this.stopDistance);
        }
        
        @Override
        public void start() {
            this.entity.getNavigation().moveTo(this.owner, this.speedModifier);
        }
        
        @Override
        public void stop() {
            this.owner = null;
            this.entity.getNavigation().stop();
        }
        
        @Override
        public void tick() {
            this.entity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.entity.getMaxHeadXRot());
            if (this.entity.distanceToSqr(this.owner) > (double)(this.startDistance * this.startDistance)) {
                this.entity.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}