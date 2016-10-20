package net.minecraftforge.lex.voidutils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyCell extends Block
{
    public BlockEnergyCell()
    {
        super(Material.IRON);
        this.setUnlocalizedName(VoidUtils.MODID + ":block.energy_cell");
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityEnergyCell();
    }
}
