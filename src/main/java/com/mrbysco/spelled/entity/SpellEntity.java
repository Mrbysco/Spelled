package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.Reference;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.OptionalInt;

public class SpellEntity extends DamagingProjectileEntity {
    private static final DataParameter<OptionalInt> COLOR = EntityDataManager.createKey(SpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Boolean> FIERY = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAVA = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WATER = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> COLD = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SNOW = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HARVEST = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SMOKY = EntityDataManager.createKey(SpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<OptionalInt> HEALING = EntityDataManager.createKey(SpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<OptionalInt> DAMAGE = EntityDataManager.createKey(SpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<OptionalInt> KNOCKBACK = EntityDataManager.createKey(SpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Float> SIZE_MULTIPLIER = EntityDataManager.createKey(SpellEntity.class, DataSerializers.FLOAT);

    public SpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(COLOR, OptionalInt.empty());
        this.dataManager.register(FIERY, false);
        this.dataManager.register(LAVA, false);
        this.dataManager.register(WATER, false);
        this.dataManager.register(COLD, false);
        this.dataManager.register(SNOW, false);
        this.dataManager.register(EXPLODING, false);
        this.dataManager.register(HARVEST, false);
        this.dataManager.register(SMOKY, false);
        this.dataManager.register(HEALING, OptionalInt.empty());
        this.dataManager.register(DAMAGE, OptionalInt.empty());
        this.dataManager.register(SIZE_MULTIPLIER, 1.0F);
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
            if(isFiery())
                return ParticleTypes.FLAME;
            if(isSnow())
                return ParticleTypes.ITEM_SNOWBALL;

            return ParticleTypes.AMBIENT_ENTITY_EFFECT;
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
    }

    public void explode() {
        if(doesExplode()) {
            boolean flag = isSnow();
            this.world.createExplosion((Entity)null, this.getPosX(), this.getPosY(), this.getPosZ(), (float)1, !flag, !flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            explode();
            this.remove();
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult entityResult) {
        super.onEntityHit(entityResult);
        Entity hitEntity = entityResult.getEntity();
        if(isFiery() || isLava()) {
            hitEntity.setFire(5);
        }

        if(doesHurt()) {
            float damage = (float)getDamage().getAsInt();
            hitEntity.attackEntityFrom(Reference.causeMagicDamage(this), damage);
        }

        if(hasKnockback()) {
            int knockback = getKnockback().getAsInt();
            Vector3d vector3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)knockback * 0.6D);
            if (vector3d.lengthSquared() > 0.0D) {
                hitEntity.addVelocity(vector3d.x, 0.1D, vector3d.z);
            }
        }

        if(isHealing() && hitEntity instanceof LivingEntity) {
            float healAmount = (float)getHealingFactor().getAsInt();
            ((LivingEntity)hitEntity).heal(healAmount);
        }

        if(isSmoky()) {
            //Smoke em out!
            ((LivingEntity)hitEntity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 5*20));
        }
    }

    //On block hit
    @Override
    protected void func_230299_a_(BlockRayTraceResult blockResult) {
        super.func_230299_a_(blockResult);
        BlockState hitState = this.world.getBlockState(blockResult.getPos());
        BlockPos pos = blockResult.getPos();

        if(isFiery() && !isLava()) {
            BlockPos blockpos = pos.offset(blockResult.getFace());
            if (this.world.isAirBlock(blockpos)) {
                this.world.setBlockState(blockpos, AbstractFireBlock.getFireForPlacement(this.world, blockpos));
            }
        }

        if(isLava()) {
            BlockPos blockpos = pos.offset(blockResult.getFace());
            if (this.world.isAirBlock(blockpos)) {
                this.world.setBlockState(blockpos, Blocks.LAVA.getDefaultState());
            }
        }

        if(isWater()) {
            BlockPos blockpos = pos.offset(blockResult.getFace());
            Block block = hitState.getBlock();
            if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(world, pos, hitState, Fluids.WATER)) {
                ((ILiquidContainer) block).receiveFluid(world, pos, hitState, Fluids.WATER.getStillFluidState(false));
            } else {
                if (this.world.isAirBlock(blockpos)) {
                    this.world.setBlockState(blockpos, Blocks.WATER.getDefaultState());
                }
            }
        }

        if(isSnow()) {
            BlockPos blockpos = pos.offset(blockResult.getFace());
            if (this.world.isAirBlock(blockpos)) {
                this.world.setBlockState(blockpos, Blocks.SNOW.getDefaultState());
            }
        }

        if(doesHarvest()) {
            if(hitState.getHarvestLevel() <= 2 && !world.isRemote) {
                world.destroyBlock(pos, true);
            }
        }
    }

    public void shootSpell(Vector3d lookVec) {
        this.setMotion(lookVec);
        this.accelerationX = lookVec.x * 0.1D;
        this.accelerationY = lookVec.y * 0.1D;
        this.accelerationZ = lookVec.z * 0.1D;
    }
}
