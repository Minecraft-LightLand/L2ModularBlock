package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface OnReplacedBlockMethod extends MultipleBlockMethod {
	void onReplaced(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving);
}