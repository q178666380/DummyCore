package DummyCore.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;

import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Client.AdvancedModelLoader;
import DummyCore.Client.GuiMainMenuOld;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.IModelRegisterer;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Client.ModelUtils;
import DummyCore.Client.obj.ObjModelLoader;
import DummyCore.Client.techne.TechneModelLoader;
import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import DummyCore.Items.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;

//Internal
public class NetProxy_Client extends NetProxy_Server{

	public static final Hashtable<String, ShaderGroup> shaders = new Hashtable<String, ShaderGroup>();
	public static final Hashtable<Block,Integer[]> cachedMeta = new Hashtable<Block,Integer[]>();
	public static final Hashtable<Item,Integer[]> cachedMetaI = new Hashtable<Item,Integer[]>();

	//Why vanilla's(or is it forge?) thread checking?
	public void handlePacketS35(SPacketUpdateTileEntity packetIn) {
		WorldClient world = Minecraft.getMinecraft().world;
		if(world != null && world.isBlockLoaded(packetIn.getPos())) {
			TileEntity tileentity = world.getTileEntity(packetIn.getPos());

			if(tileentity == null)
				return;
			int i = packetIn.getTileEntityType();

			if(i == 1 && tileentity instanceof TileEntityMobSpawner || i == 2 && tileentity instanceof TileEntityCommandBlock || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner) {
				tileentity.readFromNBT(packetIn.getNbtCompound());
			}
			else {
				NetworkManager nm = null;
				try {
					Field f = Minecraft.class.getDeclaredField(ASMManager.chooseByEnvironment("myNetworkManager", "field_71453_ak"));
					f.setAccessible(true);
					nm = NetworkManager.class.cast(f.get(world));
				}
				catch(Exception e) {}
				tileentity.onDataPacket(nm, packetIn);
			}
		}
	}

	public static int getIndex(Item item, int meta) {
		return Item.getIdFromItem(item) << 16 | meta;
	}

	@Override
	public EntityPlayer getPlayerOnSide(INetHandler handler) {
		if(handler instanceof NetHandlerPlayClient) {
			return Minecraft.getMinecraft().player;
		}
		return null;
	}

	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	public Integer[] createPossibleMetadataCacheFromBlock(Block b) {
		if(cachedMeta.containsKey(b))
			return cachedMeta.get(b);

		Item i = Item.getItemFromBlock(b);
		ArrayList<ItemStack> dummyTabsTrick = new ArrayList<ItemStack>();
		i.getSubItems(i, b.getCreativeTabToDisplayOn(), dummyTabsTrick);
		Integer[] retInt = new Integer[dummyTabsTrick.size()];
		int count = 0;
		for(ItemStack is : dummyTabsTrick) {
			if(is != null && is.getItem() == i) {
				retInt[count] = is.getItemDamage();
				++count;
			}
		}

		cachedMeta.put(b, retInt);
		return retInt;
	}

	public Integer[] createPossibleMetadataCacheFromItem(Item i) {
		if(cachedMetaI.containsKey(i))
			return cachedMetaI.get(i);

		ArrayList<ItemStack> dummyTabsTrick = new ArrayList<ItemStack>();
		i.getSubItems(i, i.getCreativeTab(), dummyTabsTrick);
		Integer[] retInt = new Integer[dummyTabsTrick.size()];
		int count = 0;
		for(ItemStack is : dummyTabsTrick) {
			if(is != null && is.getItem() == i) {
				retInt[count] = is.getItemDamage();
				++count;
			}
		}

		cachedMetaI.put(i, retInt);
		return retInt;
	}

	@Override
	public void registerInfo() {
		AdvancedModelLoader.registerModelHandler(new ObjModelLoader());
		AdvancedModelLoader.registerModelHandler(new TechneModelLoader());
		MainMenuRegistry.initMenuConfigs();
		MainMenuRegistry.registerNewGui(GuiMainMenuVanilla.class,"[DC] Vanilla","Just a simple vanilla MC gui.");
		MainMenuRegistry.registerNewGui(GuiMainMenuOld.class,"[DC] Old Vanilla","An old MC gui.");
		TimerHijack.initMCTimer();

		MinecraftForge.EVENT_BUS.register(new DCParticleEngine());
	}

