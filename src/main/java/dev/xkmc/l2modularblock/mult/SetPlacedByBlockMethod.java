package dev.xkmc.l2modularblock.mult;

import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface SetPlacedByBlockMethod extends MultipleBlockMethod {

	void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack);

}
