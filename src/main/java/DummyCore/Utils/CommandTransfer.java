package DummyCore.Utils;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

/**
 * Internal. A command to switch dimensions
 * @author modbder
 *
 */
public class CommandTransfer extends CommandBase {

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		return "/tpToDimension <player> <dimensionID> <x> <y> <z>";
	}

	@Override
	public void execute(MinecraftServer p_71515_0_, ICommandSender p_71515_1_, String[] p_71515_2_) throws CommandException {
		try {
			int var3 = parseInt(p_71515_2_[1]);
			EntityPlayerMP player = p_71515_2_.length == 0 ? getCommandSenderAsPlayer(p_71515_1_) : getPlayer(p_71515_0_, p_71515_1_, p_71515_2_[0]);
			BlockPos pos = p_71515_2_.length > 2 ? parseBlockPos(p_71515_1_, p_71515_2_, 2, true) : player.getPosition();
			WorldServer ws = p_71515_0_.worldServerForDimension(var3);
			Teleporter teleporter = new DummyTeleporter(ws, pos.getX()+0.5D, pos.getY(), pos.getZ()+0.5D, DummyPortalGenerator.TELEPORT_ONLY, false);
			DummyPortalHandler.transferPlayerToDimension(player, var3, teleporter);
		}
		catch(Exception e){
			throw new CommandException("Error trying to teleport player to dimension", new Object[0]);
		}
	}

	@Override
	public String getName() {
		return "tpToDimension";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : args.length >= 3 && args.length <= 5 ? getTabCompletionCoordinate(args, 1, pos) : Collections.<String>emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return super.isUsernameIndex(args, index);
	}
}
