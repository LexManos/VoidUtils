package net.minecraftforge.lex.voidutils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class TileEntityQuarry extends TileEntityPowered
{
    private static final int GEN_RANGE = 1;
    WorldServer reference;
    BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos(0, -1, 0);
    BlockPos _end;

    public EnumFacing getFacing()
    {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        if (state.getBlock() != VoidUtils.Blocks.quarry)
            return EnumFacing.EAST;
        return state.getValue(BlockQuarry.FACING);
    }
    public BlockPos getStart()
    {
        EnumFacing dir = getFacing().getOpposite();
        return this.getPos().offset(dir).down(this.getPos().getY());
    }
    public BlockPos getEnd()
    {
        if (_end == null)
        {
            EnumFacing dir = getFacing().getOpposite();
            _end = this.getStart();
            if (dir.getAxis() == EnumFacing.Axis.X)
                _end = _end.add(dir.getFrontOffsetX() * 15, 256, dir.rotateY().getFrontOffsetZ() * 15);
            else
                _end = _end.add(dir.rotateY().getFrontOffsetX() * 15, 256, dir.getFrontOffsetZ() * 15);
        }
        return _end;
    }
    public BlockPos getCurrent()
    {
        return this.current;
    }

    //This is an attempt to get the grid to render always, but we don't have a hook to prevent full chunk culling so thats an issue
    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
    @Override
    public double getMaxRenderDistanceSquared()
    {
        return Double.MAX_VALUE;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (this.reference != null)
        {
            ChunkProviderServer provider = this.reference.getChunkProvider();
            int sX = this.getStart().getX() / 16 - GEN_RANGE;
            int sZ = this.getStart().getZ() / 16 - GEN_RANGE;
            int eX = this.getEnd().getX() / 16 + GEN_RANGE;
            int eZ = this.getEnd().getZ() / 16 + GEN_RANGE;
            for (int x = sX; x <= eX; x++)
                for (int z = sZ; z <= eZ; z++)
                    if (provider.chunkExists(x, z))
                        provider.unload(provider.getLoadedChunk(x, z));
        }
    }

    public void loadChunks()
    {
        if (this.worldObj.isRemote)
            return;

        this.reference = DimensionManager.getWorld(VoidUtils.DIM);
        if (this.reference == null)
        {
            DimensionManager.initDimension(VoidUtils.DIM);
            this.reference = DimensionManager.getWorld(VoidUtils.DIM);
        }

        //We need to load the chunk, and the chunks around it before we start copying, so that the chunk gets decorated.
        ChunkProviderServer provider = this.reference.getChunkProvider();
        int sX = this.getStart().getX() / 16 - GEN_RANGE;
        int sZ = this.getStart().getZ() / 16 - GEN_RANGE;
        int eX = this.getEnd().getX() / 16 + GEN_RANGE;
        int eZ = this.getEnd().getZ() / 16 + GEN_RANGE;
        for (int x = sX; x <= eX; x++)
            for (int z = sZ; z <= eZ; z++)
                provider.loadChunk(x, z);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("current"))
            this.current = new MutableBlockPos(BlockPos.fromLong(nbt.getLong("current")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setLong("current", current.toLong());
        return nbt;
    }

    @Override
    protected boolean doWork()
    {
        if (this.reference == null)
            loadChunks();

        if (this.reference == null) //Shit borked
            return false;

        if (this.current.getY() > 255)
        {
            this.ENABLED = false;
            return false;
        }

        if (this.current.getY() < 0)
            this.current.setPos(this.getStart());

        boolean didWork = false;
        IBlockState remote = this.reference.getBlockState(current);
        IBlockState local = this.getWorld().getBlockState(current);
        BlockPos imm = current.toImmutable();
        if (remote.getBlock() != Blocks.AIR)
            System.currentTimeMillis();
        if (local.getBlock().isReplaceable(this.getWorld(), imm) || local.getBlock().isAir(local, this.getWorld(), imm))
        {
            this.getWorld().setBlockState(imm, remote, 2);
            //TODO: Tile entities? Do we care?
            if (!remote.getBlock().isAir(remote, this.getWorld(), imm))
                didWork = true;
        }

        //EnumFacing dir = this.getFacing().getOpposite();
        BlockPos end = this.getEnd(); //We do this lazy load do it can cache the right value
        BlockPos start = this.getStart();
        boolean xPos = start.getX() < end.getX();
        boolean zPos = start.getZ() < end.getZ();
        current.move(xPos ? EnumFacing.EAST : EnumFacing.WEST);

        if ((xPos && current.getX() > end.getX()) ||
            (!xPos && current.getX() < end.getX()))
            current.setPos(getStart().getX(), current.getY(), current.getZ() + (zPos ? 1 : -1));

        if ((zPos && current.getZ() > end.getZ()) ||
            (!zPos && current.getZ() < end.getZ()))
            current.setPos(current.getX(), current.getY() + 1, getStart().getZ());

        if (current.getY() > end.getY())
        {
            current.setPos(getStart().getX(), 256, getStart().getZ());
            this.ENABLED = false; //Finished!
        }

        return false;
    }
}
