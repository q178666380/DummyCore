package DummyCore.Blocks;

import java.util.Hashtable;

import DummyCore.Core.Core;
import DummyCore.Utils.IOldCubicBlock;
import DummyCore.Utils.OldTextureHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description Use this class to register new blocks in your game.
 *  This will automatically add your blocks to the corresponding creative tab
 */
public class BlocksRegistry {
	
	/**
	 * Used to check the creative tab block belongs to
	 */
	public static Hashtable<Block,String> blocksList = new Hashtable<Block, String>();
	
	/**
	 * Use this to register new simple blocks.
	 * @version From DummyCore 1.0
	 * @param b - the block to be registered.
	 * @param name - in-game name of the block. Will be written to the corresponding .lang file
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 * @param blockClass - used, if you want to register a block, that has an ItemBlock. Can be null.
	 */
	public static void registerBlock(Block b, String name, Class<?> modClass, Class<? extends ItemBlock> blockClass)
	{
		if(blockClass == null)
		{
			GameRegistry.registerBlock(b, name);
		}else
		{
			GameRegistry.registerBlock(b, blockClass, name);
		}
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			if(Core.getBlockTabForMod(modClass) != null)
			{
				b.setCreativeTab(Core.getBlockTabForMod(modClass));
				blocksList.put(b, Core.getBlockTabForMod(modClass).getTabLabel());
			}
			
			if(b instanceof IOldCubicBlock)
				OldTextureHandler.addOldBlock(Core.getModFromClass(modClass).modid+":"+name, b);
		}
	}

}
