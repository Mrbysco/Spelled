package com.mrbysco.spelled.block;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.container.AltarContainer;
import com.mrbysco.spelled.tile.LevelingAltarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class LevelingAltarBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
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
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
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
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public LevelingAltarBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(HORIZONTAL_FACING);
		if (SHAPE != null && SHAPE_2 != null) return direction.getAxis() == Direction.Axis.X ? SHAPE_2 : SHAPE;

		return FALLBACK;
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (rand.nextInt(16) == 0) {
					for (int k = 0; k <= 1; ++k) {
						BlockPos blockpos = pos.offset(i, k, j);
						if (worldIn.getBlockState(blockpos).getEnchantPowerBonus(worldIn, blockpos) > 0) {
							if (!worldIn.isEmptyBlock(pos.offset(i / 2, 0, j / 2))) {
								break;
							}

							worldIn.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D, (double) pos.getY() + 2.0D, (double) pos.getZ() + 0.5D, (double) ((float) i + rand.nextFloat()) - 0.5D, (double) ((float) k - rand.nextFloat() - 1.0F), (double) ((float) j + rand.nextFloat()) - 0.5D);
						}
					}
				}
			}
		}

	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LevelingAltarTile(pos, state);
	}

	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			player.openMenu(state.getMenuProvider(worldIn, pos));
			return InteractionResult.CONSUME;
		}
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity instanceof LevelingAltarTile) {
			Component itextcomponent = ((Nameable) blockEntity).getDisplayName();
			return new SimpleMenuProvider((id, inventory, player) -> {
				int level = worldIn.isClientSide ? 0 : SpelledAPI.getLevel((ServerPlayer) player);
				return new AltarContainer(id, inventory, ContainerLevelAccess.create(worldIn, pos), level);
			}, itextcomponent);
		} else {
			return null;
		}
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity blockEntity = worldIn.getBlockEntity(pos);
			if (blockEntity instanceof LevelingAltarTile) {
				((LevelingAltarTile) blockEntity).setCustomName(stack.getHoverName());
			}
		}

	}

	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}

	/**
	 * Rotation and Waterlog section
	 */


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, WATERLOGGED);
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.empty();
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter p_54766_, BlockPos p_54767_, BlockState p_54768_, Fluid p_54769_) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor p_54770_, BlockPos p_54771_, BlockState p_54772_, FluidState p_54773_) {
		return false;
	}
}
