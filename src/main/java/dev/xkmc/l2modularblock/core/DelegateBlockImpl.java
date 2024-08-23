package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.mult.*;
import dev.xkmc.l2modularblock.one.*;
import dev.xkmc.l2modularblock.tile_api.BlockContainer;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
	public final int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.getAnalogOutputSignal(blockState, levelIn, pos)).orElse(0);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return impl.one(AnalogOutputBlockMethod.class).map(e -> e.hasAnalogOutputSignal(state)).orElse(false);
	}


	@Override
	public final int getLightEmission(BlockState bs, BlockGetter w, BlockPos pos) {
		return impl.one(LightBlockMethod.class).map(e -> e.getLightValue(bs, w, pos))
				.orElse(super.getLightEmission(bs, w, pos));
	}

	@Override
	public final BlockState getStateForPlacement(BlockPlaceContext context) {
		return impl.reduce(PlacementBlockMethod.class, defaultBlockState(),
				(state, impl) -> impl.getStateForPlacement(state, context));
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
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return impl.execute(UseItemOnBlockMethod.class)
				.map(e -> e.useItemOn(stack, state, level, pos, player, hand, hit))
				.filter(e -> e != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
				.findFirst().orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
	}

	@Override
	public final void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
		impl.forEach(OnReplacedBlockMethod.class, e -> e.onReplaced(state, levelIn, pos, newState, isMoving));
		if (impl.one(BlockEntityBlockMethod.class).isPresent() && state.getBlock() != newState.getBlock()) {
			BlockEntity entity = levelIn.getBlockEntity(pos);
			if (entity != null) {
				if (entity instanceof Container) {
					Containers.dropContents(levelIn, pos, (Container) entity);
					levelIn.updateNeighbourForOutputSignal(pos, this);
				} else if (entity instanceof BlockContainer blockContainer) {
					for (Container c : blockContainer.getContainers())
						Containers.dropContents(levelIn, pos, c);
					levelIn.updateNeighbourForOutputSignal(pos, this);
				}
				levelIn.removeBlockEntity(pos);
			}
		}
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
	public final void neighborChanged(BlockState state, Level level, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
		impl.forEach(NeighborUpdateBlockMethod.class, e -> e.neighborChanged(this, state, level, pos, nei_block, nei_pos, moving));
		super.neighborChanged(state, level, pos, nei_block, nei_pos, moving);
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
	public final void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		impl.one(EntityInsideBlockMethod.class).ifPresent(e -> e.entityInside(state, level, pos, entity));
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
	public final void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height) {
		if (impl.reduce(FallOnBlockMethod.class, true, (a, e) -> a & e.fallOn(level, state, pos, entity, height))) {
			super.fallOn(level, state, pos, entity, height);
		}
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return impl.one(GetBlockItemBlockMethod.class).map(e -> e.getCloneItemStack(level, pos, state))
				.orElse(super.getCloneItemStack(level, pos, state));
	}

	@Override
	public final List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return impl.one(SpecialDropBlockMethod.class).map(e -> e.getDrops(state, builder))
				.orElse(super.getDrops(state, builder));
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
	public final BlockState updateShape(BlockState selfState, Direction from, BlockState sourceState, LevelAccessor level, BlockPos selfPos, BlockPos sourcePos) {
		return impl.reduce(ShapeUpdateBlockMethod.class, selfState, (currentState, e) -> e.updateShape(this, currentState, selfState, from, sourceState, level, selfPos, sourcePos));
	}

	@Override
	public final void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		impl.forEach(ToolTipBlockMethod.class, e -> e.appendHoverText(stack, ctx, list, flag));
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

	public final BlockImplementor getImpl() {
		return impl;
	}

}
