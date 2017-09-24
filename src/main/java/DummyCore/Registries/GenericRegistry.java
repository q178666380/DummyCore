package DummyCore.Registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class GenericRegistry {

	public static <T extends IForgeRegistryEntry> T register(T entry) {
		GameRegistry.findRegistry(entry.getClass()).register(entry);
		return entry;
	}

	public static <T extends IForgeRegistryEntry> T register(T entry, String name) {
		GameRegistry.findRegistry(entry.getClass()).register((T)entry.setRegistryName(new ResourceLocation(name)));
		return entry;
	}
}
