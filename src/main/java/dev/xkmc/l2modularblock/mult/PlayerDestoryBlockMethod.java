package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface PlayerDestoryBlockMethod extends MultipleBlockMethod {

	boolean playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack stack);

	void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player);

}
