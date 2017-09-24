package DummyCore.Client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockColor {
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex);
	
	@SideOnly(Side.CLIENT)
	default public net.minecraft.client.renderer.color.IBlockColor toMCBlockColor() {
		IBlockColor itf = this;
		return new net.minecraft.client.renderer.color.IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex) {
				return itf.colorMultiplier(state, world, pos, tintIndex);
			}
		};
	}
}
