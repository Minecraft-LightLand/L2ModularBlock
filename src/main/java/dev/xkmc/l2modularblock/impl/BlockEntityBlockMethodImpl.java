package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.mult.SetPlacedByBlockMethod;
import dev.xkmc.l2modularblock.mult.UseWithoutItemBlockMethod;
import dev.xkmc.l2modularblock.one.AnalogOutputBlockMethod;
import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * To make it tickable, implements TickableBlockEntity <br>
 * To make it show menu, implements MenuProvider <br>
 * To make it menu show name, implements NameSetable <br>
 * To make it drop content when break, implements Container or BlockContainer <br>
 * To make it output redstone signal, implements Container <br>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockEntityBlockMethodImpl<T extends BlockEntity> implements BlockEntityBlockMethod<T>, UseWithoutItemBlockMethod,
		SetPlacedByBlockMethod, AnalogOutputBlockMethod {

	private final Supplier<BlockEntityType<T>> type;
	private final Class<T> cls;

	public BlockEntityBlockMethodImpl(Supplier<BlockEntityType<T>> type, Class<T> cls) {
		this.type = type;
		this.cls = cls;
	}

	@Override
	public T createTileEntity(BlockPos pos, BlockState state) {
		T ans = type.get().create(pos, state);
		assert ans != null;
		return ans;
	}

	@Override
	public BlockEntityType<T> getType() {
		return type.get();
	}

	@Override
	public Class<T> getEntityClass() {
		return cls;
	}

	@Override
	public InteractionResult clickNoItem(BlockState state, Level level, BlockPos pos, Player pl, BlockHitResult result) {
		BlockEntity te = level.getBlockEntity(pos);
		if (level.isClientSide())
			return te instanceof MenuProvider ? InteractionResult.SUCCESS : InteractionResult.PASS;
		if (te instanceof MenuProvider) {
			pl.openMenu((MenuProvider) te);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		//TODO set placed by set name
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		BlockEntity e = worldIn.getBlockEntity(pos);
		if (e != null) {
			if (e instanceof Container) {
				return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(e);
			}
		}
		var cap = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (cap != null) {
			return ItemHandlerHelper.calcRedstoneFromInventory(cap);
		}
		return 0;
	}

}
