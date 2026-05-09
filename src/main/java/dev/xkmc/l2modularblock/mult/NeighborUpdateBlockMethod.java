package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public interface NeighborUpdateBlockMethod extends MultipleBlockMethod {

	void neighborChanged(Block self, BlockState state, Level world, BlockPos pos, Block nei_block, @Nullable Orientation orientation, boolean moving);

}
