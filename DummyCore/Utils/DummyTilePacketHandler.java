package DummyCore.Utils;

import DummyCore.Core.CoreInitialiser;
import io.netty.channel.ChannelHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

//Internal
@ChannelHandler.Sharable
public class DummyTilePacketHandler implements IMessageHandler<DummyPacketIMSG_Tile, IMessage> {

	@Override
	public IMessage onMessage(DummyPacketIMSG_Tile message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			handleMessage(message, ctx);
		});

		return null;
	}

	public void handleMessage(DummyPacketIMSG_Tile message, MessageContext ctx)
	{
		Side s = ctx.side;
		int packetID = -10;
		if(message.dataTag.hasKey("packetID"))
		{
			packetID = message.dataTag.getInteger("packetID");
			message.dataTag.removeTag("packetID");
		}

		SPacketUpdateTileEntity genPkt = new SPacketUpdateTileEntity(new BlockPos(message.dataTag.getInteger("x"),message.dataTag.getInteger("y"),message.dataTag.getInteger("z")),packetID,message.dataTag);

		if(s == Side.CLIENT)
			CoreInitialiser.proxy.handlePacketS35(genPkt);
	}

	public static void sendToAll(DummyPacketIMSG_Tile message)
	{
		CoreInitialiser.network.sendToAll(message);
	}

	public static void sendToAllAround(DummyPacketIMSG_Tile message, TargetPoint pnt)
	{
		CoreInitialiser.network.sendToAllAround(message, pnt);
	}

	public static void sendToAllAround(DummyPacketIMSG_Tile message, int dim)
	{
		CoreInitialiser.network.sendToDimension(message, dim);
	}

	public static void sendToPlayer(DummyPacketIMSG_Tile message, EntityPlayerMP player)
	{
		CoreInitialiser.network.sendTo(message, player);
	}

	public static void sendToServer(DummyPacketIMSG_Tile message)
	{
		CoreInitialiser.network.sendToServer(message);
	}

}