package DummyCore.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

/**
 * 
 * @author Modbder
 *
 * @Description Used in code where you want to reference either ItemStack or its OreDictionary replacement and where you do not want to use Ingredient.
 */
public class UnformedItemStack {

	public static final UnformedItemStack EMPTY;

	public List<ItemStack> possibleStacks = new ArrayList<ItemStack>();
	
	public UnformedItemStack() {}

	public UnformedItemStack(String... lst) {
		for(String s : lst)
			possibleStacks.addAll(Lists.transform(OreDictionary.getOres(s, false), UnformedItemStack::copyAndSetCountToOne));
		sort();
	}

	public UnformedItemStack(ItemStack is) {
		possibleStacks.add(copyAndSetCountToOne(is));
		sort();
	}

	public UnformedItemStack(String oreDictName) {
		possibleStacks.addAll(Lists.transform(OreDictionary.getOres(oreDictName, false), UnformedItemStack::copyAndSetCountToOne));
		sort();
	}

	public UnformedItemStack(List<?> lst) {
		for(Object obj1 : lst) {
			possibleStacks.addAll(getItemStacks(obj1));
		}
		sort();
	}

	public UnformedItemStack(ItemStack... stk) {
		possibleStacks.addAll(Lists.transform(Arrays.asList(stk), UnformedItemStack::copyAndSetCountToOne));
		sort();
	}

	public UnformedItemStack(Block b) {
		possibleStacks.add(new ItemStack(b,1,OreDictionary.WILDCARD_VALUE));
		sort();
	}

	public UnformedItemStack(Item i) {
		possibleStacks.add(new ItemStack(i,1,OreDictionary.WILDCARD_VALUE));
		sort();
	}

	public UnformedItemStack(Object obj) {
		possibleStacks.addAll(getItemStacks(obj));
		sort();
	}

	public UnformedItemStack(Object... lst) {
		for(Object obj1 : lst)
			possibleStacks.addAll(getItemStacks(obj1));
		sort();
	}

	public List<ItemStack> getItemStacks(Object obj) {
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		if(obj instanceof Object[])
			for(Object obj1 : (Object[])obj)
				stacks.addAll(getItemStacks(obj1));
		if(obj instanceof List<?>)
			for(Object obj1 : (List<?>)obj)
				stacks.addAll(getItemStacks(obj1));
		if(obj instanceof ItemStack) {
			ItemStack stk = copyAndSetCountToOne((ItemStack)obj);
			stacks.add(stk);
		}
		if(obj instanceof Block)
			stacks.add(new ItemStack((Block)obj,1,OreDictionary.WILDCARD_VALUE));
		if(obj instanceof Item)
			stacks.add(new ItemStack((Item)obj,1,OreDictionary.WILDCARD_VALUE));
		if(obj instanceof String) {
			stacks.addAll(Lists.transform(OreDictionary.getOres((String)obj, false), UnformedItemStack::copyAndSetCountToOne));
		}
		return stacks;
	}

	public boolean matches(UnformedItemStack uis) {
		return possibleStacks.equals(uis.possibleStacks);
	}

	public boolean isEmpty() {
		if(this == UnformedItemStack.EMPTY) {
			return true;
		}
		return possibleStacks.isEmpty();
	}

	public boolean itemStackMatches(ItemStack is) {
		if(is.isEmpty())
			return false;
		return possibleStacks.stream().anyMatch(s->is.getItem()==s.getItem() && (is.getItemDamage()==OreDictionary.WILDCARD_VALUE || (is.getItemDamage()==s.getItemDamage() && ItemStack.areItemStackShareTagsEqual(s, is))));
	}

	public static ItemStack copyAndSetCountToOne(ItemStack stk) {
		stk = stk.copy();
		stk.setCount(1);
		return stk;
	}

	@Override
	public String toString() {
		String str = "";
		for(ItemStack s : possibleStacks)str += s;
		return str;
	}

	public UnformedItemStack copy() {
		return new UnformedItemStack(this.possibleStacks);
	}

	public void nullify() {
		possibleStacks.clear();
	}

	public void sort() {
		List<ItemStack> possibleStacksCopy = new ArrayList<ItemStack>();
		possibleStacksCopy.addAll(possibleStacks);
		possibleStacks.clear();
		for(int i = 0; i < possibleStacksCopy.size();++i) {
			ItemStack is = possibleStacksCopy.get(i);
			if(is != null && !is.isEmpty() && !possibleStacks.contains(is))
				possibleStacks.add(is);
		}
		possibleStacks.sort((stk0, stk1)->stk0.toString().compareTo(stk1.toString()));
		possibleStacksCopy.clear();
	}

	public ItemStack getISToDraw(long time) {
		int size = this.possibleStacks.size();
		if(size <= 0)return ItemStack.EMPTY;
		return this.possibleStacks.get(((int)(time/30))%size);
	}

	public void writeToNBTTagCompound(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();
		for(ItemStack is : this.possibleStacks)
		{
			NBTTagCompound itemTag = new NBTTagCompound();
			is.writeToNBT(itemTag);
			items.appendTag(itemTag);
		}
		tag.setTag("unformedISList", items);
	}

	public UnformedItemStack(NBTTagCompound tag) {
		NBTTagList items = tag.getTagList("unformedISList", 10);
		for(int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound itemTag = items.getCompoundTagAt(i);
			ItemStack is = new ItemStack(itemTag);
			possibleStacks.add(is);
		}
		sort();
	}

	static {
		EMPTY = new UnformedItemStack();
		EMPTY.possibleStacks = ImmutableList.<ItemStack>of();
	}
}
