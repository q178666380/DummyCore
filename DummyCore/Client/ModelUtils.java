package DummyCore.Client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSortedMap;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Some methods to help with model registering
 * @author TheLMiffy1111
 * 
 */
public class ModelUtils {
	public static final ArrayList<Pair<IBlockColor,Block>> blockColors = new ArrayList<Pair<IBlockColor,Block>>();
	public static final ArrayList<Pair<IItemColor,Item>> itemColors = new ArrayList<Pair<IItemColor,Item>>();
	public static final ArrayList<Pair<ModelResourceLocation,IBakedModel>> MODELS = new ArrayList<Pair<ModelResourceLocation,IBakedModel>>();

	@SideOnly(Side.CLIENT)
	public static void registerColors() {
		for(Pair<IBlockColor,Block> b : blockColors)
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(b.getLeft(), b.getRight());
		for(Pair<IItemColor,Item> i : itemColors)
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(i.getLeft(), i.getRight());
	}
	
	/**
	 * 
	 * @param mrl
	 * @param ibm
	 */
	public static void registerModel(ModelResourceLocation mrl, IBakedModel ibm) {
		MODELS.add(Pair.<ModelResourceLocation,IBakedModel>of(mrl, ibm));
	}
	
	/**
	 * 
	 * @param item
	 */
	public static void setItemModelInventory(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().getResourceDomain()+":item/"+item.getRegistryName(), "inventory"));
	}
	
	/**
	 * 
	 * @param item
	 * @param list
	 */
	public static void setItemModels(Item item, List<Pair<Integer,ModelResourceLocation>> list) {
		for(Pair<Integer,ModelResourceLocation> pair : list) {
			ModelLoader.setCustomModelResourceLocation(item, pair.getLeft().intValue(), pair.getRight());
		}
	}
	
	/**
	 * 
	 * @param item
	 * @param maxMeta
	 * @param func
	 */
	public static void setItemModels(Item item, int maxMeta, IntFunction<ModelResourceLocation> func) {
		for(int i = 0; i < maxMeta; i++) {
			if(func.apply(i) != null)
				ModelLoader.setCustomModelResourceLocation(item, i, func.apply(i));
		}
	}
	
	/**
	 * 
	 * @param block
	 */
	public static void setBlockModelInventory(Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().getResourceDomain()+':'+block.getRegistryName(), "inventory"));
	}
	
	/**
	 * 
	 * @param block
	 * @param list
	 */
	public static void setBlockModels(Block block, List<Pair<Integer,ModelResourceLocation>> list) {
		for(Pair<Integer,ModelResourceLocation> pair : list) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), pair.getLeft().intValue(), pair.getRight());
		}
	}
	
	/**
	 * 
	 * @param block
	 * @param maxMeta
	 * @param func
	 */
	public static void setBlockModels(Block block, int maxMeta, IntFunction<ModelResourceLocation> func) {
		for(int i = 0; i < maxMeta; i++) {
			if(func.apply(i) != null)
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, func.apply(i));
		}
	}
	
	/**
	 * 
	 * @param item
	 * @param args
	 */
	public static void setItemModelSingleIcon(Item item, String... args) {
		MeshDefinitionSingleIcon mesh = new MeshDefinitionSingleIcon(args);
		ModelLoader.setCustomMeshDefinition(item, mesh);
		ModelBakery.registerItemVariants(item, mesh.location);
	}
	
	/**
	 * 
	 * @param item
	 * @param args
	 */
	public static void setItemModelSingleIcon(Item item, ModelResourceLocation args) {
		MeshDefinitionSingleIcon mesh = new MeshDefinitionSingleIcon(args);
		ModelLoader.setCustomMeshDefinition(item, mesh);
		ModelBakery.registerItemVariants(item, mesh.location);
	}
	
	/**
	 * 
	 * @param item
	 * @param args
	 */
	public static void setItemModelNBTActive(Item item, String... args) {
		MeshDefinitionNBTActive mesh = new MeshDefinitionNBTActive(args);
		ModelLoader.setCustomMeshDefinition(item, mesh);
		ModelBakery.registerItemVariants(item, mesh.location);
	}
	
	/**
	 * 
	 * @param item
	 * @param args
	 */
	public static void setItemModelNBTActive(Item item, ResourceLocation args) {
		MeshDefinitionNBTActive mesh = new MeshDefinitionNBTActive(args);
		ModelLoader.setCustomMeshDefinition(item, mesh);
		ModelBakery.registerItemVariants(item, mesh.location);
	}
	
	public static class MeshDefinitionSingleIcon implements ItemMeshDefinition {
		public String name;
		public String variant;
		public ModelResourceLocation location;
		
		public MeshDefinitionSingleIcon(String... args) {
			if(args.length == 1) {
				name = args[0];
				variant = "inventory";
			}
			else if(args.length == 2) {
				name = args[0];
				variant = args[1];
			}
			else if(args.length == 3) {
				name = args[0] + ':' + args[1];
				variant = args[2];
			}
			else {
				throw new IllegalArgumentException("No arguments or more than 3 arguments!");
			}
			location = new ModelResourceLocation(name, variant);
		}
		
		public MeshDefinitionSingleIcon(ModelResourceLocation mrl) {
			location = mrl;
			name = mrl.getResourceDomain()+':'+mrl.getResourcePath();
			variant = mrl.getVariant();
		}
		
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return location;
		}
	}
	
	public static class MeshDefinitionNBTActive implements ItemMeshDefinition {
		public String name;
		public ModelResourceLocation[] location = new ModelResourceLocation[2];
		
		public MeshDefinitionNBTActive(String... args) {
			if(args.length == 1) {
				name = args[0];
			}
			else if(args.length == 2) {
				name = args[0] + ':' + args[1];
			}
			else {
				throw new IllegalArgumentException("No arguments or more than 2 arguments!");
			}
			location[0] = new ModelResourceLocation(name, "active=false");
			location[1] = new ModelResourceLocation(name, "active=true");
		}
		
		public MeshDefinitionNBTActive(ResourceLocation rl) {
			location[0] = new ModelResourceLocation(rl, "active=false");
			location[1] = new ModelResourceLocation(rl, "active=true");
			name = rl.toString();
		}
		
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("active") ? location[1] : location[0];
		}
	}
}
