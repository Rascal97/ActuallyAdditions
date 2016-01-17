/*
 * This file ("LensColor.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items.lens;


import de.ellpeck.actuallyadditions.api.internal.IAtomicReconstructor;
import de.ellpeck.actuallyadditions.api.lens.Lens;
import de.ellpeck.actuallyadditions.mod.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class LensColor extends Lens{

    public static final int ENERGY_USE = 200;
    public static final Object[] CONVERTABLE_BLOCKS = new Object[]{
            Items.dye,
            Blocks.wool,
            Blocks.stained_glass,
            Blocks.stained_glass_pane,
            Blocks.stained_hardened_clay,
            Blocks.carpet,
            InitBlocks.blockColoredLamp,
            InitBlocks.blockColoredLampOn
    };
    //Thanks to xdjackiexd for this, as I couldn't be bothered
    private static final float[][] possibleColorLensColors = {
            {158F, 43F, 39F}, //Red
            {234F, 126F, 53F}, //Orange
            {194F, 181F, 28F}, //Yellow
            {57F, 186F, 46F}, //Lime Green
            {54F, 75F, 24F}, //Green
            {99F, 135F, 210F}, //Light Blue
            {38F, 113F, 145F}, //Cyan
            {37F, 49F, 147F}, //Blue
            {126F, 52F, 191F}, //Purple
            {190F, 73F, 201F}, //Magenta
            {217F, 129F, 153F}, //Pink
            {86F, 51F, 28F}, //Brown
    };

    @SuppressWarnings("unchecked")
    @Override
    public boolean invoke(BlockPos hitBlock, IAtomicReconstructor tile){
        if(hitBlock != null){
            if(Util.arrayContains(CONVERTABLE_BLOCKS, PosUtil.getBlock(hitBlock, tile.getWorldObject())) >= 0 && tile.getEnergy() >= ENERGY_USE){
                int meta = PosUtil.getMetadata(hitBlock, tile.getWorldObject());
                if(meta >= 15){
                    PosUtil.setMetadata(hitBlock, tile.getWorldObject(), 0, 2);
                }
                else{
                    PosUtil.setMetadata(hitBlock, tile.getWorldObject(), meta+1, 2);
                }
                tile.extractEnergy(ENERGY_USE);
            }

            ArrayList<EntityItem> items = (ArrayList<EntityItem>)tile.getWorldObject().getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), hitBlock.getX()+1, hitBlock.getY()+1, hitBlock.getZ()+1));
            for(EntityItem item : items){
                if(!item.isDead && item.getEntityItem() != null && tile.getEnergy() >= ENERGY_USE){
                    if(Util.arrayContains(CONVERTABLE_BLOCKS, item.getEntityItem().getItem()) >= 0 || Util.arrayContains(CONVERTABLE_BLOCKS, Block.getBlockFromItem(item.getEntityItem().getItem())) >= 0){
                        ItemStack newStack = item.getEntityItem().copy();
                        int meta = newStack.getItemDamage();
                        if(meta >= 15){
                            newStack.setItemDamage(0);
                        }
                        else{
                            newStack.setItemDamage(meta+1);
                        }

                        item.setDead();

                        EntityItem newItem = new EntityItem(tile.getWorldObject(), item.posX, item.posY, item.posZ, newStack);
                        tile.getWorldObject().spawnEntityInWorld(newItem);

                        tile.extractEnergy(ENERGY_USE);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public float[] getColor(){
        float[] colors = possibleColorLensColors[Util.RANDOM.nextInt(possibleColorLensColors.length)];
        return new float[]{colors[0]/255F, colors[1]/255F, colors[2]/255F};
    }

    @Override
    public int getDistance(){
        return 10;
    }
}