package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public interface PathFindBlockMethod extends SingletonBlockMethod {

	boolean isPathfindable(BlockState state, PathComputationType type);

}
