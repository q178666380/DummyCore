package DummyCore.Client.ctm;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import team.chisel.ctm.api.texture.ISubmap;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.ctx.TextureContextCTM;
import team.chisel.ctm.client.texture.render.TextureCTM;
import team.chisel.ctm.client.util.CTMLogic;
import team.chisel.ctm.client.util.Dir;
import team.chisel.ctm.client.util.Quad;
import team.chisel.ctm.client.util.Submap;

public class TextureCTMVerbose extends TextureCTM<TextureTypeCTMVerbose> {

	public TextureCTMVerbose(TextureTypeCTMVerbose type, TextureInfo info) {
		super(type, info);
	}

	@Override
	public List<BakedQuad> transformQuad(BakedQuad bq, ITextureContext context, int quadGoal) {
		Quad quad = makeQuad(bq, context);
		if(context == null) {
			return Collections.singletonList(quad.transformUVs(sprites[0]).rebake());
		}

		CTMLogic ctm = ((TextureContextCTM)context).getCTM(bq.getFace());
		TextureAtlasSprite sprite = sprites[1];
		ISubmap submap = null;
		boolean top     = ctm.connected(Dir.TOP);
		boolean right   = ctm.connected(Dir.RIGHT);
		boolean bottom  = ctm.connected(Dir.BOTTOM);
		boolean left    = ctm.connected(Dir.LEFT);
		int index =
				(ctm.connected(Dir.TOP_RIGHT)    ? 1 : 0) +
				(ctm.connected(Dir.BOTTOM_RIGHT) ? 2 : 0) +
				(ctm.connected(Dir.BOTTOM_LEFT)  ? 4 : 0) +
				(ctm.connected(Dir.TOP_LEFT)     ? 8 : 0)
				;
		int index0 = (index/4)*4;
		int index1 = (index%4)*4;
		if(!top && !right && !bottom && !left) {
			sprite = sprites[0];
			submap = Submap.X1;
		}
		else if(top && right && bottom && left) {
			submap = X16[index0+2][index1+2];
		}
		else if(!top && right && bottom && left) {
			submap = X16[index0+1][index1+2];
		}
		else if(top && !right && bottom && left) {
			submap = X16[index0+2][index1+3];
		}
		else if(top && right && !bottom && left) {
			submap = X16[index0+3][index1+2];
		}
		else if(top && right && bottom && !left) {
			submap = X16[index0+2][index1+1];
		}
		else if(!top && !right && bottom && left) {
			submap = X16[index0+1][index1+3];
		}
		else if(top && !right && !bottom && left) {
			submap = X16[index0+3][index1+3];
		}
		else if(top && right && !bottom && !left) {
			submap = X16[index0+3][index1+1];
		}
		else if(!top && right && bottom && !left) {
			submap = X16[index0+1][index1+1];
		}
		else if(!top && right && !bottom && left) {
			submap = X16[index0+0][index1+2];
		}
		else if(top && !right && bottom && !left) {
			submap = X16[index0+2][index1+0];
		}
		else if(top && !right && !bottom && !left) {
			submap = X16[index0+3][index1+0];
		}
		else if(!top && right && !bottom && !left) {
			submap = X16[index0+0][index1+1];
		}
		else if(!top && !right && bottom && !left) {
			submap = X16[index0+1][index1+0];
		}
		else if(!top && !right && !bottom && left) {
			submap = X16[index0+0][index1+3];
		}
		if (submap == null) {
			submap = Submap.X1;
		}

		quad = quad.transformUVs(sprite, submap);

		if(quadGoal == 1) {
			return Collections.<BakedQuad>singletonList(quad.rebake());
		}

		return Lists.newArrayList(quad.subdivide(quadGoal)).stream()
				.filter(Objects::nonNull)
				.map(Quad::rebake)
				.collect(Collectors.toList());
	}

	public static final ISubmap[][] X16 = new ISubmap[16][16];

	static {
		for(int i = 0; i < 16; ++i) {
			for(int j = 0; j < 16; ++j) {
				X16[i][j] = new Submap(1, 1, i, j);
			}
		}
	}
}
