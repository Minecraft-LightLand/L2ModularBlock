package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ShapeUpdateBlockMethod extends MultipleBlockMethod {

	BlockState updateShape(
			Block self,
			BlockState selfCurrent, BlockState selfOld,
			Direction from, BlockState sourceState,
			LevelReader level, BlockPos selfPos, BlockPos sourcePos,
			ScheduledTickAccess ticks, RandomSource random
	);

}
