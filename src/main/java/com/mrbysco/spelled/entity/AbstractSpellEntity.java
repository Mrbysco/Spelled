package com.mrbysco.spelled.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.IceBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.OptionalInt;
import java.util.function.Predicate;

public abstract class AbstractSpellEntity extends DamagingProjectileEntity {
    private static final DataParameter<Integer> SPELL_TYPE = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.VARINT);
    private static final DataParameter<OptionalInt> COLOR = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Boolean> FIERY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAVA = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WATER = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> COLD = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SNOW = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HARVEST = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SMOKY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> INKY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<OptionalInt> HEALING = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<OptionalInt> DAMAGE = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<OptionalInt> KNOCKBACK = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Float> SIZE_MULTIPLIER = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.FLOAT);

    public AbstractSpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SPELL_TYPE, 0);
        this.dataManager.register(COLOR, OptionalInt.empty());
        this.dataManager.register(FIERY, false);
        this.dataManager.register(LAVA, false);
        this.dataManager.register(WATER, false);
        this.dataManager.register(COLD, false);
        this.dataManager.register(SNOW, false);
        this.dataManager.register(EXPLODING, false);
        this.dataManager.register(HARVEST, false);
        this.dataManager.register(SMOKY, false);
        this.dataManager.register(INKY, false);
        this.dataManager.register(HEALING, OptionalInt.empty());
        this.dataManager.register(DAMAGE, OptionalInt.empty());
        this.dataManager.register(KNOCKBACK, OptionalInt.empty());
        this.dataManager.register(SIZE_MULTIPLIER, 1.0F);
    }

    public void setSpellType(int type) {
        this.getDataManager().set(SPELL_TYPE, 0);
    }
    public int getSpellType() {
        return this.getDataManager().get(SPELL_TYPE);
    }

    public void setColor(int color) {
        this.getDataManager().set(COLOR, OptionalInt.of(color));
    }
    public OptionalInt getColor() {
        return this.getDataManager().get(COLOR);
    }
    public boolean hasColor() { return this.getDataManager().get(COLOR).isPresent(); }

    public void setFiery(boolean fiery) {
        this.getDataManager().set(FIERY, fiery);
    }
    public boolean isFiery() {
        return this.getDataManager().get(FIERY);
    }

    public void setLava(boolean lava) {
        this.getDataManager().set(LAVA, lava);
    }
    public boolean isLava() {
        return this.getDataManager().get(LAVA);
    }

    public void setWater(boolean water) {
        this.getDataManager().set(WATER, water);
    }
    public boolean isWater() {
        return this.getDataManager().get(WATER);
    }

    public void setCold(boolean cold) {
        this.getDataManager().set(COLD, cold);
    }
    public boolean isCold() {
        return this.getDataManager().get(COLD);
    }

    public void setSnow(boolean snow) {
        this.getDataManager().set(SNOW, snow);
    }
    public boolean isSnow() {
        return this.getDataManager().get(SNOW);
    }

    public void setExploding(boolean explodes) {
        this.getDataManager().set(EXPLODING, explodes);
    }
    public boolean doesExplode() {
        return this.getDataManager().get(EXPLODING);
    }

    public void setHarvests(boolean harvest) {
        this.getDataManager().set(HARVEST, harvest);
    }
    public boolean doesHarvest() {
        return this.getDataManager().get(HARVEST);
    }

    public void setSmoky(boolean smoky) {
        this.getDataManager().set(SMOKY, smoky);
    }
    public boolean isSmoky() {
        return this.getDataManager().get(SMOKY);
    }

    public void setInky(boolean inky) {
        this.getDataManager().set(INKY, inky);
    }
    public boolean isInky() {
        return this.getDataManager().get(INKY);
    }

    public void setKnockback(int knockback) {
        this.getDataManager().set(KNOCKBACK, OptionalInt.of(knockback));
    }
    public OptionalInt getKnockback() {
        return this.getDataManager().get(KNOCKBACK);
    }
    public boolean hasKnockback() { return this.getDataManager().get(KNOCKBACK).isPresent(); }

    public void setHealing(int healFactor) {
        this.getDataManager().set(HEALING, OptionalInt.of(healFactor));
    }
    public OptionalInt getHealingFactor() {
        return this.getDataManager().get(HEALING);
    }
    public boolean isHealing() { return this.getDataManager().get(HEALING).isPresent(); }

    public void setDamage(int damage) {
        this.getDataManager().set(DAMAGE, OptionalInt.of(damage));
    }
    public OptionalInt getDamage() {
        return this.getDataManager().get(DAMAGE);
    }
    public boolean doesHurt() { return this.getDataManager().get(DAMAGE).isPresent(); }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.getDataManager().set(SIZE_MULTIPLIER, sizeMultiplier);
        this.recenterBoundingBox();
        this.recalculateSize();
    }
    public float getSizeMultiplier() {
        return this.getDataManager().get(SIZE_MULTIPLIER);
    }

    public void recalculateSize() {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        super.recalculateSize();
        this.setPosition(d0, d1, d2);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SIZE_MULTIPLIER.equals(key)) {
            this.recalculateSize();
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(this.getSizeMultiplier());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        if(compound.contains("colorPresent"))
            setColor(compound.getInt("Color"));

        setFiery(compound.getBoolean("Fiery"));
        setLava(compound.getBoolean("Lava"));
        setWater(compound.getBoolean("Water"));
        setCold(compound.getBoolean("Cold"));
        setSnow(compound.getBoolean("Snow"));
        setExploding(compound.getBoolean("Exploding"));
        setHarvests(compound.getBoolean("Harvest"));
        setSmoky(compound.getBoolean("Smoky"));
        setInky(compound.getBoolean("Inky"));

        if(compound.contains("healingPresent"))
            setHealing(compound.getInt("HealingFactor"));

        if(compound.contains("damagePresent"))
            setDamage(compound.getInt("DamageAmount"));

        if(compound.contains("knockbackPresent"))
            setKnockback(compound.getInt("KnockbackAmount"));

        setSizeMultiplier(compound.getFloat("SizeMultiplier"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putBoolean("colorPresent", hasColor());
        if (hasColor())
            compound.putInt("Color", getColor().getAsInt());

        compound.putBoolean("Fiery", isFiery());
        compound.putBoolean("Lava", isLava());
        compound.putBoolean("Water", isWater());
        compound.putBoolean("Cold", isCold());
        compound.putBoolean("Snow", isSnow());
        compound.putBoolean("Exploding", doesExplode());
        compound.putBoolean("Harvest", doesHarvest());
        compound.putBoolean("Smoky", isSmoky());
        compound.putBoolean("Inky", isInky());

        compound.putBoolean("healingPresent", isHealing());
        if (isHealing())
            compound.putInt("HealingFactor", getHealingFactor().getAsInt());

        compound.putBoolean("damagePresent", doesHurt());
        if (doesHurt())
            compound.putInt("DamageAmount", getDamage().getAsInt());

        compound.putBoolean("knockbackPresent", hasKnockback());
        if (hasKnockback())
            compound.putInt("KnockbackAmount", getKnockback().getAsInt());

        compound.putFloat("SizeMultiplier", getSizeMultiplier());
    }

    @Override
    protected boolean isFireballFiery() {
        return isFiery();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected IParticleData getParticle() {
        if(isLava() && (isCold() || isSnow() || isWater())) {
            return new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState());
        } else {
            if(isFiery() && isSnow())
                return ParticleTypes.DRIPPING_WATER;
            if(isSmoky())
                return ParticleTypes.SMOKE;
            if(isInky())
                return ParticleTypes.SQUID_INK;
            if(isFiery())
                return ParticleTypes.FLAME;
            if(isSnow())
                return ParticleTypes.ITEM_SNOWBALL;

            return ParticleTypes.WITCH;
        }
    }

    @Override
    public float getBrightness() {
        return (isFiery() || isLava()) ? 1.0F : 0.5F;
    }

    @Override
    public void tick() {
        super.tick();

        if (ticksExisted > 200) {
            this.remove();
        }

        if(isCold() || isWater()) {
            Entity entity = this.func_234616_v_();
            if (this.world.isRemote || (entity == null || !entity.removed) && this.world.isBlockLoaded(this.getPosition())) {

                RayTraceResult raytraceresult = rayTraceWater(this::func_230298_a_);
                if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                    this.onImpact(raytraceresult);
                }
            }
        }
    }

    public RayTraceResult rayTraceWater(Predicate<Entity> entityPredicate) {
        Vector3d vector3d = this.getMotion();
        World world = this.world;
        Vector3d vector3d1 = this.getPositionVec();
        Vector3d vector3d2 = vector3d1.add(vector3d);
        RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, FluidMode.SOURCE_ONLY, this));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d2 = raytraceresult.getHitVec();
        }

        RayTraceResult raytraceresult1 = ProjectileHelper.rayTraceEntities(world, this, vector3d1, vector3d2, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), entityPredicate);
        if (raytraceresult1 != null) {
            raytraceresult = raytraceresult1;
        }

        return raytraceresult;
    }

    public void explode() {
        if (doesExplode()) {
            boolean flag = isSnow() || isWater();
            int size = (int) Math.ceil(1 * getSizeMultiplier());
            this.world.createExplosion((Entity) null, this.getPosX(), this.getPosY(), this.getPosZ(), (float) size, !flag, !flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
        }
    }

    public Iterable<BlockPos> getSizedPos(BlockPos pos) {
        double offset = getSizeMultiplier() * 0.5f;
        return BlockPos.getAllInBoxMutable(pos.add(-offset, -offset, -offset), pos.add(offset, offset, offset));
    }

    public void executeBreakBehavior(BlockPos pos) {
        BlockState hitState = this.world.getBlockState(pos);
        if(hitState.getHarvestLevel() <= 2) {
            world.destroyBlock(pos, true);
        }
    }

    public void executeFireBehavior(BlockPos offPos) {
        BlockState offState = this.world.getBlockState(offPos);

        if (offState.getMaterial().isReplaceable()) {
            this.world.setBlockState(offPos, AbstractFireBlock.getFireForPlacement(this.world, offPos));
        }
    }

    public void executeLavaBehavior(BlockPos offPos) {
        BlockState offState = this.world.getBlockState(offPos);

        if (offState.isReplaceable(Fluids.LAVA)) {
            this.world.setBlockState(offPos, Blocks.LAVA.getDefaultState());
        }
    }

    public void executeWaterBehavior(BlockPos pos, BlockPos offPos) {
        BlockState hitState = this.world.getBlockState(pos);
        BlockState offState = this.world.getBlockState(offPos);

        Block block = hitState.getBlock();
        if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(world, pos, hitState, Fluids.WATER)) {
            ((ILiquidContainer) block).receiveFluid(world, pos, hitState, Fluids.WATER.getStillFluidState(false));
        } else {
            if(hitState.getBlock() instanceof FlowingFluidBlock && ((FlowingFluidBlock)hitState.getBlock()).getFluid() == Fluids.LAVA) {
                Block fluidBlock = world.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                world.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(world, pos, pos, fluidBlock.getDefaultState()));
            } else {
                if(hitState.isReplaceable(Fluids.WATER)) {
                    this.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                } else if (offState.isReplaceable(Fluids.WATER) ) {
                    this.world.setBlockState(offPos, Blocks.WATER.getDefaultState());
                }
            }
        }
    }

    public void executeColdBehavior(BlockPos pos) {
        BlockState hitState = this.world.getBlockState(pos);
        if(hitState.getBlock() instanceof FlowingFluidBlock && ((FlowingFluidBlock)hitState.getBlock()).getFluid() == Fluids.WATER)
            this.world.setBlockState(pos, Blocks.ICE.getDefaultState());
        if(hitState.getBlock() instanceof IceBlock)
            this.world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
    }

    public void executeSnowBehavior(BlockPos pos, BlockPos offPos) {
        BlockState hitState = this.world.getBlockState(pos);
        BlockState offState = this.world.getBlockState(offPos);

        if(offState.getBlock() instanceof SnowBlock && offState.get(SnowBlock.LAYERS) < 8) {
            int layers = offState.get(SnowBlock.LAYERS);
            this.world.setBlockState(offPos, offState.getBlock().getDefaultState().with(SnowBlock.LAYERS, layers + 1));
        } else if(hitState.getBlock() instanceof SnowBlock && hitState.get(SnowBlock.LAYERS) < 8) {
            int layers = hitState.get(SnowBlock.LAYERS);
            this.world.setBlockState(pos, hitState.getBlock().getDefaultState().with(SnowBlock.LAYERS, layers + 1));
        } else {
            BlockState snowState = Blocks.SNOW.getDefaultState();
            if (offState.getMaterial().isReplaceable() && snowState.isValidPosition(world, offPos))
                this.world.setBlockState(offPos, snowState);
        }
    }

    public void shootSpell(Vector3d lookVec) {
        if(getSpellType() == 2) {
            this.setMotion(lookVec.inverse());
            this.accelerationX = -(lookVec.x * 0.1D);
            this.accelerationY = -(lookVec.y * 0.1D);
            this.accelerationZ = -(lookVec.z * 0.1D);
        } else {
            this.setMotion(lookVec);
            this.accelerationX = lookVec.x * 0.1D;
            this.accelerationY = lookVec.y * 0.1D;
            this.accelerationZ = lookVec.z * 0.1D;
        }
    }
}
