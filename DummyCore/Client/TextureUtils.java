package DummyCore.Client;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TextureUtils {
	public static final ArrayList<ResourceLocation> TEXTURES = Lists.<ResourceLocation>newArrayList();
	
	public static void register(ResourceLocation rl) {
		TEXTURES.add(rl);
	}
	
	public static TextureAtlasSprite fromBlock(IBlockState state) {
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	}
	
	public static TextureAtlasSprite fromBlock(Block b, int meta) {
		return fromBlock(b.getStateFromMeta(meta));
	}
	
	public static TextureAtlasSprite fromBlock(Block b) {
		return fromBlock(b.getDefaultState());
	}
	
	public static TextureAtlasSprite fromBlock(IBlockAccess w, int x, int y, int z) {
		return fromBlock(w.getBlockState(new BlockPos(x,y,z)));
	}
	
	public static TextureAtlasSprite fromItem(ItemStack stk) {
		return fromItem(stk.getItem(), stk.getItemDamage());
	}
	
	public static TextureAtlasSprite fromItem(Item itm, int meta) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(itm, meta);
	}
	
	public static TextureAtlasSprite fromItem(Item itm) {
		return fromItem(itm, 0);
	}
}
