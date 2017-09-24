package DummyCore.Client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IItemColor {
	public int getColorFromItemstack(ItemStack stack, int layer);

	@SideOnly(Side.CLIENT)
	default public net.minecraft.client.renderer.color.IItemColor toMCItemColor() {
		IItemColor itf = this;
		return new net.minecraft.client.renderer.color.IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int layer) {
				return itf.getColorFromItemstack(stack, layer);
			}
		};
	}
}
