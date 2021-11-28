package com.mrbysco.spelled.block;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.container.AltarContainer;
import com.mrbysco.spelled.tile.LevelingAltarTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class LevelingAltarBlock extends ContainerBlock implements IBucketPickupHandler, ILiquidContainer {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape FALLBACK = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 12.0D, 12.0D);
    protected static final VoxelShape SHAPE = Stream.of(
            Block.box(8.08182, 0.75, 7.423660000000002, 15.08182, 12.75, 8.423660000000002),
            Block.box(13.33182, 12, 7.173660000000002, 15.33182, 13, 8.673660000000002),
            Block.box(14.33182, 1.5, 7.173660000000002, 15.33182, 2.5, 8.673660000000002),
            Block.box(14.33182, 11, 7.173660000000002, 15.33182, 12, 8.673660000000002),
            Block.box(13.33182, 0.5, 7.173660000000002, 15.33182, 1.5, 8.673660000000002),
            Block.box(7, 0.5, 7.500000000000002, 9, 13, 8.500000000000002),
            Block.box(0.68804, 0.5, 7.273510000000002, 2.68804, 1.5, 8.773510000000002),
            Block.box(0.68804, 1.5, 7.273510000000002, 1.68804, 2.5, 8.773510000000002),
            Block.box(0.68804, 11, 7.273510000000002, 1.68804, 12, 8.773510000000002),
            Block.box(0.68804, 12, 7.273510000000002, 2.68804, 13, 8.773510000000002),
            Block.box(0.93804, 0.75, 7.523510000000002, 7.93804, 12.75, 8.523510000000002),
            Block.box(7.75, 1.5, 7.250000000000002, 8.25, 12, 7.500000000000002)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    protected static final VoxelShape SHAPE_2 = Stream.of(
            Block.box(7.492089999999999, 0.75, 0.9899700000000013, 8.49209, 12.75, 7.989970000000001),
            Block.box(7.242089999999999, 12, 0.7399700000000013, 8.74209, 13, 2.7399700000000013),
            Block.box(7.242089999999999, 1.5, 0.7399700000000013, 8.74209, 2.5, 1.7399700000000013),
            Block.box(7.242089999999999, 11, 0.7399700000000013, 8.74209, 12, 1.7399700000000013),
            Block.box(7.242089999999999, 0.5, 0.7399700000000013, 8.74209, 1.5, 2.7399700000000013),
            Block.box(7.568429999999999, 0.5, 7.071790000000002, 8.56843, 13, 9.071790000000002),
            Block.box(7.341939999999999, 0.5, 13.383750000000001, 8.84194, 1.5, 15.383750000000001),
            Block.box(7.341939999999999, 1.5, 14.383750000000001, 8.84194, 2.5, 15.383750000000001),
            Block.box(7.341939999999999, 11, 14.383750000000001, 8.84194, 12, 15.383750000000001),
            Block.box(7.341939999999999, 12, 13.383750000000001, 8.84194, 13, 15.383750000000001),
            Block.box(7.591939999999999, 0.75, 8.133750000000001, 8.59194, 12.75, 15.133750000000001),
            Block.box(7.318429999999999, 1.5, 7.821790000000002, 7.568429999999999, 12, 8.321790000000002)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public LevelingAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.getValue(HORIZONTAL_FACING);
        if(SHAPE != null && SHAPE_2 != null) return direction.getAxis() == Direction.Axis.X ? SHAPE_2 : SHAPE;

        return FALLBACK;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);

        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }

                if (rand.nextInt(16) == 0) {
                    for(int k = 0; k <= 1; ++k) {
                        BlockPos blockpos = pos.offset(i, k, j);
                        if (worldIn.getBlockState(blockpos).getEnchantPowerBonus(worldIn, blockpos) > 0) {
                            if (!worldIn.isEmptyBlock(pos.offset(i / 2, 0, j / 2))) {
                                break;
                            }

                            worldIn.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }

    }

    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new LevelingAltarTile();
    }

    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(worldIn, pos));
            return ActionResultType.CONSUME;
        }
    }

    @Nullable
    public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LevelingAltarTile) {
            ITextComponent itextcomponent = ((INameable)tileentity).getDisplayName();
            return new SimpleNamedContainerProvider((id, inventory, player) -> {
                int level = worldIn.isClientSide ? 0 : SpelledAPI.getLevel((ServerPlayerEntity) player);
                return new AltarContainer(id, inventory, IWorldPosCallable.create(worldIn, pos), level);
            }, itextcomponent);
        } else {
            return null;
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof LevelingAltarTile) {
                ((LevelingAltarTile)tileentity).setCustomName(stack.getHoverName());
            }
        }

    }

    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    /**
     * Rotation and Waterlog section
     */


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public Fluid takeLiquid(IWorld worldIn, BlockPos pos, BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.valueOf(false)), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return !state.getValue(WATERLOGGED) && fluidIn == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.getValue(WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
            if (!worldIn.isClientSide()) {
                worldIn.setBlock(pos, state.setValue(WATERLOGGED, Boolean.valueOf(true)), 3);
                worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, WATERLOGGED);
    }
}
