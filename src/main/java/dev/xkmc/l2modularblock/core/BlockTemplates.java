package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.impl.*;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlockTemplates {

	public static final BlockMethod POWER = new PowerBlockMethodImpl();
	public static final BlockMethod ALL_DIRECTION = new AllDireBlockMethodImpl();
	public static final BlockMethod HORIZONTAL = new HorizontalBlockMethodImpl();
	public static final BlockMethod TRIGGER = new TriggerBlockMethodImpl(4);
	public static final BlockMethod WATER = new SimpleWaterloggedImpl();

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

}
