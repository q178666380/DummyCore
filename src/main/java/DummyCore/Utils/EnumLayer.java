package DummyCore.Utils;

import net.minecraft.util.IStringSerializable;

public enum EnumLayer implements IStringSerializable {
	BOTTOM("bottom", 0, 0, 0, 0),
	LOWERMIDDLE("lower_middle", 1, 1, -1, -1),
	MIDDLE("middle", 2, -1, 1, -1),
	UPPERMIDDLE("upper_middle", 3, 2, -1, -1),
	TOP("top", 4, 3, 2, 1);
	
	private String name;
	private int index5;
	private int index4;
	private int index3;
	private int index2;
	
	private EnumLayer(String s, int i0, int i1, int i2, int i3) {
		name = s;
		index5 = i0;
		index4 = i1;
		index3 = i2;
		index2 = i3;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public int getIndexFive() {
		return index5;
	}
	
	public int getIndexFour() {
		return index4;
	}
	
	public int getIndexThree() {
		return index3;
	}
	
	public int getIndexTwo() {
		return index2;
	}
	
	public static EnumLayer fromIndexFive(int i) {
		return values()[i%5];
	}
	
	public static EnumLayer fromIndexFour(int i) {
		return LAYERFOUR[i%4];
	}
	
	public static EnumLayer fromIndexThree(int i) {
		return LAYERTHREE[i%3];
	}
	
	public static EnumLayer fromIndexTwo(int i) {
		return LAYERTWO[i%2];
	}
	
	public static final EnumLayer[] LAYERFOUR = new EnumLayer[] {BOTTOM, LOWERMIDDLE, UPPERMIDDLE, TOP};
	public static final EnumLayer[] LAYERTHREE = new EnumLayer[] {BOTTOM, MIDDLE, TOP};
	public static final EnumLayer[] LAYERTWO = new EnumLayer[] {BOTTOM, TOP};
}
