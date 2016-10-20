package net.minecraftforge.lex.voidutils;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

public abstract class TileEntityPowered extends TileEntity implements ITickable
{
    protected int ENERGY_PER_ACTION = 50;
    protected int MAX_RECEIVE = 100;
    protected int MAX_STORAGE = 1000000;
    protected boolean ENABLED = true;
    protected MutibleEnergy STORAGE;

    protected TileEntityPowered(){}
    protected TileEntityPowered(int maxStorage, int maxReceive, int costPerAction)
    {
        this.MAX_STORAGE = maxStorage;
        this.MAX_RECEIVE = maxReceive;
        this.ENERGY_PER_ACTION = costPerAction;
    }

    private MutibleEnergy getStorage()
    {
        if (this.STORAGE == null)
            this.STORAGE = new MutibleEnergy(MAX_STORAGE, MAX_RECEIVE, 0);
        return this.STORAGE;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing)
    {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(this.getStorage());
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("energy"))
            this.getStorage().setEnergy(nbt.getInteger("energy"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (this.STORAGE != null)
            nbt.setInteger("energy", this.getStorage().getEnergyStored());
        return nbt;
    }

    @Override
    public void update()
    {
        if (this.worldObj.isRemote || this.STORAGE == null)
            return;

        while (ENABLED && STORAGE.getEnergyStored() >= ENERGY_PER_ACTION)
        {
            if (doWork())
                STORAGE.setEnergy(STORAGE.getEnergyStored() - ENERGY_PER_ACTION);
            else
                break; //Break out so we dont have a infinite loop
        }
    }

    //Return true if the work has been done and energy should be removed!
    //Returning false will cause the loop to break and not execute anymore that tick.
    protected abstract boolean doWork();

    //This is a private class so we can access the energy internally directly, but not allow externals to touch it.
    private static class MutibleEnergy extends EnergyStorage
    {
        public MutibleEnergy(int capacity, int maxReceive, int maxExtract)
        {
            super(capacity, maxReceive, maxExtract);
        }

        protected void setEnergy(int energy)
        {
            this.energy = energy < 0 ? 0 : energy;
        }
    }
}
