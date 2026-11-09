package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

public interface PathFindBlockMethod extends MultipleBlockMethod {

	@Nullable Boolean isPathfindable(BlockState state, PathComputationType type);

}
