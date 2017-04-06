package DummyCore.Utils;

import java.util.Hashtable;
import java.util.UUID;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author TheLMiffy1111
 * @version From DummyCore 2.3
 * @Description
 */
public class DummyPortalHandler {

	public static final Hashtable<Block, PortalBlockData> BLOCK_DATA = new Hashtable<Block, PortalBlockData>();
	public static final Hashtable<UUID, PortalPlayerData> PLAYER_DATA = new Hashtable<UUID, PortalPlayerData>();
	public static final Int2ObjectMap<DummyTeleporter> TO_TELEPORTERS = new Int2ObjectOpenHashMap<DummyTeleporter>();
	public static final Int2ObjectMap<DummyTeleporter> RETURN_TELEPORTERS = new Int2ObjectOpenHashMap<DummyTeleporter>();

	public static void registerPortal(Block block, int toDim, int retDim, int tpTicks, boolean checkBelow, DummyPortalGenerator generator, ResourceLocation overlayLocation, int color) {
		BLOCK_DATA.put(block, new PortalBlockData(toDim, retDim, tpTicks, checkBelow, generator, overlayLocation, color));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		for(PortalBlockData data : BLOCK_DATA.values()) {
			data.overlayTexture = event.getMap().registerSprite(data.overlayLocation);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == Phase.END)
			return;

		if(!PLAYER_DATA.containsKey(MiscUtils.getUUIDFromPlayer(event.player))) {
			PLAYER_DATA.put(MiscUtils.getUUIDFromPlayer(event.player), new PortalPlayerData());
		}

		PortalPlayerData playerData = PLAYER_DATA.get(MiscUtils.getUUIDFromPlayer(event.player));
		BlockPos pos = new BlockPos(event.player.posX,event.player.posY,event.player.posZ);
		Block block = event.player.getEntityWorld().getBlockState(pos).getBlock();
		BlockPos posDown = new BlockPos(event.player.posX,event.player.posY-1,event.player.posZ);
		Block blockDown = event.player.getEntityWorld().getBlockState(posDown).getBlock();

		if(BLOCK_DATA.containsKey(blockDown)) {
			PortalBlockData blockData = BLOCK_DATA.get(blockDown);

			if(blockData.checkBelow) {
				playerData.portalUsing = blockData;
				int toDim = event.player.dimension == blockData.toDim ? blockData.retDim : blockData.toDim;

				if((playerData.portalTicks == blockData.tpTicks || event.player.capabilities.isCreativeMode) && !playerData.hasFinished) {
					transferEntityToDimension(blockData, event.player, toDim, true);
					playerData.hasFinished = true;
				}

				playerData.portalTicks++;
				return;
			}
		}
		if(BLOCK_DATA.containsKey(block)) {
			PortalBlockData blockData = BLOCK_DATA.get(block);
			playerData.portalUsing = blockData;
			int toDim = event.player.dimension == blockData.toDim ? blockData.retDim : blockData.toDim;

			if((playerData.portalTicks == blockData.tpTicks || event.player.capabilities.isCreativeMode) && !playerData.hasFinished) {
				transferEntityToDimension(blockData, event.player, toDim, true);
				playerData.hasFinished = true;
			}

			playerData.portalTicks++;
			return;
		}
		playerData.portalUsing = null;
		playerData.portalTicks = 0;
		playerData.hasFinished = false;
	}

	public static void transferEntityToDimension(Entity entityIn) {
		if(entityIn instanceof EntityPlayer)
			return;

		BlockPos pos = new BlockPos(entityIn.posX,entityIn.posY,entityIn.posZ);
		Block block = entityIn.getEntityWorld().getBlockState(pos).getBlock();
		BlockPos posDown = new BlockPos(entityIn.posX,entityIn.posY-1,entityIn.posZ);
		Block blockDown = entityIn.getEntityWorld().getBlockState(posDown).getBlock();

		if(BLOCK_DATA.containsKey(blockDown)) {
			PortalBlockData blockData = BLOCK_DATA.get(blockDown);
			if(blockData.checkBelow) {
				if(entityIn.timeUntilPortal == 0) {
					entityIn.timeUntilPortal = entityIn.getPortalCooldown();
					int toDim = entityIn.dimension == blockData.toDim ? blockData.retDim : blockData.toDim;
					transferEntityToDimension(blockData, entityIn, toDim, false);
					return;
				}
			}
		}
		if(BLOCK_DATA.containsKey(block)) {
			if(entityIn.timeUntilPortal == 0) {
				entityIn.timeUntilPortal = entityIn.getPortalCooldown();
				PortalBlockData blockData = BLOCK_DATA.get(block);
				int toDim = entityIn.dimension == blockData.toDim ? blockData.retDim : blockData.toDim;
				transferEntityToDimension(blockData, entityIn, toDim, false);
			}
		}
	}

