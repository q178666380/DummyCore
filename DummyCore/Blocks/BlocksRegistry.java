package DummyCore.Blocks;

import java.util.Hashtable;

import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	 * @param blockClass - used, if you want to register a block, that has an ItemBlock. Can be null if no ItemBlock is required.
	 */
	public static void registerBlock(Block b, String name, Class<?> modClass, Class<? extends ItemBlock> blockClass)
	{
		GameRegistry.register(b.setRegistryName(Core.getModFromClass(modClass).modid, name));
		ItemBlock ib = null;
		if(blockClass != null)
		{	
			try
			{
				blockClass.getConstructor(Block.class).setAccessible(true);
				ib = blockClass.getConstructor(Block.class).newInstance(b);
				GameRegistry.register(ib.setRegistryName(Core.getModFromClass(modClass).modid, name));
			}
			catch(Exception e) {}
		}
		CoreInitialiser.proxy.handleBlockRegister(b, ib, name, modClass);
	}
	
	/**
	 * Use this to register new simple blocks.
	 * @version From DummyCore 2.3
	 * @param b - the block to be registered, in the form of an ItemBlock.
	 * @param name - in-game name of the block. Will be written to the corresponding .lang file
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 */
	public static void registerBlock(ItemBlock ib, String name, Class<?> modClass)
	{
		Block b = ib.block;
		GameRegistry.register(b.setRegistryName(Core.getModFromClass(modClass).modid, name));
		GameRegistry.register(ib.setRegistryName(Core.getModFromClass(modClass).modid, name));
		

		CoreInitialiser.proxy.handleBlockRegister(b, ib, name, modClass);
	}
}
