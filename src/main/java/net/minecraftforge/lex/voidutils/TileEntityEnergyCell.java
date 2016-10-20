package net.minecraftforge.lex.voidutils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityEnergyCell extends TileEntity implements ITickable
{
    private static final int POWER_PER_TICK = 1000;

    @Override
    public void update()
    {
        if (this.getWorld().isRemote)
            return;

        for (EnumFacing dir : EnumFacing.VALUES)
        {
            EnumFacing side = dir.getOpposite();
            BlockPos pos = this.getPos().offset(dir);

            ICapabilityProvider provider = this.getWorld().getTileEntity(pos);
            if (provider == null)
                continue;

            IEnergyStorage storage = null;
            if (provider.hasCapability(CapabilityEnergy.ENERGY, side))
                storage = provider.getCapability(CapabilityEnergy.ENERGY, side);

            //TODO? Other compatible energy types?

            if (storage == null)
                continue;

            if (storage.canReceive())
                storage.receiveEnergy(POWER_PER_TICK, false);
        }
    }
}
