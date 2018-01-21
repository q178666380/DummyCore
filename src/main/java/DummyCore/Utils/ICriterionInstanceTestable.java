package DummyCore.Utils;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * An {@link ICriterionInstance} that can be tested with a given type.
 * Copied from CyclopsCore
 * @author rubensworks
 */
public interface ICriterionInstanceTestable<D> extends ICriterionInstance {

	public boolean test(EntityPlayerMP player, D criterionData);
}