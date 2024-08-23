package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.type.BlockMethod;
import dev.xkmc.l2modularblock.type.WaterloggedMethodMarker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DelegateBlock extends Block {

	public static DelegateBlock newBaseBlock(BlockBehaviour.Properties p, BlockMethod... impl) {
		boolean water = false, entity = false;
		for (BlockMethod m : impl) {
			if (m instanceof BlockEntityBlockMethod<?>) {
				entity = true;
			}
			if (m instanceof WaterloggedMethodMarker) {
				water = true;
			}
		}
		return water ?
				entity ? new DelegateEntityBlockSimpleWaterImpl(p, impl) :
						new DelegateBlockSimpleWaterImpl(p, impl) :
				entity ? new DelegateEntityBlockImpl(p, impl) :
						new DelegateBlockImpl(p, impl);
	}

	protected DelegateBlock(Properties props) {
		super(props);
	}

}