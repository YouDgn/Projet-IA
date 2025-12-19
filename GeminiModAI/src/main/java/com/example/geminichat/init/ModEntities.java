package com.example.geminichat.init;

import com.example.geminichat.GeminiChatMod;
import com.example.geminichat.entity.CacamanEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GeminiChatMod.MODID);
    
    public static final RegistryObject<EntityType<CacamanEntity>> CACAMAN = ENTITIES.register("cacaman",
        () -> EntityType.Builder.of(CacamanEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .clientTrackingRange(10)
            .build("cacaman"));
}