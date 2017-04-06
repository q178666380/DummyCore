package DummyCore.Utils;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * A simple wrapper for custom step sounds for blocks
 * @author modbder
 * 
 */
public class CustomStepSound extends SoundType {

	public CustomStepSound(String name, float volume, float frequency) {
		super(volume, frequency,
				GameRegistry.register(new SoundEvent(new ResourceLocation(name+".break")).setRegistryName(name+".break")),
				GameRegistry.register(new SoundEvent(new ResourceLocation(name+".step")).setRegistryName(name+".step")),
				GameRegistry.register(new SoundEvent(new ResourceLocation(name+".place")).setRegistryName(name+".place")),
				GameRegistry.register(new SoundEvent(new ResourceLocation(name+".hit")).setRegistryName(name+".hit")),
				GameRegistry.register(new SoundEvent(new ResourceLocation(name+".fall")).setRegistryName(name+".fall")));
	}

}
