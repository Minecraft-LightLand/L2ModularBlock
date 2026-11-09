package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.mult.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HALF;

public final class DoubleBlockImpl implements CreateBlockStateBlockMethod, DefaultStateBlockMethod, PlacementBlockMethod,
		ShapeUpdateBlockMethod, SetPlacedByBlockMethod, SurviveBlockMethod, PlayerDestoryBlockMethod {

	public BlockState updateShape(Block self, BlockState current, BlockState state, Direction dir, BlockState nstate, LevelAccessor level, BlockPos pos, BlockPos npos) {
		var half = current.getValue(HALF);
		if (dir.getAxis() == Direction.Axis.Y && (half == Half.BOTTOM) == (dir == Direction.UP) &&
				!(nstate.is(self) && nstate.getValue(HALF) != half))
			return Blocks.AIR.defaultBlockState();
		return current;
	}

	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}

	@Override
	public BlockState getDefaultState(BlockState state) {
		return state.setValue(HALF, Half.BOTTOM);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockState state, BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context) ?
				state : null;
	}

	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity user, ItemStack stack) {
		BlockPos blockpos = pos.above();
		level.setBlock(blockpos, state.setValue(HALF, Half.TOP), 3);
	}

	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(HALF) == Half.BOTTOM) return true;
		BlockState lower = level.getBlockState(pos.below());
		return lower.is(state.getBlock()) && lower.getValue(HALF) == Half.BOTTOM;
	}

	public @Nullable BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			if (player.isCreative()) {
				preventDropFromBottomPart(level, pos, state, player);
			} else {
				Block.dropResources(state, level, pos, null, player, player.getMainHandItem());
			}
		}
		return null;
	}

	public boolean playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable BlockEntity entity, ItemStack stack) {
		return true;
	}

	public static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
		var half = state.getValue(HALF);
		if (half == Half.BOTTOM) return;
		BlockPos lo = pos.below();
		BlockState bottom = level.getBlockState(lo);
		if (bottom.is(state.getBlock()) && bottom.getValue(HALF) == Half.BOTTOM) {
			level.setBlock(lo, Blocks.AIR.defaultBlockState(), 35);
			level.levelEvent(player, 2001, lo, Block.getId(bottom));
		}
	}

}
