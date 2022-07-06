package pers.gwyog.gtneioreplugin.plugin.gregtech5;

import static pers.gwyog.gtneioreplugin.util.OreVeinLayer.*;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import cpw.mods.fml.common.Loader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreLayerHelper.OreLayerWrapper;

public class PluginGT5VeinStat extends PluginGT5Base {

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
                addVeinWithLayers(oreVein, getMaximumMaterialIndex(oreId, false));
            }
        }
    }

    private void addVeinWithLayers(OreLayerWrapper oreVein, int maximumMaterialIndex) {
        this.arecipes.add(new CachedVeinStatRecipe(
                oreVein.veinName,
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
        OreLayerWrapper oreLayer = getOreLayer(recipe);

        drawVeinName(oreLayer);
        drawVeinLayerNames(oreLayer);
        drawVeinInfo(oreLayer);

        String sDimNames = GT5OreLayerHelper.bufferedDims.get(oreLayer);
        drawDimNames(sDimNames);

        drawSeeAllRecipesLabel();
    }

    private OreLayerWrapper getOreLayer(int recipe) {
        CachedVeinStatRecipe crecipe = (CachedVeinStatRecipe) this.arecipes.get(recipe);
        return GT5OreLayerHelper.mapOreLayerWrapper.get(crecipe.veinName);
    }

    private void drawVeinName(OreLayerWrapper oreLayer) {
        if (Loader.isModLoaded("visualprospecting")) {
            drawVeinNameLine(I18n.format(oreLayer.veinName) + " ");
        } else {
            String veinName = getGTOreLocalizedName(oreLayer.Meta[VEIN_PRIMARY]);
            if (veinName.contains("Ore")) drawVeinNameLine(veinName.split("Ore")[0]);
            else if (veinName.contains("Sand")) drawVeinNameLine(veinName.split("Sand")[0]);
            else drawVeinNameLine(veinName + " ");
        }
    }

    private void drawVeinNameLine(String veinName) {
        drawLine("gtnop.gui.nei.veinName", veinName + I18n.format("gtnop.gui" + ".nei.vein"), 2, 20);
    }

    private void drawVeinLayerNames(OreLayerWrapper oreLayer) {
        drawVeinLayerNameLine(oreLayer, VEIN_PRIMARY, 50);
        drawVeinLayerNameLine(oreLayer, VEIN_SECONDARY, 60);
        drawVeinLayerNameLine(oreLayer, VEIN_BETWEEN, 70);
        drawVeinLayerNameLine(oreLayer, VEIN_SPORADIC, 80);
    }

    private void drawVeinLayerNameLine(OreLayerWrapper oreLayer, int veinLayer, int height) {
        drawLine(getOreVeinLayerName(veinLayer), getGTOreLocalizedName(oreLayer.Meta[veinLayer]), 2, height);
    }

    private void drawVeinInfo(OreLayerWrapper oreLayer) {
        drawLine("gtnop.gui.nei.genHeight", oreLayer.worldGenHeightRange, 2, 90);
        drawLine("gtnop.gui.nei.weightedChance", Integer.toString(oreLayer.randomWeight), 100, 90);
    }

    private void drawLine(String lineKey, String value, int x, int y) {
        GuiDraw.drawString(I18n.format(lineKey) + ": " + value, x, y, 0x404040, false);
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
        OreLayerWrapper oreLayer = getOreLayer(recipe);
        return GT5OreLayerHelper.bufferedDims.get(oreLayer);
    }

    public class CachedVeinStatRecipe extends CachedRecipe {
        public String veinName;
        public PositionedStack positionedStackPrimary;
        public PositionedStack positionedStackSecondary;
        public PositionedStack positionedStackBetween;
        public PositionedStack positionedStackSporadic;

        public CachedVeinStatRecipe(
                String veinName,
                List<ItemStack> stackListPrimary,
                List<ItemStack> stackListSecondary,
                List<ItemStack> stackListBetween,
                List<ItemStack> stackListSporadic) {
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
