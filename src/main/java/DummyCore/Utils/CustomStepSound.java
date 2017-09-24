package DummyCore.Utils;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.GameData;

/**
 * A simple wrapper for custom step sounds for blocks
 * @author modbder
 * 
 */
public class CustomStepSound extends SoundType {

	public CustomStepSound(String name, float volume, float frequency) {
		super(volume, frequency,
				GameData.register_impl(new SoundEvent(new ResourceLocation(name+".break")).setRegistryName(name+".break")),
				GameData.register_impl(new SoundEvent(new ResourceLocation(name+".step")).setRegistryName(name+".step")),
				GameData.register_impl(new SoundEvent(new ResourceLocation(name+".place")).setRegistryName(name+".place")),
				GameData.register_impl(new SoundEvent(new ResourceLocation(name+".hit")).setRegistryName(name+".hit")),
				GameData.register_impl(new SoundEvent(new ResourceLocation(name+".fall")).setRegistryName(name+".fall")));
	}

}
