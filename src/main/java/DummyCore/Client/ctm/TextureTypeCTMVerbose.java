package DummyCore.Client.ctm;

import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.TextureType;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.type.TextureTypeCTM;

@TextureType("ctm_verbose")
public class TextureTypeCTMVerbose extends TextureTypeCTM {

	@Override
    public ICTMTexture<? extends TextureTypeCTM> makeTexture(TextureInfo info) {
        return new TextureCTMVerbose(this, info);
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
