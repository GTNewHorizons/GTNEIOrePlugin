package pers.gwyog.gtneioreplugin.plugin.gregtech5;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import cpw.mods.fml.common.Loader;
import gregtech.api.GregTech_API;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper.OreLayerWrapper;

import java.util.ArrayList;
import java.util.List;

public class PluginGT5VeinStat extends PluginGT5Base {

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOutputId())) {
            for (String veinName : GT5OreLayerHelper.mapOreLayerWrapper.keySet()) {
                OreLayerWrapper oreLayerWrapper = GT5OreLayerHelper.mapOreLayerWrapper.get(veinName);
                int maximumMaterialIndex = 7;
                List<ItemStack> stackListPrimary = new ArrayList<>();
                List<ItemStack> stackListSecondary = new ArrayList<>();
                List<ItemStack> stackListBetween = new ArrayList<>();
                List<ItemStack> stackListSporadic = new ArrayList<>();
                for (int i = 0; i < maximumMaterialIndex; i++) {
                    stackListPrimary.add(new ItemStack(GregTech_API.sBlockOres1, 1, oreLayerWrapper.Meta[0] + i * 1000));
                    stackListSecondary.add(new ItemStack(GregTech_API.sBlockOres1, 1, oreLayerWrapper.Meta[1] + i * 1000));
                    stackListBetween.add(new ItemStack(GregTech_API.sBlockOres1, 1, oreLayerWrapper.Meta[2] + i * 1000));
                    stackListSporadic.add(new ItemStack(GregTech_API.sBlockOres1, 1, oreLayerWrapper.Meta[3] + i * 1000));
                }
                this.arecipes.add(new CachedVeinStatRecipe(veinName, stackListPrimary, stackListSecondary, stackListBetween, stackListSporadic));
            }
        } else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack stack) {
        if (stack.getUnlocalizedName().startsWith("gt.blockores")) {
            if (stack.getItemDamage() > 16000) {
                super.loadCraftingRecipes(stack);
                return;
            }
            short baseMeta = (short) (stack.getItemDamage() % 1000);
            for (OreLayerWrapper worldGen : GT5OreLayerHelper.mapOreLayerWrapper.values()) {
                if (worldGen.Meta[0] == baseMeta || worldGen.Meta[1] == baseMeta || worldGen.Meta[2] == baseMeta || worldGen.Meta[3] == baseMeta) {
                    int maximumMaterialIndex =
                        getMaximumMaterialIndex(baseMeta, false);
                    List<ItemStack> stackListPrimary = new ArrayList<>();
                    List<ItemStack> stackListSecondary = new ArrayList<>();
                    List<ItemStack> stackListBetween = new ArrayList<>();
                    List<ItemStack> stackListSporadic = new ArrayList<>();
                    for (int i = 0; i < maximumMaterialIndex; i++) {
                        stackListPrimary.add(new ItemStack(GregTech_API.sBlockOres1, 1, worldGen.Meta[0] + i * 1000));
                        stackListSecondary.add(new ItemStack(GregTech_API.sBlockOres1, 1, worldGen.Meta[1] + i * 1000));
                        stackListBetween.add(new ItemStack(GregTech_API.sBlockOres1, 1, worldGen.Meta[2] + i * 1000));
                        stackListSporadic.add(new ItemStack(GregTech_API.sBlockOres1, 1, worldGen.Meta[3] + i * 1000));
                    }
                    this.arecipes.add(new CachedVeinStatRecipe(worldGen.veinName, stackListPrimary, stackListSecondary, stackListBetween, stackListSporadic));
                }
            }
        } else
            super.loadCraftingRecipes(stack);
    }

    @Override
    public void drawExtras(int recipe) {
        CachedVeinStatRecipe crecipe = (CachedVeinStatRecipe) this.arecipes.get(recipe);
        OreLayerWrapper oreLayer = GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);

        String sDimNames = GT5OreLayerHelper.bufferedDims.get(oreLayer);

        if(Loader.isModLoaded("visualprospecting")) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + I18n.format(oreLayer.veinName) + " " + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
        }
        else {
            if (getGTOreLocalizedName(oreLayer.Meta[0]).contains("Ore"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + getGTOreLocalizedName(oreLayer.Meta[0]).split("Ore")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
            else if (getGTOreLocalizedName(oreLayer.Meta[0]).contains("Sand"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + getGTOreLocalizedName(oreLayer.Meta[0]).split("Sand")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
            else
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + getGTOreLocalizedName(oreLayer.Meta[0]) + " " + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
        }

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.primaryOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[0]), 2, 50, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.secondaryOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[1]), 2, 60, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.betweenOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[2]), 2, 70, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.sporadicOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[3]), 2, 80, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.genHeight") + ": " + oreLayer.worldGenHeightRange, 2, 90, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.weightedChance") + ": " + Integer.toString(oreLayer.randomWeight), 100, 90, 0x404040, false);

        drawDimNames(sDimNames);

        drawSeeAllRecipesLabel();
    }

    @Override
    public String getOutputId() {
        return "GTOrePluginVein";
    }

    @Override
    public String getRecipeName() {
        return I18n.format("gtnop.gui.veinStat.name");
    }

    /**
     * The dimension names for a given recipe identifier
     *
     * @param recipe identifier
     * @return A CSV string of dimension name abbreviations
     */
    @Override
    protected String getDimensionNames(int recipe) {
        CachedVeinStatRecipe crecipe = (CachedVeinStatRecipe) this.arecipes.get(recipe);
        OreLayerWrapper oreLayer = GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);
        return GT5OreLayerHelper.bufferedDims.get(oreLayer);
    }

    public class CachedVeinStatRecipe extends CachedRecipe {
        public String veinName;
        public PositionedStack positionedStackPrimary;
        public PositionedStack positionedStackSecondary;
        public PositionedStack positionedStackBetween;
        public PositionedStack positionedStackSporadic;

        public CachedVeinStatRecipe(String veinName, List<ItemStack> stackListPrimary, List<ItemStack> stackListSecondary,
                                    List<ItemStack> stackListBetween, List<ItemStack> stackListSporadic) {
            this.veinName = veinName;
            positionedStackPrimary = new PositionedStack(stackListPrimary, 2, 0);
            positionedStackSecondary = new PositionedStack(stackListSecondary, 22, 0);
            positionedStackBetween = new PositionedStack(stackListBetween, 42, 0);
            positionedStackSporadic = new PositionedStack(stackListSporadic, 62, 0);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> ingredientsList = new ArrayList<>();
            positionedStackPrimary.setPermutationToRender((cycleticks / 20) % positionedStackPrimary.items.length);
            positionedStackSecondary.setPermutationToRender((cycleticks / 20) % positionedStackPrimary.items.length);
            positionedStackBetween.setPermutationToRender((cycleticks / 20) % positionedStackPrimary.items.length);
            positionedStackSporadic.setPermutationToRender((cycleticks / 20) % positionedStackPrimary.items.length);
            ingredientsList.add(positionedStackPrimary);
            ingredientsList.add(positionedStackSecondary);
            ingredientsList.add(positionedStackBetween);
            ingredientsList.add(positionedStackSporadic);
            return ingredientsList;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

    }

}
