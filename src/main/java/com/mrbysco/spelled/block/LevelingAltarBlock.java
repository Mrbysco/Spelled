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
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class LevelingAltarBlock extends ContainerBlock implements IBucketPickupHandler, ILiquidContainer {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape FALLBACK = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 12.0D, 12.0D, 12.0D);
    protected static final Optional<VoxelShape> SHAPE = Stream.of(
            Block.makeCuboidShape(8.08182, 0.75, 7.423660000000002, 15.08182, 12.75, 8.423660000000002),
            Block.makeCuboidShape(13.33182, 12, 7.173660000000002, 15.33182, 13, 8.673660000000002),
            Block.makeCuboidShape(14.33182, 1.5, 7.173660000000002, 15.33182, 2.5, 8.673660000000002),
            Block.makeCuboidShape(14.33182, 11, 7.173660000000002, 15.33182, 12, 8.673660000000002),
            Block.makeCuboidShape(13.33182, 0.5, 7.173660000000002, 15.33182, 1.5, 8.673660000000002),
            Block.makeCuboidShape(7, 0.5, 7.500000000000002, 9, 13, 8.500000000000002),
            Block.makeCuboidShape(0.68804, 0.5, 7.273510000000002, 2.68804, 1.5, 8.773510000000002),
            Block.makeCuboidShape(0.68804, 1.5, 7.273510000000002, 1.68804, 2.5, 8.773510000000002),
            Block.makeCuboidShape(0.68804, 11, 7.273510000000002, 1.68804, 12, 8.773510000000002),
            Block.makeCuboidShape(0.68804, 12, 7.273510000000002, 2.68804, 13, 8.773510000000002),
            Block.makeCuboidShape(0.93804, 0.75, 7.523510000000002, 7.93804, 12.75, 8.523510000000002),
            Block.makeCuboidShape(7.75, 1.5, 7.250000000000002, 8.25, 12, 7.500000000000002)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));
    protected static final Optional<VoxelShape> SHAPE_2 = Stream.of(
            Block.makeCuboidShape(7.492089999999999, 0.75, 0.9899700000000013, 8.49209, 12.75, 7.989970000000001),
            Block.makeCuboidShape(7.242089999999999, 12, 0.7399700000000013, 8.74209, 13, 2.7399700000000013),
            Block.makeCuboidShape(7.242089999999999, 1.5, 0.7399700000000013, 8.74209, 2.5, 1.7399700000000013),
            Block.makeCuboidShape(7.242089999999999, 11, 0.7399700000000013, 8.74209, 12, 1.7399700000000013),
            Block.makeCuboidShape(7.242089999999999, 0.5, 0.7399700000000013, 8.74209, 1.5, 2.7399700000000013),
            Block.makeCuboidShape(7.568429999999999, 0.5, 7.071790000000002, 8.56843, 13, 9.071790000000002),
            Block.makeCuboidShape(7.341939999999999, 0.5, 13.383750000000001, 8.84194, 1.5, 15.383750000000001),
            Block.makeCuboidShape(7.341939999999999, 1.5, 14.383750000000001, 8.84194, 2.5, 15.383750000000001),
            Block.makeCuboidShape(7.341939999999999, 11, 14.383750000000001, 8.84194, 12, 15.383750000000001),
            Block.makeCuboidShape(7.341939999999999, 12, 13.383750000000001, 8.84194, 13, 15.383750000000001),
            Block.makeCuboidShape(7.591939999999999, 0.75, 8.133750000000001, 8.59194, 12.75, 15.133750000000001),
            Block.makeCuboidShape(7.318429999999999, 1.5, 7.821790000000002, 7.568429999999999, 12, 8.321790000000002)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR));

    public LevelingAltarBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };

        int times = (to.getHorizontalIndex() - from.getHorizontalIndex() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1], VoxelShapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        if(SHAPE.isPresent() && SHAPE_2.isPresent()) {
            return direction.getAxis() == Direction.Axis.X ? SHAPE_2.get() : SHAPE.get();
        }

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
                        BlockPos blockpos = pos.add(i, k, j);
                        if (worldIn.getBlockState(blockpos).getEnchantPowerBonus(worldIn, blockpos) > 0) {
                            if (!worldIn.isAirBlock(pos.add(i / 2, 0, j / 2))) {
                                break;
                            }

                            worldIn.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)i + rand.nextFloat()) - 0.5D, (double)((float)k - rand.nextFloat() - 1.0F), (double)((float)j + rand.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }

    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new LevelingAltarTile();
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(worldIn, pos));
            return ActionResultType.CONSUME;
        }
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof LevelingAltarTile) {
            ITextComponent itextcomponent = ((INameable)tileentity).getDisplayName();
            return new SimpleNamedContainerProvider((id, inventory, player) -> {
                int level = worldIn.isRemote ? 0 : SpelledAPI.getLevel((ServerPlayerEntity) player);
                return new AltarContainer(id, inventory, IWorldPosCallable.of(worldIn, pos), level);
            }, itextcomponent);
        } else {
            return null;
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof LevelingAltarTile) {
                ((LevelingAltarTile)tileentity).setCustomName(stack.getDisplayName());
            }
        }

    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    /**
     * Rotation and Waterlog section
     */


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        if (state.get(WATERLOGGED)) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
            if (!worldIn.isRemote()) {
                worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
                worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, WATERLOGGED);
    }
}
