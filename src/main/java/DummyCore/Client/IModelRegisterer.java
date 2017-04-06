package DummyCore.Client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelRegisterer {
	
	@SideOnly(Side.CLIENT)
	public void registerModels();
}
