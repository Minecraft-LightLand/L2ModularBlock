package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeStateExtraBlockMethod extends MultipleBlockMethod {

	BlockState changeState(Block self, BlockState selfCurrent, BlockState selfOld, LevelAccessor level, BlockPos pos);

}
