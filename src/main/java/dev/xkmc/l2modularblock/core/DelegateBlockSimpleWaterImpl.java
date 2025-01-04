package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.mult.ChangeStateExtraBlockMethod;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DelegateBlockSimpleWaterImpl extends DelegateBlockImpl implements SimpleWaterloggedBlock {

	protected DelegateBlockSimpleWaterImpl(Properties p, BlockMethod... impl) {
		super(p, impl);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			BlockState next = state.setValue(BlockStateProperties.WATERLOGGED, true);
			impl.reduce(ChangeStateExtraBlockMethod.class, next, (u, t) -> t.changeState(this, u, state, level, pos));
			level.setBlock(pos, next, 3);
			level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
			return true;
		} else {
			return false;
		}
	}

	// efficient implementation
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ?
				Fluids.WATER.getSource(false) :
				Fluids.EMPTY.defaultFluidState();
	}

}
