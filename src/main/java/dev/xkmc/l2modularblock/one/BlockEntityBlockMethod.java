package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityBlockMethod<T extends BlockEntity> extends SingletonBlockMethod {

	BlockEntity createTileEntity(BlockPos pos, BlockState state);

	BlockEntityType<T> getType();

	Class<T> getEntityClass();


}