	private static void transferEntityToDimension(PortalBlockData blockData, Entity entityIn, int toDim, boolean isPlayer) {
		if(entityIn.getEntityWorld().isRemote)
			return;

		MinecraftServer mcServer = entityIn.getServer();

		if(!TO_TELEPORTERS.containsKey(blockData.toDim)) {
			WorldServer server = mcServer.worldServerForDimension(blockData.toDim);
			TO_TELEPORTERS.put(blockData.toDim, new DummyTeleporter(server, blockData.generator, true));
		}

		if(!RETURN_TELEPORTERS.containsKey(blockData.toDim)) {
			WorldServer server = mcServer.worldServerForDimension(blockData.retDim);
			RETURN_TELEPORTERS.put(blockData.toDim, new DummyTeleporter(server, blockData.generator, true));
		}

		PlayerList list = entityIn.getServer().getPlayerList();
		DummyTeleporter teleporter = toDim == blockData.toDim ? TO_TELEPORTERS.get(blockData.toDim) : RETURN_TELEPORTERS.get(blockData.toDim);

		if(isPlayer) {
			transferPlayerToDimension((EntityPlayerMP)entityIn, toDim, teleporter);
		}
		else {
			transferEntityToWorld(entityIn, entityIn.dimension, mcServer.worldServerForDimension(entityIn.dimension), mcServer.worldServerForDimension(toDim), teleporter);
		}
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, int dimensionIn, Teleporter teleporter) {
		int i = player.dimension;
		MinecraftServer mcServer = player.mcServer;
		PlayerList list = mcServer.getPlayerList();
		WorldServer worldserver = mcServer.worldServerForDimension(player.dimension);
		player.dimension = dimensionIn;
		WorldServer worldserver1 = mcServer.worldServerForDimension(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, worldserver1.getDifficulty(), worldserver1.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		list.updatePermissionLevel(player);
		worldserver.removeEntityDangerously(player);
		player.isDead = false;
		transferEntityToWorld(player, i, worldserver, worldserver1, teleporter);
		list.preparePlayer(player, worldserver);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(worldserver1);
		player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		list.updateTimeAndWeatherForPlayer(player, worldserver1);
		list.syncPlayerInventory(player);
		for(PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, i, dimensionIn);
	}

	public static void transferEntityToWorld(Entity entityIn, int lastDimension, WorldServer oldWorldIn, WorldServer toWorldIn, Teleporter teleporter) {
		WorldProvider pOld = oldWorldIn.provider;
		WorldProvider pNew = toWorldIn.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double d0 = entityIn.posX * moveFactor;
		double d1 = entityIn.posZ * moveFactor;
		float f = entityIn.rotationYaw;
		oldWorldIn.theProfiler.startSection("placing");
		d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
		d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
		if(entityIn.isEntityAlive()) {
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
			teleporter.placeInPortal(entityIn, f);
			toWorldIn.spawnEntity(entityIn);
			toWorldIn.updateEntityWithOptionalForce(entityIn, false);
		}
		oldWorldIn.theProfiler.endSection();
		entityIn.setWorld(toWorldIn);
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGameOverlayEvent.Post event) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			return;

		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			int k = event.getResolution().getScaledWidth();
			int l = event.getResolution().getScaledHeight();

			if(PLAYER_DATA.containsKey(MiscUtils.getUUIDFromPlayer(player))) {
				PortalPlayerData playerData = PLAYER_DATA.get(MiscUtils.getUUIDFromPlayer(player));
				if(playerData.portalUsing != null) {
					PortalBlockData blockData = playerData.portalUsing;
					TextureAtlasSprite iicon = blockData.overlayTexture;
					int color = blockData.color;
					GlStateManager.disableAlpha();
					GlStateManager.disableDepth();
					GlStateManager.depthMask(false);
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					GlStateManager.color((color&0xFF0000>>16)/255F, (color&0x00FF00>>8)/255F, (color&0x0000FF)/255F, blockData.tpTicks != 0 ? (float)playerData.portalTicks/(float)blockData.tpTicks : 1F);
					Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					float f1 = iicon.getMinU();
					float f2 = iicon.getMinV();
					float f3 = iicon.getMaxU();
					float f4 = iicon.getMaxV();
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexbuffer = tessellator.getBuffer();
					vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
					vertexbuffer.pos(0.0D, (double)l, -90.0D).tex((double)f1, (double)f4).endVertex();
					vertexbuffer.pos((double)k, (double)l, -90.0D).tex((double)f3, (double)f4).endVertex();
					vertexbuffer.pos((double)k, 0.0D, -90.0D).tex((double)f3, (double)f2).endVertex();
					vertexbuffer.pos(0.0D, 0.0D, -90.0D).tex((double)f1, (double)f2).endVertex();
					tessellator.draw();
					GlStateManager.depthMask(true);
					GlStateManager.enableDepth();
					GlStateManager.enableAlpha();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	public static class PortalBlockData {
		public final int toDim,retDim,tpTicks,color;
		public final boolean checkBelow;
		public final DummyPortalGenerator generator;
		public final ResourceLocation overlayLocation;
		public TextureAtlasSprite overlayTexture;

		public PortalBlockData(int toDim, int retDim, int tpTicks, boolean checkBelow, DummyPortalGenerator generator, ResourceLocation overlayLocation, int color) {
			this.toDim = toDim;
			this.retDim = retDim;
			this.tpTicks = tpTicks;
			this.checkBelow = checkBelow;
			this.generator = generator;
			this.overlayLocation = overlayLocation;
			this.color = color;
		}
	}

	public static class PortalPlayerData {
		public int portalTicks;
		public PortalBlockData portalUsing;
		public boolean hasFinished;
	}
}
