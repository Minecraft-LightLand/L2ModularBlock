package dev.xkmc.l2modularblock.impl;

import dev.xkmc.l2modularblock.core.BlockTemplates;
import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.PlacementBlockMethod;
import dev.xkmc.l2modularblock.one.MirrorRotateBlockMethod;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class AllDireBlockMethodImpl implements PlacementBlockMethod, CreateBlockStateBlockMethod, MirrorRotateBlockMethod {

	public AllDireBlockMethodImpl() {
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockTemplates.FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockState def, BlockPlaceContext context) {
		return def.setValue(BlockTemplates.FACING, context.getClickedFace().getOpposite());
	}


	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(BlockTemplates.FACING, rot.rotate(state.getValue(BlockTemplates.FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(BlockTemplates.FACING)));
	}
}
