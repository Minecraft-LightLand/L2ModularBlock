package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface PlacementBlockMethod extends MultipleBlockMethod {

	@Nullable
	BlockState getStateForPlacement(BlockState def, BlockPlaceContext context);

}