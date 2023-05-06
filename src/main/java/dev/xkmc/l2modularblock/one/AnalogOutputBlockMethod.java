package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface AnalogOutputBlockMethod extends SingletonBlockMethod {


	int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos);

	default boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}


}
