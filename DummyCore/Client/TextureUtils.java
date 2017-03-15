package DummyCore.Client;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;

public class TextureUtils {
	public static final ArrayList<ResourceLocation> TEXTURES = Lists.<ResourceLocation>newArrayList();
	
	public static void register(ResourceLocation rl) {
		TEXTURES.add(rl);
	}
}
