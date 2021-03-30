package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.betterstrongholds.init.ModProcessors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * Dynamically generates legs below the stronghold.
 */
public class LegProcessor extends StructureProcessor {
    public static final LegProcessor INSTANCE = new LegProcessor();
    public static final Codec<LegProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @ParametersAreNonnullByDefault
    @Override
    public Template.BlockInfo process(IWorldReader worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, Template.BlockInfo blockInfoLocal, Template.BlockInfo blockInfoGlobal, PlacementSettings structurePlacementData, @Nullable Template template) {
        if (blockInfoGlobal.state.isIn(Blocks.YELLOW_STAINED_GLASS)) {
            ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos);
            IChunk currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);

            // Always replace the glass itself with stone bricks
            currentChunk.setBlockState(blockInfoGlobal.pos, Blocks.STONE_BRICKS.getDefaultState(), false);
            blockInfoGlobal = new Template.BlockInfo(blockInfoGlobal.pos, Blocks.STONE_BRICKS.getDefaultState(), blockInfoGlobal.nbt);

            // Straight line down
            BlockPos.Mutable mutable = blockInfoGlobal.pos.down().toMutable();
            BlockState currBlock = worldReader.getBlockState(mutable);
            int yBelow = 1;

            while (mutable.getY() > 0 && (currBlock.getMaterial() == Material.AIR || currBlock.getMaterial() == Material.WATER || currBlock.getMaterial() == Material.LAVA)) {
                // Generate vertical pillar
                currentChunk.setBlockState(mutable, Blocks.STONE_BRICKS.getDefaultState(), false);

                // Generate rafters
                if (yBelow == 1) {
                    BlockPos.Mutable tempMutable;
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        tempMutable = mutable.offset(direction).toMutable();
                        worldReader.getChunk(tempMutable).setBlockState(
                            tempMutable,
                            Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.HALF, Half.TOP).with(StairsBlock.FACING, direction.getOpposite()),
                            false);

                        tempMutable.move(direction);
                        worldReader.getChunk(tempMutable).setBlockState(
                            tempMutable,
                            Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.HALF, Half.TOP).with(StairsBlock.FACING, direction),
                            false);

                        tempMutable.move(direction);
                        worldReader.getChunk(tempMutable).setBlockState(
                            tempMutable,
                            Blocks.STONE_BRICKS.getDefaultState(),
                            false);
                    }
                } else if (yBelow == 2) {
                    BlockPos.Mutable tempMutable;
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        tempMutable = mutable.offset(direction).toMutable();
                        worldReader.getChunk(tempMutable).setBlockState(
                            tempMutable,
                            Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.HALF, Half.TOP).with(StairsBlock.FACING, direction.getOpposite()),
                            false);

                        tempMutable.move(direction);
                        worldReader.getChunk(tempMutable).setBlockState(
                            tempMutable,
                            Blocks.STONE_BRICK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP),
                            false);
                    }
                }

                mutable.move(Direction.DOWN);
                currBlock = worldReader.getBlockState(mutable);
                yBelow++;
            }
        }
        return blockInfoGlobal;
    }

    protected IStructureProcessorType<?> getType() {
        return ModProcessors.LEG_PROCESSOR;
    }

}
