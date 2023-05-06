package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public interface PushReactionBlockMethod extends SingletonBlockMethod {

	PushReaction getPistonPushReaction(BlockState state);

}
