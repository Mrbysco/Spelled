package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public abstract class AbstractSpellEntity extends DamagingProjectileEntity {
    private static final DataParameter<CompoundNBT> SPELL_ORDER = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<Integer> SPELL_TYPE = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.INT);
    private static final DataParameter<OptionalInt> COLOR = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final DataParameter<Boolean> FIERY = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAVA = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WATER = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> COLD = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SNOW = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SMOKY = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> INKY = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SIZE_MULTIPLIER = EntityDataManager.defineId(AbstractSpellEntity.class, DataSerializers.FLOAT);

    public AbstractSpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, World worldIn) {
        super(entityType, worldIn);
    }

    public AbstractSpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, LivingEntity shooter, World worldIn) {
        super(SpelledRegistry.SPELL.get(), worldIn);
        this.setPos(shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ());
        this.setOwner(shooter);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL_ORDER, new CompoundNBT());
        this.entityData.define(SPELL_TYPE, 0);
        this.entityData.define(COLOR, OptionalInt.empty());
        this.entityData.define(FIERY, false);
        this.entityData.define(LAVA, false);
        this.entityData.define(WATER, false);
        this.entityData.define(COLD, false);
        this.entityData.define(SNOW, false);
        this.entityData.define(SMOKY, false);
        this.entityData.define(INKY, false);
        this.entityData.define(SIZE_MULTIPLIER, 1.0F);
    }

    public void setSpellOrder(CompoundNBT order) {
        this.getEntityData().set(SPELL_ORDER, order);
    }
    public void insertAction(String action) {
        CompoundNBT order = this.getSpellOrder();
        order.putString(order.isEmpty() ? String.valueOf(0) : String.valueOf(order.size()), action);
        this.setSpellOrder(order);
    }
    public CompoundNBT getSpellOrder() {
        return this.getEntityData().get(SPELL_ORDER);
    }

    public void setSpellType(int type) {
        this.getEntityData().set(SPELL_TYPE, type);
    }
    public int getSpellType() {
        return this.getEntityData().get(SPELL_TYPE);
    }

    public void setColor(int color) {
        this.getEntityData().set(COLOR, OptionalInt.of(color));
    }
    public OptionalInt getColor() {
        return this.getEntityData().get(COLOR);
    }
    public boolean hasColor() { return this.getEntityData().get(COLOR).isPresent(); }

    public void setFiery(boolean fiery) {
        this.getEntityData().set(FIERY, fiery);
    }
    public boolean isFiery() {
        return this.getEntityData().get(FIERY);
    }

    public void setLava(boolean lava) {
        this.getEntityData().set(LAVA, lava);
    }
    public boolean isLava() {
        return this.getEntityData().get(LAVA);
    }

    public void setWater(boolean water) {
        this.getEntityData().set(WATER, water);
    }
    public boolean isWater() {
        return this.getEntityData().get(WATER);
    }

    public void setCold(boolean cold) {
        this.getEntityData().set(COLD, cold);
    }
    public boolean isCold() {
        return this.getEntityData().get(COLD);
    }

    public void setSnow(boolean snow) {
        this.getEntityData().set(SNOW, snow);
    }
    public boolean isSnow() {
        return this.getEntityData().get(SNOW);
    }

    public void setSmoky(boolean smoky) {
        this.getEntityData().set(SMOKY, smoky);
    }
    public boolean isSmoky() {
        return this.getEntityData().get(SMOKY);
    }

    public void setInky(boolean inky) {
        this.getEntityData().set(INKY, inky);
    }
    public boolean isInky() {
        return this.getEntityData().get(INKY);
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.getEntityData().set(SIZE_MULTIPLIER, sizeMultiplier);
        this.reapplyPosition();
        this.refreshDimensions();
    }
    public float getSizeMultiplier() {
        return this.getEntityData().get(SIZE_MULTIPLIER);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (SIZE_MULTIPLIER.equals(key)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntitySize getDimensions(Pose poseIn) {
        return super.getDimensions(poseIn).scale(this.getSizeMultiplier());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("SpellOrder", 10))
            this.setSpellOrder(compound.getCompound("SpellOrder"));

        if(compound.contains("colorPresent"))
            setColor(compound.getInt("Color"));

        setFiery(compound.getBoolean("Fiery"));
        setLava(compound.getBoolean("Lava"));
        setWater(compound.getBoolean("Water"));
        setCold(compound.getBoolean("Cold"));
        setSnow(compound.getBoolean("Snow"));
        setSmoky(compound.getBoolean("Smoky"));
        setInky(compound.getBoolean("Inky"));

        setSizeMultiplier(compound.getFloat("SizeMultiplier"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);

        if (!this.getSpellOrder().isEmpty())
            compound.put("SpellOrder", this.getSpellOrder());

        compound.putBoolean("colorPresent", hasColor());
        if (hasColor())
            compound.putInt("Color", getColor().getAsInt());

        compound.putBoolean("Fiery", isFiery());
        compound.putBoolean("Lava", isLava());
        compound.putBoolean("Water", isWater());
        compound.putBoolean("Cold", isCold());
        compound.putBoolean("Snow", isSnow());
        compound.putBoolean("Smoky", isSmoky());
        compound.putBoolean("Inky", isInky());

        compound.putFloat("SizeMultiplier", getSizeMultiplier());
    }

    @Override
    protected boolean shouldBurn() {
        return isFiery();
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected IParticleData getTrailParticle() {
        if(isLava() && (isCold() || isSnow() || isWater())) {
            return new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
        } else {
            if(isWater() || (isFiery() && isSnow()))
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
        if (tickCount > 200) {
            this.remove();
        }

        if(isCold() || isWater()) {
            Entity entity = this.getOwner();
            if (this.level.isClientSide || (entity == null || entity.isAlive()) && this.level.hasChunkAt(this.blockPosition())) {
                RayTraceResult raytraceresult = rayTraceWater(this::canHitEntity);
                if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                    this.onHit(raytraceresult);
                }
            }
        }
        Vector3d vector3d = this.getDeltaMovement();
        this.updateRotation();
        this.setDeltaMovement(vector3d.scale((double)0.99F));
        if (!this.isNoGravity()) {
            Vector3d vector3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d1.x, vector3d1.y - (double)0.02F, vector3d1.z);
        }

        super.tick();
    }

    public RayTraceResult rayTraceWater(Predicate<Entity> entityPredicate) {
        Vector3d vector3d = this.getDeltaMovement();
        World world = this.level;
        Vector3d vector3d1 = this.position();
        Vector3d vector3d2 = vector3d1.add(vector3d);
        RayTraceResult raytraceresult = world.clip(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, FluidMode.SOURCE_ONLY, this));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d2 = raytraceresult.getLocation();
        }

        RayTraceResult raytraceresult1 = ProjectileHelper.getEntityHitResult(world, this, vector3d1, vector3d2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), entityPredicate);
        if (raytraceresult1 != null) {
            raytraceresult = raytraceresult1;
        }

        return raytraceresult;
    }

    public void explode() {
        boolean flag = !(isSnow() || isWater()) && isFiery();
        int size = (int) Math.ceil(1 * getSizeMultiplier());
        this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float) size, flag, flag ? Mode.DESTROY : Mode.BREAK);
    }

    public List<BlockPos> getSizedPos(BlockPos pos) {
        if(getSizeMultiplier() > 1) {
            double offset = getSizeMultiplier() * 0.5f;
            List<BlockPos> positionList = new ArrayList<>();
            Iterable<BlockPos> positions = BlockPos.betweenClosed(pos.offset(-offset, -offset, -offset), pos.offset(offset, offset, offset));
            for(BlockPos position : positions) {
                if(!positionList.contains(position)) {
                    positionList.add(new BlockPos(position));
                }
            }
            return positionList;
        }
        return Collections.singletonList(pos);
    }

    public List<Entity> getRangedEntities(Entity hitEntity) {
        if(getSizeMultiplier() > 1) {
            double offset = getSizeMultiplier();
            AxisAlignedBB hitbox = new AxisAlignedBB(hitEntity.getX() - 0.5f, hitEntity.getY() - 0.5f, hitEntity.getZ() - 0.5f, hitEntity.getX() + 0.5f, hitEntity.getY() + 0.5f, hitEntity.getZ() + 0.5f)
                    .expandTowards(-offset, -offset, -offset).expandTowards(offset, offset, offset);

            return level.getEntities(this, hitbox, Entity::isAlive);
        }
        return Collections.singletonList(hitEntity);
    }

    public void shootSpell(Vector3d lookVec) {
        this.setDeltaMovement(lookVec);
        this.xPower = lookVec.x * 0.1D;
        this.yPower = lookVec.y * 0.1D;
        this.zPower = lookVec.z * 0.1D;
    }
}
