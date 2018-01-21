package DummyCore.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public abstract class ContainerGeneric extends Container {

	public final InventoryPlayer pInv;
	public final EntityPlayer player;

	public int pInvOffsetX;
	public int pInvOffsetZ;

	public ContainerGeneric(InventoryPlayer playerInv) {
		super();
		pInv = playerInv;
		player = playerInv.player;
		setupOffset();
	}

	public ContainerGeneric(EntityPlayer player) {
		this(player.inventory);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public void setupOffset() {
		setupPlayerInventory();
	}
	
	public void setupPlayerInventory() {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(pInv, j + i * 9 + 9, 8 + j * 18 + pInvOffsetX, 84 + i * 18 + pInvOffsetZ));

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(pInv, i, 8 + i * 18 + pInvOffsetX, 142 + pInvOffsetZ));
	}
}
