package com.robotgryphon.compactmachines.util;

import com.robotgryphon.compactmachines.core.Registration;
import com.robotgryphon.compactmachines.reference.EnumMachineSize;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.Arrays;

public class CompactStructureGenerator {
    /**
     * Generates a wall or platform in a given direction.
     *
     * @param world
     * @param size
     * @param cubeCenter
     * @param wallDirection
     */
    public static void generateCompactWall(IWorld world, EnumMachineSize size, BlockPos cubeCenter, Direction wallDirection) {
        int s = size.getInternalSize() / 2;

        BlockState unbreakableWall = Registration.BLOCK_SOLID_WALL.get().getDefaultState();

        BlockPos start = BlockPos.ZERO;
        AxisAlignedBB wallBounds;

        boolean horiz = wallDirection.getAxis().getPlane() == Direction.Plane.HORIZONTAL;
        if (horiz) {
            start = cubeCenter
                    .down(s)
                    .offset(wallDirection, s + 1);

            wallBounds = new AxisAlignedBB(start, start)
                    .expand(0, (s * 2) + 1, 0);
        } else {
            start = cubeCenter.offset(wallDirection, s + 1);

            wallBounds = new AxisAlignedBB(start, start)
                    .grow(s + 1, 0, s + 1);
        }

        switch (wallDirection) {
            case NORTH:
            case SOUTH:
                wallBounds = wallBounds.grow(s + 1, 0, 0);
                break;

            case WEST:
            case EAST:
                wallBounds = wallBounds.grow(0, 0, s + 1);
                break;
        }

        BlockPos.getAllInBox(wallBounds)
                .filter(world::isAirBlock)
                .map(BlockPos::toImmutable)
                .forEach(p -> world.setBlockState(p, unbreakableWall, 7));
    }

    /**
     * Generates a machine "internal" structure in a world via a machine size and a central point.
     *
     * @param world
     * @param size
     * @param center
     */
    public static void generateCompactStructure(IWorld world, EnumMachineSize size, BlockPos center) {
        int s = size.getInternalSize() / 2;

        BlockPos floorCenter = center.offset(Direction.DOWN, s);
        AxisAlignedBB floorBlocks = new AxisAlignedBB(floorCenter, floorCenter)
                .grow(s, 0, s);

        boolean anyAir = BlockPos.getAllInBox(floorBlocks).anyMatch(world::isAirBlock);

        if (anyAir) {
            // Generate the walls
            Arrays.stream(Direction.values())
                    .forEach(d -> generateCompactWall(world, size, center, d));
        }
    }
}
