package pers.gwyog.gtneioreplugin.plugin.gregtech5;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper.OreLayerWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PluginGT5VeinStat extends PluginGT5Base {
    private static final int VEIN_PRIMARY = 0;
    private static final int VEIN_SECONDARY = 1;
    private static final int VEIN_BETWEEN = 2;
    private static final int VEIN_SPORADIC = 3;

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOutputId())) {
            for (OreLayerWrapper oreVein : getAllVeins()) {
                addVeinWithLayers(oreVein, 7);
            }
        } else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack stack) {
        if (stack.getUnlocalizedName().startsWith("gt.blockores")) {
            if (stack.getItemDamage() > 16000) {
                super.loadCraftingRecipes(stack);
            } else {
                loadMatchingVeins((short) (stack.getItemDamage() % 1000));
            }
        } else super.loadCraftingRecipes(stack);
    }

    private void loadMatchingVeins(short oreId) {
        for (OreLayerWrapper oreVein : getAllVeins()) {
            if (oreVein.containsOre(oreId)) {
                addVeinWithLayers(oreVein, getMaximumMaterialIndex(oreId,
                    false));
            }
        }
    }

    private void addVeinWithLayers(OreLayerWrapper oreVein,
                                   int maximumMaterialIndex) {
        this.arecipes.add(new CachedVeinStatRecipe(oreVein.veinName,
            oreVein.getVeinLayerOre(maximumMaterialIndex, VEIN_PRIMARY),
            oreVein.getVeinLayerOre(maximumMaterialIndex, VEIN_SECONDARY),
            oreVein.getVeinLayerOre(maximumMaterialIndex, VEIN_BETWEEN),
            oreVein.getVeinLayerOre(maximumMaterialIndex, VEIN_SPORADIC)));
    }

    private Collection<OreLayerWrapper> getAllVeins() {
        return GT5OreLayerHelper.mapOreLayerWrapper.values();
    }

    @Override
    public void drawExtras(int recipe) {
        CachedVeinStatRecipe crecipe =
            (CachedVeinStatRecipe) this.arecipes.get(recipe);
        OreLayerWrapper oreLayer =
            GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);

        String sDimNames = GT5OreLayerHelper.bufferedDims.get(oreLayer);

        if (Loader.isModLoaded("visualprospecting")) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") + ": " + I18n.format(oreLayer.veinName) + " " + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
        } else {
            if (getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]).contains(
                "Ore"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") +
                    ":" + " " + getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]).split("Ore")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
            else if (getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]).contains("Sand"))
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") +
                    ":" + " " + getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]).split("Sand")[0] + "" + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
            else
                GuiDraw.drawString(I18n.format("gtnop.gui.nei.veinName") +
                    ":" + " " + getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]) + " " + I18n.format("gtnop.gui.nei.vein"), 2, 20, 0x404040, false);
        }

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.primaryOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]), 2, 50, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.secondaryOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[VEIN_SECONDARY]), 2, 60, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.betweenOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[VEIN_BETWEEN]), 2, 70, 0x404040, false);

        GuiDraw.drawString(I18n.format("gtnop.gui.nei.sporadicOre") + ": " + getGTOreLocalizedName(oreLayer.Meta[VEIN_SPORADIC]), 2, 80, 0x404040, false);

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
        CachedVeinStatRecipe crecipe =
            (CachedVeinStatRecipe) this.arecipes.get(recipe);
        OreLayerWrapper oreLayer =
            GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);
        return GT5OreLayerHelper.bufferedDims.get(oreLayer);
    }

    public class CachedVeinStatRecipe extends CachedRecipe {
        public String veinName;
        public PositionedStack positionedStackPrimary;
        public PositionedStack positionedStackSecondary;
        public PositionedStack positionedStackBetween;
        public PositionedStack positionedStackSporadic;

        public CachedVeinStatRecipe(String veinName,
                                    List<ItemStack> stackListPrimary,
                                    List<ItemStack> stackListSecondary,
                                    List<ItemStack> stackListBetween,
                                    List<ItemStack> stackListSporadic) {
            this.veinName = veinName;
            positionedStackPrimary = new PositionedStack(stackListPrimary, 2,
                0);
            positionedStackSecondary = new PositionedStack(stackListSecondary
                , 22, 0);
            positionedStackBetween = new PositionedStack(stackListBetween, 42
                , 0);
            positionedStackSporadic = new PositionedStack(stackListSporadic,
                62, 0);
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
