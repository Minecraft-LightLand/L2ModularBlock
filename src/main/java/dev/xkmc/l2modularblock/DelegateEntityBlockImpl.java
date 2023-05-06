package dev.xkmc.l2modularblock;

import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.type.BlockMethod;
import dev.xkmc.l2modularblock.tile_api.TickableBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DelegateEntityBlockImpl extends DelegateBlockImpl implements EntityBlock {

	protected DelegateEntityBlockImpl(DelegateBlockProperties p, BlockMethod... impl) {
		super(p, impl);
	}

	@Override
	public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return impl.one(BlockEntityBlockMethod.class).map(e -> e.createTileEntity(pos, state)).orElse(null);
	}

	@Nullable
	public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return impl.one(BlockEntityBlockMethod.class).map(e -> {
			if (type != e.getType() || !TickableBlockEntity.class.isAssignableFrom(e.getEntityClass()))
				return null;
			return (BlockEntityTicker<T>) (l, p, s, t) -> {
				if (t instanceof TickableBlockEntity tbe)
					tbe.tick();
			};
		}).orElse(null);
	}


	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int p_49229_, int p_49230_) {
		super.triggerEvent(state, level, pos, p_49229_, p_49230_);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity != null && blockentity.triggerEvent(p_49229_, p_49230_);
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
	}

}
