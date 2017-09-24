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

//Based on TextureEdgesFull
public class TextureCTMFull extends TextureCTM<TextureTypeCTMFull> {

	public TextureCTMFull(TextureTypeCTMFull type, TextureInfo info) {
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
		if(!top && !right && !bottom && !left) {
			sprite = sprites[0];
			submap = Submap.X1;
		}
		else if(top && right && bottom && left) {
			submap = Submap.X4[2][2];
		}
		else if(!top && right && bottom && left) {
			submap = Submap.X4[1][2];
		}
		else if(top && !right && bottom && left) {
			submap = Submap.X4[2][3];
		}
		else if(top && right && !bottom && left) {
			submap = Submap.X4[3][2];
		}
		else if(top && right && bottom && !left) {
			submap = Submap.X4[2][1];
		}
		else if(!top && !right && bottom && left) {
			submap = Submap.X4[1][3];
		}
		else if(top && !right && !bottom && left) {
			submap = Submap.X4[3][3];
		}
		else if(top && right && !bottom && !left) {
			submap = Submap.X4[3][1];
		}
		else if(!top && right && bottom && !left) {
			submap = Submap.X4[1][1];
		}
		else if(!top && right && !bottom && left) {
			submap = Submap.X4[0][2];
		}
		else if(top && !right && bottom && !left) {
			submap = Submap.X4[2][0];
		}
		else if(top && !right && !bottom && !left) {
			submap = Submap.X4[3][0];
		}
		else if(!top && right && !bottom && !left) {
			submap = Submap.X4[0][1];
		}
		else if(!top && !right && bottom && !left) {
			submap = Submap.X4[1][0];
		}
		else if(!top && !right && !bottom && left) {
			submap = Submap.X4[0][3];
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
}
