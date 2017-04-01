package DummyCore.Utils;

import java.util.Random;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * 
 * @author TheLMiffy1111
 * @version From DummyCore 2.3
 * @Description
 */
public abstract class DummyPortalGenerator {
	
	public abstract boolean placeInExistingPortal(WorldServer worldIn, Entity entityIn, float rotationYaw, double x, double y, double z,
			Random random, Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache);
	
	public abstract boolean makePortal(WorldServer worldIn, Entity entityIn, double x, double y, double z,
			Random random, Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache);
	
	public static final DummyPortalGenerator TELEPORT_ONLY = new DummyPortalGenerator() {

		@Override
		public boolean placeInExistingPortal(WorldServer worldIn, Entity entityIn, float rotationYaw, double x, double y, double z,
				Random random, Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache) {
			worldIn.getBlockState(new BlockPos((int)x, (int)y, (int)z)); //Dummy Load
			entityIn.setLocationAndAngles(x, y, z, entityIn.rotationYaw, 0.0F);
			entityIn.motionX = 0.0D;
			entityIn.motionY = 0.0D;
			entityIn.motionZ = 0.0D;
			return true;
		}

		@Override
		public boolean makePortal(WorldServer worldIn, Entity entityIn, double x, double y, double z,
				Random random, Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache) {
			return true;
		}
	};
}
