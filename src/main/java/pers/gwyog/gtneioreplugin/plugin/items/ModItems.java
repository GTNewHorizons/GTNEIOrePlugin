package pers.gwyog.gtneioreplugin.plugin.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import pers.gwyog.gtneioreplugin.GTNEIOrePlugin;
import pers.gwyog.gtneioreplugin.plugin.renderer.ItemDimensionDisplayRenderer;

public class ModItems {

    public static Item itemDimensionDisplay = new ItemDimensionDisplay();

    public static void init() {
        GameRegistry.registerItem(itemDimensionDisplay, "itemDimensionDisplay", GTNEIOrePlugin.MODID);
        ItemDimensionDisplay.loadItems();
        new ItemDimensionDisplayRenderer();
    }
}