	@Override
	public void registerInit() {
		MainMenuRegistry.registerMenuConfigs();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		try {
			Class<?> guiClass = Class.forName(GuiContainerLibrary.guis.get(ID));
			Constructor<?> constrctr_gui = guiClass.getConstructor(Container.class, TileEntity.class);
			Class<?> containerClass = Class.forName(GuiContainerLibrary.containers.get(ID));
			Constructor<?> constrctr = containerClass.getConstructor(InventoryPlayer.class, TileEntity.class);
			Object obj = constrctr.newInstance(player.inventory,world.getTileEntity(new BlockPos(x, y, z)));
			return constrctr_gui.newInstance(obj,world.getTileEntity(new BlockPos(x, y, z)));
		}
		catch(Exception e) {
			Notifier.notifySimple("Unable to open GUI for ID "+ID);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void initShaders(ResourceLocation rLoc) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityRenderer er = mc.entityRenderer;
		try {
			if(rLoc == null) {
				if(er.isShaderActive())
					er.switchUseShader();
			}
			else {
				Class<? extends EntityRenderer> erclazz = er.getClass();
				Method loadShader = null;
				for(Method m : erclazz.getDeclaredMethods())
					if(m.getParameterCount() == 1 && m.getParameters()[0].getType() == ResourceLocation.class) {
						loadShader = m;
						break;
					}
				if(loadShader != null)
					loadShader.invoke(er, rLoc);
			}
		}
		catch(Exception e) {
			return;
		}
	}

	@Override
	public void choseDisplayStack(CreativePageBlocks blocks) {
		World w = Minecraft.getMinecraft().world;
		if(Minecraft.getMinecraft().player != null && w.isRemote && Minecraft.getMinecraft().player.ticksExisted % 60 == 0) {
			blocks.delayTime = 0;
			blocks.blockList = blocks.initialiseBlocksList();
			if(blocks.blockList != null && !blocks.blockList.isEmpty()) {
				Random rand;
				if(DummyConfig.shouldChangeImage)
					rand = new Random(Minecraft.getMinecraft().player.ticksExisted);
				else
					rand = new Random(0);
				int random = rand.nextInt(blocks.blockList.size());
				ItemStack itm = blocks.blockList.get(random);
				if(itm != null && itm.getItem() != null)
					blocks.displayStack = itm;
			}
		}
	}

	@Override
	public void choseDisplayStack(CreativePageItems items) {
		World w = Minecraft.getMinecraft().world;
		if(Minecraft.getMinecraft().player != null && w.isRemote && Minecraft.getMinecraft().player.ticksExisted % 60 == 0) {
			items.delayTime = 0;
			items.itemList = items.initialiseItemsList();
			if(items.itemList != null && !items.itemList.isEmpty()) {
				Random rand;
				if(DummyConfig.shouldChangeImage)
					rand = new Random(Minecraft.getMinecraft().player.ticksExisted);
				else
					rand = new Random(0);
				int random = rand.nextInt(items.itemList.size());
				ItemStack itm = items.itemList.get(random);
				if(itm != null && itm.getItem() != null)
					items.displayStack = itm;
			}
		}
	}

	public void registerPostInit() {
		ModelUtils.registerColors();
	}

	public void handleBlockRegister(Block b, ItemBlock ib, String name, Class<?> modClass) {
		if(Core.getBlockTabForMod(modClass) != null) {
			b.setCreativeTab(Core.getBlockTabForMod(modClass));
			BlocksRegistry.blocksList.put(b, Core.getBlockTabForMod(modClass).getTabLabel());
		}

		if(b instanceof IBlockColor)
			ModelUtils.blockColors.add(Pair.<IBlockColor, Block>of((IBlockColor)b, b));
		
		if(ib != null && b instanceof IItemColor)
			ModelUtils.itemColors.add(Pair.<IItemColor, Item>of((IItemColor)b, ib));

		if(ib != null && ib instanceof IItemColor)
			ModelUtils.itemColors.add(Pair.<IItemColor, Item>of((IItemColor)ib, ib));

		if(b instanceof IModelRegisterer)
			((IModelRegisterer)b).registerModels();

		if(ib != null && ib instanceof IModelRegisterer)
			((IModelRegisterer)ib).registerModels();
	}

	public void handleItemRegister(Item i, String name, Class<?> modClass) {
		if(Core.getItemTabForMod(modClass) != null) {
			i.setCreativeTab(Core.getItemTabForMod(modClass));
			ItemRegistry.itemsList.put(i, Core.getItemTabForMod(modClass).getTabLabel());
		}

		if(i instanceof IItemColor)
			ModelUtils.itemColors.add(Pair.<IItemColor, Item>of((IItemColor)i, i));

		if(i instanceof IModelRegisterer)
			((IModelRegisterer)i).registerModels();
	}
}
