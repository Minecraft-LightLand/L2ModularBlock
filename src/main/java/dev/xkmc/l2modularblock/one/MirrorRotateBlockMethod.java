package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public interface MirrorRotateBlockMethod extends SingletonBlockMethod {
	BlockState mirror(BlockState state, Mirror mirrorIn);

	BlockState rotate(BlockState state, Rotation rot);
}