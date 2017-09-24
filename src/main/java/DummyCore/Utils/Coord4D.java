package DummyCore.Utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * @author TheLMiffy1111
 */
public class Coord4D {

	public int x;
	public int y;
	public int z;
	public int dim;

	/**
	 * Creates a 4d coordinate from 4 given vars
	 * @param x - x
	 * @param y - y
	 * @param z - z
	 * @param dim - dim
	 */
	public Coord4D(int x, int y, int z, int dim) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
	}

	public Coord4D(BlockPos pos, int dim) {
		this(pos.getX(), pos.getY(), pos.getZ(), dim);
	}

	public Coord4D(BlockPos pos, World w) {
		this(pos, w.provider.getDimension());
	}
	
	public Coord4D(TileEntity te) {
		this(te.getPos(), te.getWorld());
	}

	/**
	 * Not only transforms the coord to a string,  but also does it in a way that it can be later parsed by DummyData
	 */
	@Override
	public String toString() {
		return "||x:"+x+"||y:"+y+"||z:"+z+"||dim:"+dim;
	}

	/**
	 * Creates a Coord4D object from a valid DummyData string
	 * @param data - the valid DummyData string
	 * @return a newly created object
	 */
	public static Coord4D fromString(String data) {
		DummyData[] dt = DataStorage.parseData(data);
		int x = Integer.parseInt(dt[0].fieldValue);
		int y = Integer.parseInt(dt[1].fieldValue);
		int z = Integer.parseInt(dt[2].fieldValue);
		int dim = Integer.parseInt(dt[3].fieldValue);
		return new Coord4D(x,y,z,dim);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Coord4D ? x == ((Coord4D)obj).x && y == ((Coord4D)obj).y && z == ((Coord4D)obj).z && dim == ((Coord4D)obj).dim : super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(x) + Integer.hashCode(y)^3 + Integer.hashCode(z)^9 + Integer.hashCode(dim)^12;
	}
}
