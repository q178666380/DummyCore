package DummyCore.Client.ctm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.api.texture.ITextureType;
import team.chisel.ctm.api.texture.TextureType;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.ctx.TextureContextCTM;
import team.chisel.ctm.client.texture.render.TextureEdgesFull;
import team.chisel.ctm.client.texture.type.TextureTypeCTM;

@TextureType("ctm_full")
public class TextureTypeCTMFull extends TextureTypeCTM {

	@Override
    public ICTMTexture<? extends TextureTypeCTM> makeTexture(TextureInfo info) {
        return new TextureCTMFull(this, info);
    }

    @Override
    public int requiredTextures() {
        return 2;
    }

    @Override
    public int getQuadsPerSide() {
        return 1;
    }
}
