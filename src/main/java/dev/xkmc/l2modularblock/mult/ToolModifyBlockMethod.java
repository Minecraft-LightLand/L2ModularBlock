package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public interface ToolModifyBlockMethod extends MultipleBlockMethod {

	@Nullable
	BlockState getToolModifiedState(Block block, @Nullable BlockState current, BlockState old, UseOnContext ctx, ItemAbility ability, boolean simulate);

}
