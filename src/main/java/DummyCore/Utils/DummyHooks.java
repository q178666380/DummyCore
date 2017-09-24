package DummyCore.Utils;

import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class DummyHooks {
	public static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
		if(!stack.isEmpty()) {
			Set<IItemOverlayElement> elements = MiscUtils.itemOverlayElements.get(stack.getItem());
			for(IItemOverlayElement element : elements) {
				element.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
			}
		}
	}
}
