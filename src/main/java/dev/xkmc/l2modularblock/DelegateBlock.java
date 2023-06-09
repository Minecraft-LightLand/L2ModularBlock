package dev.xkmc.l2modularblock;

import dev.xkmc.l2modularblock.one.BlockEntityBlockMethod;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DelegateBlock extends Block {

	public static DelegateBlock newBaseBlock(BlockBehaviour.Properties p, BlockMethod... impl) {
		for (BlockMethod m : impl) {
			if (m instanceof BlockEntityBlockMethod<?>) {
				return new DelegateEntityBlockImpl(p, impl);
			}
		}
		return new DelegateBlockImpl(p, impl);
	}

	@Deprecated
	public static DelegateBlock newBaseBlock(DelegateBlockProperties p, BlockMethod... impl) {
		return newBaseBlock(p.getProps(), impl);
	}

	protected DelegateBlock(Properties props) {
		super(props);
	}

}