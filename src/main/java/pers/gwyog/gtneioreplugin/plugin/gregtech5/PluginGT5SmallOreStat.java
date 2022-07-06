package pers.gwyog.gtneioreplugin.plugin.gregtech5;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import gregtech.api.GregTech_API;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import pers.gwyog.gtneioreplugin.util.GT5OreSmallHelper;
import pers.gwyog.gtneioreplugin.util.GT5OreSmallHelper.OreSmallWrapper;

public class PluginGT5SmallOreStat extends PluginGT5Base {

    private static final int SMALL_ORE_BASE_META = 16000;

    @Override
    public void drawExtras(int recipe) {
        CachedOreSmallRecipe crecipe = (CachedOreSmallRecipe) this.arecipes.get(recipe);
        OreSmallWrapper oreSmall = GT5OreSmallHelper.mapOreSmallWrapper.get(crecipe.oreGenName);
        String sDimNames = GT5OreSmallHelper.bufferedDims.get(oreSmall);
        GuiDraw.drawString(
                I18n.format("gtnop.gui.nei.oreName") + ": " + getGTOreLocalizedName((short) (oreSmall.oreMeta + SMALL_ORE_BASE_META)),
                2,
                18,
                0x404040,
                false);

        GuiDraw.drawString(
                I18n.format("gtnop.gui.nei.genHeight") + ": " + oreSmall.worldGenHeightRange, 2, 31, 0x404040, false);
        GuiDraw.drawString(
                I18n.format("gtnop.gui.nei.amount") + ": " + oreSmall.amountPerChunk, 2, 44, 0x404040, false);
        GuiDraw.drawString(
                I18n.format("gtnop.gui.nei.chanceDrops") + ": ", 2, 83 + getRestrictBiomeOffset(), 0x404040, false);
        GuiDraw.drawString(I18n.format("gtnop.gui.nei.worldNames") + ": ", 2, 100, 0x404040, false);

        drawDimNames(sDimNames);
        drawSeeAllRecipesLabel();
    }

    public int getRestrictBiomeOffset() {
        return GT5OreSmallHelper.restrictBiomeSupport ? 0 : -13;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOutputId()))
            for (ItemStack stack : GT5OreSmallHelper.oreSmallList) loadCraftingRecipes(stack);
        else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack stack) {
        if (stack.getUnlocalizedName().startsWith("gt.blockores")) {
            short baseMeta = (short) (stack.getItemDamage() % 1000);
            for (OreSmallWrapper oreSmallWorldGen : GT5OreSmallHelper.mapOreSmallWrapper.values()) {
                if (oreSmallWorldGen.oreMeta == baseMeta) {
                    List<ItemStack> stackList = new ArrayList<>();
                    int maximumIndex = getMaximumMaterialIndex(baseMeta, true);
                    for (int i = 0; i < maximumIndex; i++)
                        stackList.add(new ItemStack(
                                GregTech_API.sBlockOres1, 1, oreSmallWorldGen.oreMeta + SMALL_ORE_BASE_META + i * 1000));
                    List<ItemStack> materialDustStackList = new ArrayList<>();
                    for (int i = 0; i < maximumIndex; i++)
                        materialDustStackList.add(
                                GT_OreDictUnificator.get(OrePrefixes.dust, GT5OreSmallHelper.getDroppedDusts()[i], 1L));
                    this.arecipes.add(new CachedOreSmallRecipe(
                            oreSmallWorldGen.oreGenName,
                            stackList,
                            materialDustStackList,
                            GT5OreSmallHelper.mapOreMetaToOreDrops.get(baseMeta)));
                }
            }
        } else if (GT5OreSmallHelper.mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
            short baseMeta = GT5OreSmallHelper.mapOreDropUnlocalizedNameToOreMeta.get(stack.getUnlocalizedName());
            for (String oreGenName : GT5OreSmallHelper.mapOreSmallWrapper.keySet()) {
                OreSmallWrapper oreSmallWrapper = GT5OreSmallHelper.mapOreSmallWrapper.get(oreGenName);
                if (oreSmallWrapper.oreMeta == baseMeta) {
                    List<ItemStack> stackList = new ArrayList<>();
                    for (int i = 0; i < 7; i++)
                        stackList.add(new ItemStack(GregTech_API.sBlockOres1, 1, baseMeta + SMALL_ORE_BASE_META + i * 1000));
                    List<ItemStack> materialDustStackList = new ArrayList<>();
                    for (int i = 0; i < 7; i++)
                        materialDustStackList.add(
                                GT_OreDictUnificator.get(OrePrefixes.dust, GT5OreSmallHelper.getDroppedDusts()[i], 1L));
                    this.arecipes.add(new CachedOreSmallRecipe(
                            GT5OreSmallHelper.mapOreSmallWrapper.get(oreGenName).oreGenName,
                            stackList,
                            materialDustStackList,
                            GT5OreSmallHelper.mapOreMetaToOreDrops.get(baseMeta)));
                }
            }
        } else super.loadCraftingRecipes(stack);
    }

    @Override
    public String getOutputId() {
        return "GTOrePluginOreSmall";
    }

    @Override
    public String getRecipeName() {
        return I18n.format("gtnop.gui.smallOreStat.name");
    }

    /**
     * The dimension names for a given recipe identifier
     *
     * @param recipe identifier
     * @return A CSV string of dimension name abbreviations
     */
    @Override
    protected String getDimensionNames(int recipe) {
        CachedOreSmallRecipe crecipe = (CachedOreSmallRecipe) this.arecipes.get(recipe);
        OreSmallWrapper oreSmall = GT5OreSmallHelper.mapOreSmallWrapper.get(crecipe.oreGenName);
        return GT5OreSmallHelper.bufferedDims.get(oreSmall);
    }

    public class CachedOreSmallRecipe extends CachedRecipe {
        public String oreGenName;
        public PositionedStack positionedStackOreSmall;
        public PositionedStack positionedStackMaterialDust;
        public List<PositionedStack> positionedDropStackList;

        public CachedOreSmallRecipe(
                String oreGenName,
                List<ItemStack> stackList,
                List<ItemStack> materialDustStackList,
                List<ItemStack> dropStackList) {
            this.oreGenName = oreGenName;
            this.positionedStackOreSmall = new PositionedStack(stackList, 2, 0);
            this.positionedStackMaterialDust =
                    new PositionedStack(materialDustStackList, 43, 79 + getRestrictBiomeOffset());
            List<PositionedStack> positionedDropStackList = new ArrayList<>();
            int i = 1;
            for (ItemStack stackDrop : dropStackList)
                positionedDropStackList.add(new PositionedStack(
                        stackDrop, 43 + 20 * (i % 4), 79 + 16 * ((i++) / 4) + getRestrictBiomeOffset()));
            this.positionedDropStackList = positionedDropStackList;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            positionedStackOreSmall.setPermutationToRender((cycleticks / 20) % positionedStackOreSmall.items.length);
            positionedStackMaterialDust.setPermutationToRender(
                    (cycleticks / 20) % positionedStackMaterialDust.items.length);
            positionedDropStackList.add(positionedStackOreSmall);
            positionedDropStackList.add(positionedStackMaterialDust);
            return positionedDropStackList;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }
}
