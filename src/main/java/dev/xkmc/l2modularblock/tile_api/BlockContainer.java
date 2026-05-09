package dev.xkmc.l2modularblock.tile_api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;

public interface BlockContainer {

	static void onRemove(ServerLevel sl, BlockPos pos, Block block, BlockEntity entity) {
		if (entity instanceof BlockContainer blockContainer) {
			for (Container c : blockContainer.getContainers())
				Containers.dropContents(sl, pos, c);
		}
	}

	List<Container> getContainers();

}
