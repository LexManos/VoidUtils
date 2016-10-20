package net.minecraftforge.lex.voidutils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityScanner extends TileEntityPowered
{
    public EnumFacing getFacing()
    {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        if (state.getBlock() != VoidUtils.Blocks.quarry)
            return EnumFacing.EAST;
        return state.getValue(BlockQuarry.FACING);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        //if (nbt.hasKey("current"))
        //    this.current = new MutableBlockPos(BlockPos.fromLong(nbt.getLong("current")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt = super.writeToNBT(nbt);
        //nbt.setLong("current", current.toLong());
        return nbt;
    }

    @Override
    protected boolean doWork()
    {
        return false;
    }
}
