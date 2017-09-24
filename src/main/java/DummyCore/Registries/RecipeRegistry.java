package DummyCore.Registries;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeRegistry {

	public static void addShapedOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output, input);
		ShapedOreRecipe recipe = new ShapedOreRecipe(output.getItem().getRegistryName(), output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static void addShapelessOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output, input);
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(output.getItem().getRegistryName(), output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static ResourceLocation getNameForRecipe(ItemStack output, Object... input) {
		ModContainer activeContainer = Loader.instance().activeModContainer();
		ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getResourcePath()+"_"+Integer.toUnsignedString(Arrays.deepToString(input).hashCode(), 32));
		return recipeLoc;
	}
}
