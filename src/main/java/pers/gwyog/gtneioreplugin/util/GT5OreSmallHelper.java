package pers.gwyog.gtneioreplugin.util;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;

public class GT5OreSmallHelper {
    private static final int SMALL_ORE_BASE_META = 16000;
    public static boolean restrictBiomeSupport = false;
    public static boolean gcBasicSupport = false;
    public static List<ItemStack> oreSmallList = new ArrayList<>();
    public static HashMap<String, OreSmallWrapper> mapOreSmallWrapper = new HashMap<>();
    public static HashMap<String, Short> mapOreDropUnlocalizedNameToOreMeta = new HashMap<>();
    public static HashMap<Short, List<ItemStack>> mapOreMetaToOreDrops = new HashMap<>();
    public static HashMap<OreSmallWrapper, String> bufferedDims = new HashMap<>();

    public GT5OreSmallHelper() {
        checkExtraSupport();
        ItemStack stack;
        Materials material;
        short meta;
        for (GT_Worldgen worldGen : GregTech_API.sWorldgenList)
            if (worldGen.mWorldGenName.startsWith("ore.small.") && worldGen instanceof GT_Worldgen_GT_Ore_SmallPieces) {
                GT_Worldgen_GT_Ore_SmallPieces worldGenSmallPieces = (GT_Worldgen_GT_Ore_SmallPieces) worldGen;
                meta = worldGenSmallPieces.mMeta;
                if (meta < 0) break;
                material = GregTech_API.sGeneratedMaterials[meta];
                mapOreSmallWrapper.put(worldGen.mWorldGenName, new OreSmallWrapper(worldGenSmallPieces));
                if (!mapOreMetaToOreDrops.containsKey(meta)) {
                    List<ItemStack> stackList = new ArrayList<>();
                    stack = GT_OreDictUnificator.get(
                            OrePrefixes.gemExquisite,
                            material,
                            GT_OreDictUnificator.get(OrePrefixes.gem, material, 1L),
                            1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(
                            OrePrefixes.gemFlawless,
                            material,
                            GT_OreDictUnificator.get(OrePrefixes.gem, material, 1L),
                            1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(OrePrefixes.gem, material, 1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(
                            OrePrefixes.gemFlawed,
                            material,
                            GT_OreDictUnificator.get(OrePrefixes.crushed, material, 1L),
                            1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(OrePrefixes.crushed, material, 1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(
                            OrePrefixes.gemChipped,
                            material,
                            GT_OreDictUnificator.get(OrePrefixes.dustImpure, material, 1L),
                            1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    stack = GT_OreDictUnificator.get(OrePrefixes.dustImpure, material, 1L);
                    if (stack != null && !mapOreDropUnlocalizedNameToOreMeta.containsKey(stack.getUnlocalizedName())) {
                        mapOreDropUnlocalizedNameToOreMeta.put(stack.getUnlocalizedName(), meta);
                        stackList.add(stack);
                    }
                    oreSmallList.add(new ItemStack(GregTech_API.sBlockOres1, 1, meta + SMALL_ORE_BASE_META));
                    mapOreMetaToOreDrops.put(meta, stackList);
                }
            }
        for (OreSmallWrapper oreSmallWrapper : mapOreSmallWrapper.values()) {
            bufferedDims.put(oreSmallWrapper, GT5CFGHelper.GT5CFGSmallOres(oreSmallWrapper.oreGenName));
        }
    }

    private static void checkExtraSupport() {
        Class<?> clazzGTOreSmall = null;
        try {
            clazzGTOreSmall = Class.forName("gregtech.common" + ".GT_Worldgen_GT_Ore_SmallPieces");
        } catch (ClassNotFoundException e) {
        }
        if (clazzGTOreSmall != null) {
            try {
                Field fieldRestrictBiome = clazzGTOreSmall.getField("mRestrictBiome");
                restrictBiomeSupport = true;
            } catch (Exception e) {
            }
            try {
                Field fieldGCMoon = clazzGTOreSmall.getField("mMoon");
                Field fieldGCMars = clazzGTOreSmall.getField("mMars");
                gcBasicSupport = true;
            } catch (Exception e) {
            }
        }
    }

    public static Materials[] getDroppedDusts() {
        return new Materials[] {
            Materials.Stone,
            Materials.Netherrack,
            Materials.Endstone,
            Materials.GraniteBlack,
            Materials.GraniteRed,
            Materials.Marble,
            Materials.Basalt,
            Materials.Stone
        };
    }

    public static class OreSmallWrapper {
        public String oreGenName;
        public short oreMeta;
        public String worldGenHeightRange;
        public short amountPerChunk;
        public String restrictBiome;

        public OreSmallWrapper(GT_Worldgen_GT_Ore_SmallPieces worldGen) {
            this.oreGenName = worldGen.mWorldGenName;
            this.oreMeta = worldGen.mMeta;
            this.worldGenHeightRange = worldGen.mMinY + "-" + worldGen.mMaxY;
            this.amountPerChunk = worldGen.mAmount;
        }

        public List<ItemStack> getMaterialDrops(int maximumIndex) {
            List<ItemStack> stackList = new ArrayList<>();
            for (int i = 0; i < maximumIndex; i++)
                stackList.add(new ItemStack(GregTech_API.sBlockOres1, 1, oreMeta + SMALL_ORE_BASE_META + i * 1000));
            return stackList;
        }
    }
}
