package DummyCore.Utils;

import java.util.Hashtable;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * 
 * @author TheLMiffy1111
 * @version From DummyCore 2.3
 * @Description Used to teleport between dimensions.
 */
public class DummyTeleporter extends Teleporter {

	protected WorldServer worldIn;
	protected Random random;
	protected double x,y,z;
	protected DummyPortalGenerator generator;
	protected final Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache = new Long2ObjectOpenHashMap(4096);

	public DummyTeleporter(WorldServer worldIn, DummyPortalGenerator generator, boolean doPutInWorld) {
		super(worldIn);
		this.generator = generator;
		if(doPutInWorld)
			worldIn.customTeleporters.add(this);
		this.worldIn = worldIn;
		this.random = new Random(worldIn.getSeed());
		this.x = worldIn.getSpawnPoint().getX();
		this.y = worldIn.getSpawnPoint().getY();
		this.z = worldIn.getSpawnPoint().getZ();
	}

	public DummyTeleporter(WorldServer worldIn, double x, double y, double z, DummyPortalGenerator generator, boolean doPutInWorld) {
		this(worldIn, generator, doPutInWorld);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw) {
		if(!placeInExistingPortal(entityIn, rotationYaw)) {
			makePortal(entityIn);
			placeInExistingPortal(entityIn, rotationYaw);
		}
	}

	@Override
	public boolean makePortal(Entity entityIn) {
		return generator.makePortal(worldIn, entityIn, x, y, z, random, destinationCoordinateCache);
	}

	@Override
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
		return generator.placeInExistingPortal(worldIn, entityIn, rotationYaw, x, y, z, random, destinationCoordinateCache);
	}

	@Override
	public void removeStalePortalLocations(long worldTime) {
		if(worldTime % 100L == 0L) {
			long i = worldTime - 300L;
			ObjectIterator<Teleporter.PortalPosition> objectiterator = destinationCoordinateCache.values().iterator();

			while(objectiterator.hasNext()) {
				Teleporter.PortalPosition teleporter$portalposition = (Teleporter.PortalPosition)objectiterator.next();

				if(teleporter$portalposition == null || teleporter$portalposition.lastUpdateTime < i) {
					objectiterator.remove();
				}
			}
		}
	}

	public void remove() {
		worldIn.customTeleporters.remove(this);
	}
}
