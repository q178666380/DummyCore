package DummyCore.Utils;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandom;

/**
 * 
 * @author thelmiffy1111
 * @Description
 * Basically the old WeightRandomChestContent class, made this because this is still useful.
 */
public class WeightedRandomChestContent extends WeightedRandom.Item {
	/** The Item/Block to generate in the Chest. */
	public ItemStack theItem;
	/** The minimum chance of item generating. */
	public int theMinimumChanceToGenerateItem;
	/** The maximum chance of item generating. */
	public int theMaximumChanceToGenerateItem;
	
	public WeightedRandomChestContent(Item p_i45311_1_, int p_i45311_2_, int p_i45311_3_, int p_i45311_4_, int p_i45311_5_) {
		super(p_i45311_5_);
		theItem = new ItemStack(p_i45311_1_, 1, p_i45311_2_);
		theMinimumChanceToGenerateItem = p_i45311_3_;
		theMaximumChanceToGenerateItem = p_i45311_4_;
	}
	
	public WeightedRandomChestContent(ItemStack p_i1558_1_, int p_i1558_2_, int p_i1558_3_, int p_i1558_4_) {
		super(p_i1558_4_);
		theItem = p_i1558_1_;
		theMinimumChanceToGenerateItem = p_i1558_2_;
		theMaximumChanceToGenerateItem = p_i1558_3_;
	}
	
	/**
	 * Generates the Chest contents.
	 */
	public static void generateChestContents(Random p_76293_0_, WeightedRandomChestContent[] p_76293_1_, IInventory p_76293_2_, int p_76293_3_) {
		for(int j = 0; j < p_76293_3_; ++j) {
			WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(p_76293_0_, Arrays.asList(p_76293_1_));
			ItemStack[] stacks = weightedrandomchestcontent.generateChestContent(p_76293_0_, p_76293_2_);
			
			for(ItemStack item : stacks) {
				p_76293_2_.setInventorySlotContents(p_76293_0_.nextInt(p_76293_2_.getSizeInventory()), item);
			}
		}
	}
	
	public static void generateDispenserContents(Random p_150706_0_, WeightedRandomChestContent[] p_150706_1_, TileEntityDispenser p_150706_2_, int p_150706_3_) {
		for(int j = 0; j < p_150706_3_; ++j) {
			WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(p_150706_0_, Arrays.asList(p_150706_1_));
			int k = weightedrandomchestcontent.theMinimumChanceToGenerateItem + p_150706_0_.nextInt(weightedrandomchestcontent.theMaximumChanceToGenerateItem - weightedrandomchestcontent.theMinimumChanceToGenerateItem + 1);
            ItemStack[] stacks = weightedrandomchestcontent.generateChestContent(p_150706_0_, p_150706_2_);
            for(ItemStack item : stacks) {
            	p_150706_2_.setInventorySlotContents(p_150706_0_.nextInt(p_150706_2_.getSizeInventory()), item);
            }
		}
	}
	
	public static WeightedRandomChestContent[] merge(WeightedRandomChestContent[] p_92080_0_, WeightedRandomChestContent ... p_92080_1_) {
		WeightedRandomChestContent[] aweightedrandomchestcontent1 = new WeightedRandomChestContent[p_92080_0_.length + p_92080_1_.length];
		int i = 0;
		
		for(int j = 0; j < p_92080_0_.length; ++j) {
			aweightedrandomchestcontent1[i++] = p_92080_0_[j];
		}
		
		WeightedRandomChestContent[] aweightedrandomchestcontent2 = p_92080_1_;
		int k = p_92080_1_.length;
		
		for(int l = 0; l < k; ++l) {
			WeightedRandomChestContent weightedrandomchestcontent1 = aweightedrandomchestcontent2[l];
			aweightedrandomchestcontent1[i++] = weightedrandomchestcontent1;
		}
		
		return aweightedrandomchestcontent1;
	}
	
	/**	
	 * Allow a mod to submit a custom implementation that can delegate item stack generation beyond simple stack lookup
	 *
	 * @param random The current random for generation
	 * @param newInventory The inventory being generated (do not populate it, but you can refer to it)
	 * @return An array of {@link ItemStack} to put into the chest
	 */
	protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
		return generateStacks(random, theItem, theMinimumChanceToGenerateItem, theMaximumChanceToGenerateItem);
	}
	
	/**
	 * Generates an array of items based on the input min/max count.
	 * If the stack can not hold the total amount, it will be split into
	 * stacks of size 1.
	 *
	 * @param rand A random number generator
	 * @param source Source item stack
	 * @param min Minimum number of items
	 * @param max Maximum number of items
	 * @return An array containing the generated item stacks
	 */
	public static ItemStack[] generateStacks(Random rand, ItemStack source, int min, int max) {
		int count = min + (rand.nextInt(max - min + 1));
		
		ItemStack[] ret;
		if(source.getItem() == null) {
			ret = new ItemStack[0];
		}
		else if(count > source.getMaxStackSize()) {
			ret = new ItemStack[count];
			for(int x = 0; x < count; x++) {
				ret[x] = source.copy();
				ret[x].stackSize = 1;
			}
		}
		else {
			ret = new ItemStack[1];
			ret[0] = source.copy();
			ret[0].stackSize = count;
		}
		return ret;
	}
}
