package com.minecolonies.blocks;

import com.minecolonies.tilentities.TileEntityHutBuilder;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHutBuilder extends BlockInformator
{
    public final String name = "blockHutBuilder";

    protected BlockHutBuilder()
    {
        super(Material.wood);
        setBlockName(getName());
        GameRegistry.registerBlock(this, getName());
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityHutBuilder();
    }
}
