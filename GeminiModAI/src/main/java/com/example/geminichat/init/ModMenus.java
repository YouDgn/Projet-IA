package com.example.geminichat.init;

import com.example.geminichat.GeminiChatMod;
import com.example.geminichat.menu.CacamanMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, GeminiChatMod.MODID);
    
    public static final RegistryObject<MenuType<CacamanMenu>> CACAMAN_MENU = MENUS.register("cacaman_menu",
        () -> IForgeMenuType.create((windowId, inv, data) -> {
            int entityId = data.readInt();
            var entity = inv.player.level().getEntity(entityId);
            if (entity instanceof com.example.geminichat.entity.CacamanEntity cacaman) {
                return new CacamanMenu(windowId, inv, cacaman);
            }
            return null;
        }));
}