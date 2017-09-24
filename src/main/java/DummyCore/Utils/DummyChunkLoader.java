package DummyCore.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DummyCore.Core.CoreInitialiser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * 
 * @author Yun-Chian
 * Based on Mekanism's chunk loading
 */
public class DummyChunkLoader {

	public Set<ChunkPos> chunkSet = new HashSet<ChunkPos>();

	public TileEntity tileEntity;

	public Ticket chunkTicket;

	public Coord4D prevCoord;

	public DummyChunkLoader(TileEntity tile) {
		if(!(tile instanceof IChunkLoader)) {
			throw new IllegalArgumentException("The TileEntity passed in should implement IChunkLoader");
		}
		tileEntity = tile;
	}

	public void setTicket(Ticket t) {
		if(chunkTicket != t && chunkTicket != null && chunkTicket.world == tileEntity.getWorld()) {
			for(ChunkPos chunk : chunkTicket.getChunkList()) {
				if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk)) {
					ForgeChunkManager.unforceChunk(chunkTicket, chunk);
				}
			}

			ForgeChunkManager.releaseTicket(chunkTicket);
		}

		chunkTicket = t;
	}

	public void release() {
		setTicket(null);
	}

	public void sortChunks() {
		if(chunkTicket != null) {
			for(ChunkPos chunk : chunkTicket.getChunkList()) {
				if(!chunkSet.contains(chunk)) {
					if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk)) {
						ForgeChunkManager.unforceChunk(chunkTicket, chunk);
					}
				}
			}

			for(ChunkPos chunk : chunkSet) {
				if(!chunkTicket.getChunkList().contains(chunk)) {
					ForgeChunkManager.forceChunk(chunkTicket, chunk);
				}
			}
		}
	}

	public void refreshChunkSet() {
		IChunkLoader loader = (IChunkLoader)tileEntity;

		if(!chunkSet.equals(loader.getChunks())) {
			chunkSet = loader.getChunks();
			sortChunks();
		}
	}

	public void forceChunks(Ticket ticket) {
		setTicket(ticket);

		for(ChunkPos chunk : chunkSet) {
			ForgeChunkManager.forceChunk(chunkTicket, chunk);
		}
	}

	/**
	 * Call this in update()
	 */
	public void tick() {
		if(!tileEntity.getWorld().isRemote) {			
			if(prevCoord == null || !prevCoord.equals(new Coord4D(tileEntity))) {
				release();
				prevCoord = new Coord4D(tileEntity);
			}

			if(chunkTicket != null && (!canOperate() || chunkTicket.world != tileEntity.getWorld())) {
				release();
			}

			refreshChunkSet();

			if(canOperate() && chunkTicket == null) {
				Ticket ticket = ForgeChunkManager.requestTicket(CoreInitialiser.instance, tileEntity.getWorld(), Type.NORMAL);

				if(ticket != null)  {
					ticket.getModData().setInteger("x", tileEntity.getPos().getX());
					ticket.getModData().setInteger("y", tileEntity.getPos().getY());
					ticket.getModData().setInteger("z", tileEntity.getPos().getZ());

					forceChunks(ticket);
				}
			}
		}
	}

	public boolean canOperate() {
		return ((IChunkLoader)tileEntity).canOperate();
	}

	/**
	 * Call this in readFromNBT()
	 */
	public void read(NBTTagCompound nbtTags) {
		prevCoord = Coord4D.fromString(nbtTags.getString("prevCoord"));

		chunkSet.clear();
		NBTTagList list = nbtTags.getTagList("chunkSet", NBT.TAG_COMPOUND);

		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			chunkSet.add(new ChunkPos(compound.getInteger("chunkX"), compound.getInteger("chunkZ")));
		}
	}

	/**
	 * Call this in writeToNBT()
	 */
	public void write(NBTTagCompound nbtTags) {
		if(prevCoord != null) {
			nbtTags.setString("prevCoord", prevCoord.toString());
		}

		NBTTagList list = new NBTTagList();

		for(ChunkPos pos : chunkSet) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("chunkX", pos.x);
			compound.setInteger("chunkZ", pos.z);
			list.appendTag(compound);
		}

		nbtTags.setTag("chunkSet", list);
	}

	/**
	 * Call this in invalidate()
	 */
	public void invalidate()  {
		if(!tileEntity.getWorld().isRemote) {
			release();
		}
	}

	public static class TicketHandler implements LoadingCallback {
		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world) {
			for(Ticket ticket : tickets) {
				int x = ticket.getModData().getInteger("xCoord");
				int y = ticket.getModData().getInteger("yCoord");
				int z = ticket.getModData().getInteger("zCoord");

				TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

				if(tileEntity instanceof IChunkLoader) {
					((IChunkLoader)tileEntity).getChunkLoader().refreshChunkSet();
					((IChunkLoader)tileEntity).getChunkLoader().forceChunks(ticket);
				}
			}
		}
	}

	public static interface IChunkLoader {
		Set<ChunkPos> getChunks();
		DummyChunkLoader getChunkLoader();
		boolean canOperate();
	}
}
