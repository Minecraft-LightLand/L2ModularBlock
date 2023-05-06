package dev.xkmc.l2modularblock.one;

import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface RenderShapeBlockMethod extends SingletonBlockMethod {

	@OnlyIn(Dist.CLIENT)
	RenderShape getRenderShape(BlockState state);

}
