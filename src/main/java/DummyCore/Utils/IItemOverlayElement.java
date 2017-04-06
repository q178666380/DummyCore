package DummyCore.Utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public interface IItemOverlayElement {
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text);
}
