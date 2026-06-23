package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.mult.*;
import dev.xkmc.l2modularblock.one.*;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"deprecation"})
public class DelegateBlockImpl extends DelegateBlock {

	private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

	BlockImplementor impl;

	protected DelegateBlockImpl(BlockBehaviour.Properties p, BlockMethod... impl) {
		super(handler(new BlockImplementor(p).addImpls(impl)));
		registerDefaultState(this.impl.reduce(DefaultStateBlockMethod.class, defaultBlockState(),
				(state, def) -> def.getDefaultState(state)));
	}

	private static Properties handler(BlockImplementor bi) {
		if (TEMP.get() != null)
			throw new RuntimeException("concurrency error");
		TEMP.set(bi);
		return bi.props;
	}

	@Override
	public final boolean isSignalSource(BlockState bs) {
		return impl.one(BlockPowerBlockMethod.class).isPresent();
	}

	@Override
	public final int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos, Direction direction) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.getAnalogOutputSignal(blockState, levelIn, pos, direction)).orElse(0);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.hasAnalogOutputSignal(state)).orElse(false);
	}


	@Override
	public final int getLightEmission(BlockState bs, BlockGetter w, BlockPos pos) {
		return impl.one(LightBlockMethod.class).map(e -> e.getLightValue(bs, w, pos))
				.orElseGet(() -> super.getLightEmission(bs, w, pos));
	}

	@Override
	public final BlockState getStateForPlacement(BlockPlaceContext context) {
		return impl.reduce(PlacementBlockMethod.class, defaultBlockState(),
				(state, impl) -> state == null ? null : impl.getStateForPlacement(state, context));
	}

	@Override
	public final int getSignal(BlockState bs, BlockGetter r, BlockPos pos, Direction d) {
		return impl.one(BlockPowerBlockMethod.class)
				.map(e -> e.getSignal(bs, r, pos, d))
				.orElse(0);
	}

	@Override
	public final BlockState mirror(BlockState state, Mirror mirrorIn) {
		return impl.one(MirrorRotateBlockMethod.class).map(e -> e.mirror(state, mirrorIn)).orElse(state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState bs, Level w, BlockPos pos, Player pl, BlockHitResult hit) {
		return impl.execute(UseWithoutItemBlockMethod.class)
				.map(e -> e.useWithoutItem(bs, w, pos, pl, hit))
				.filter(e -> e != InteractionResult.PASS)
				.findFirst().orElse(InteractionResult.PASS);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return impl.execute(UseItemOnBlockMethod.class)
				.map(e -> e.useItemOn(stack, state, level, pos, player, hand, hit))
				.filter(e -> e != InteractionResult.PASS && e != InteractionResult.TRY_WITH_EMPTY_HAND)
				.findFirst().orElse(InteractionResult.TRY_WITH_EMPTY_HAND);
	}

	@Override
	public final BlockState rotate(BlockState state, Rotation rot) {
		return impl.one(MirrorRotateBlockMethod.class).map(e -> e.rotate(state, rot)).orElse(state);
	}

	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		impl = TEMP.get();
		TEMP.set(null);
		impl.forEach(CreateBlockStateBlockMethod.class, e -> e.createBlockStateDefinition(builder));
	}

	@Override
	public final void neighborChanged(BlockState state, Level level, BlockPos pos, Block nei_block, @Nullable Orientation orientation, boolean moving) {
		impl.forEach(NeighborUpdateBlockMethod.class, e -> e.neighborChanged(this, state, level, pos, nei_block, orientation, moving));
		super.neighborChanged(state, level, pos, nei_block, orientation, moving);
	}

	@Override
	public final void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		impl.forEach(RandomTickBlockMethod.class, e -> e.randomTick(state, level, pos, random));
	}

	@Override
	public final void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		impl.forEach(ScheduleTickBlockMethod.class, e -> e.tick(state, level, pos, random));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
		impl.forEach(AnimateTickBlockMethod.class, e -> e.animateTick(state, level, pos, r));
	}

	@Override
	public final void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		impl.one(EntityInsideBlockMethod.class).ifPresent(e -> e.entityInside(state, level, pos, entity, effectApplier, isPrecise));
	}

	@Override
	public final VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		if (!hasCollision) return Shapes.empty();
		return impl.one(ShapeBlockMethod.class).map(e -> e.getCollisionShape(state, level, pos, ctx))
				.orElseGet(() -> super.getCollisionShape(state, level, pos, ctx));
	}

	@Override
	public final VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getBlockSupportShape(state, level, pos))
				.orElseGet(() -> super.getBlockSupportShape(state, level, pos));
	}

	@Override
	public final VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getVisualShape(state, level, pos, ctx))
				.orElseGet(() -> super.getVisualShape(state, level, pos, ctx));
	}

	@Override
	public final VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return impl.one(ShapeBlockMethod.class).map(e -> e.getShape(state, level, pos, ctx))
				.orElseGet(() -> super.getShape(state, level, pos, ctx));
	}

	@Override
	public final void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double height) {
		if (impl.reduce(FallOnBlockMethod.class, true, (a, e) -> a & e.fallOn(level, state, pos, entity, height))) {
			super.fallOn(level, state, pos, entity, height);
		}
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
		return impl.one(GetBlockItemBlockMethod.class).map(e -> e.getCloneItemStack(level, pos, state, includeData))
				.orElseGet(() -> super.getCloneItemStack(level, pos, state, includeData));
	}

	@Override
	public final List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return impl.one(SpecialDropBlockMethod.class).map(e -> e.getDrops(state, builder))
				.orElseGet(() -> super.getDrops(state, builder));
	}

	@Override
	public final void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		impl.forEach(SetPlacedByBlockMethod.class, e -> e.setPlacedBy(level, pos, state, entity, stack));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final RenderShape getRenderShape(BlockState state) {
		return impl.one(RenderShapeBlockMethod.class).map(e -> e.getRenderShape(state)).orElseGet(() -> super.getRenderShape(state));
	}

	@Override
	public final void attack(BlockState state, Level level, BlockPos pos, Player player) {
		impl.execute(AttackBlockMethod.class).filter(u -> u.attack(state, level, pos, player)).findFirst();
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		return impl.reduce(ShapeUpdateBlockMethod.class, state, (currentState, e) ->
				e.updateShape(this, currentState, state, directionToNeighbour, neighbourState, level, pos, neighbourPos, ticks, random));
	}

	@Override
	public final void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
		impl.forEach(OnPlaceBlockMethod.class, e -> e.onPlace(state, level, pos, old, moving));
	}

	@Override
	public final void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		impl.forEach(StepOnBlockMethod.class, e -> e.stepOn(pLevel, pPos, pState, pEntity));
	}

	@Override
	protected final boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return impl.testAnd(SurviveBlockMethod.class, e -> e.canSurvive(state, level, pos));
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate) {
		ItemStack stack = context.getItemInHand();
		if (!stack.canPerformAction(ability)) {
			return null;
		}
		return impl.reduce(ToolModifyBlockMethod.class, super.getToolModifiedState(state, context, ability, simulate),
				(current, impl) -> impl.getToolModifiedState(this, current, state, context, ability, simulate));
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return impl.one(PathFindBlockMethod.class)
				.map(e -> e.isPathfindable(state, type))
				.orElseGet(() -> super.isPathfindable(state, type));
	}

	public final BlockImplementor getImpl() {
		return impl;
	}

}
