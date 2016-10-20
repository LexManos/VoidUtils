package net.minecraftforge.lex.voidutils;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import static net.minecraftforge.fml.relauncher.Side.*;

import java.lang.reflect.Field;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
    modid = VoidUtils.MODID,
    name = "VoidUtils",
    version = "1.0",
    clientSideOnly = false,
    serverSideOnly = false,
    dependencies = "required-after:Forge@[12.18.1.2092,)"
)
@Mod.EventBusSubscriber
public class VoidUtils
{
    public static final String MODID = "void_utils";
    public static final int DIM = 1024; //TODO Make Config, or pick random dim?

    @Instance(MODID)
    public static VoidUtils INSTANCE = null;

    @ObjectHolder(MODID)
    public static class Blocks
    {
        public static final Block quarry = null;
        public static final Block energy_cell = null;
    }
    @ObjectHolder(MODID)
    public static class Items
    {
        public static final Item quarry = null;
        public static final Item energy_cell = null;
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        DimensionManager.registerDimension(DIM, DimensionType.OVERWORLD); //We need to select a random dim id.
        //We also should look into not doing this but instead adding a config GUI to the TE itself.
        //And being able to select what dim we wanna move over.
        //Would be cool to be like 'I want a chunk of the nether in the overworld!

        // This is where crafting recipes would go IF I HAD ANNY!!!
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld().provider.getDimension() == DIM && !event.getWorld().isRemote)
        {
            WorldServer world = (WorldServer)event.getWorld();

            ReflectionHelper.setPrivateValue(World.class, world,
                new DerivedWorldInfo(world.getWorldInfo())
                {
                    public WorldType getTerrainType()
                    {
                        return WorldType.DEFAULT;
                    }
                },
                "field_72986_" + "A", "worldInfo", "field_72986_A");
            world.provider.registerWorld(world);
            world.getChunkProvider().chunkGenerator = world.provider.createChunkGenerator();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
            new BlockQuarry().setRegistryName(MODID, "quarry").setCreativeTab(CreativeTabs.BUILDING_BLOCKS),
            new BlockEnergyCell().setRegistryName(MODID, "energy_cell").setCreativeTab(CreativeTabs.BUILDING_BLOCKS)
        );
        //If only TEs were a registry.. HUMM MOJANG HUMMMMMMMMMMMMM!!!!
        GameRegistry.registerTileEntity(TileEntityQuarry.class, MODID + ".quarry");
        GameRegistry.registerTileEntity(TileEntityEnergyCell.class, MODID + ".energy_cell");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        Block[] blocks = {
            Blocks.quarry,
            Blocks.energy_cell
        };
        for (Block block : blocks)
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    @SubscribeEvent
    @SideOnly(CLIENT)
    public static void registerModels(ModelRegistryEvent event) throws Exception
    {
        for (Field f : Items.class.getDeclaredFields())
        {
            Item item = (Item)f.get(null);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityQuarry.class, new QuarryRenderer());

    }
}
