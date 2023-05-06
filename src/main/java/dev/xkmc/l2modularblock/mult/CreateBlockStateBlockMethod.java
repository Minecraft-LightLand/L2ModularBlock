package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public interface CreateBlockStateBlockMethod extends MultipleBlockMethod {

	void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

}