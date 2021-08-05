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
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public abstract class AbstractSpellEntity extends DamagingProjectileEntity {
    private static final DataParameter<CompoundNBT> SPELL_ORDER = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.COMPOUND_NBT);
    private static final DataParameter<Integer> SPELL_TYPE = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.VARINT);
    private static final DataParameter<OptionalInt> COLOR = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FIERY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAVA = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WATER = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> COLD = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SNOW = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SMOKY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> INKY = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SIZE_MULTIPLIER = EntityDataManager.createKey(AbstractSpellEntity.class, DataSerializers.FLOAT);

    public AbstractSpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, World worldIn) {
        super(entityType, worldIn);
    }

    public AbstractSpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, LivingEntity shooter, World worldIn) {
        super(SpelledRegistry.SPELL.get(), worldIn);
        this.setPosition(shooter.getPosX(), shooter.getPosYEye() - (double)0.1F, shooter.getPosZ());
        this.setShooter(shooter);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SPELL_ORDER, new CompoundNBT());
        this.dataManager.register(SPELL_TYPE, 0);
        this.dataManager.register(COLOR, OptionalInt.empty());
        this.dataManager.register(FIERY, false);
        this.dataManager.register(LAVA, false);
        this.dataManager.register(WATER, false);
        this.dataManager.register(COLD, false);
        this.dataManager.register(SNOW, false);
        this.dataManager.register(EXPLODING, false);
        this.dataManager.register(SMOKY, false);
        this.dataManager.register(INKY, false);
        this.dataManager.register(SIZE_MULTIPLIER, 1.0F);
    }

    public void setSpellOrder(CompoundNBT order) {
        this.getDataManager().set(SPELL_ORDER, order);
    }
    public void insertAction(String action) {
        CompoundNBT order = this.getSpellOrder();
        order.putString(order.isEmpty() ? String.valueOf(0) : String.valueOf(order.size()), action);
        this.setSpellOrder(order);
    }
    public CompoundNBT getSpellOrder() {
        return this.getDataManager().get(SPELL_ORDER);
    }

    public void setSpellType(int type) {
        this.getDataManager().set(SPELL_TYPE, type);
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

        if (compound.contains("SpellOrder", 10))
            this.setSpellOrder(compound.getCompound("SpellOrder"));

        if(compound.contains("colorPresent"))
            setColor(compound.getInt("Color"));

        setFiery(compound.getBoolean("Fiery"));
        setLava(compound.getBoolean("Lava"));
        setWater(compound.getBoolean("Water"));
        setCold(compound.getBoolean("Cold"));
        setSnow(compound.getBoolean("Snow"));
        setExploding(compound.getBoolean("Exploding"));
        setSmoky(compound.getBoolean("Smoky"));
        setInky(compound.getBoolean("Inky"));

        setSizeMultiplier(compound.getFloat("SizeMultiplier"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

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
        compound.putBoolean("Exploding", doesExplode());
        compound.putBoolean("Smoky", isSmoky());
        compound.putBoolean("Inky", isInky());

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
        if (ticksExisted > 200) {
            this.remove();
        }

        if(isCold() || isWater()) {
            Entity entity = this.getShooter();
            if (this.world.isRemote || (entity == null || entity.isAlive()) && this.world.isBlockLoaded(this.getPosition())) {
                RayTraceResult raytraceresult = rayTraceWater(this::func_230298_a_);
                if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                    this.onImpact(raytraceresult);
                }
            }
        }
        Vector3d vector3d = this.getMotion();
        this.updatePitchAndYaw();
        this.setMotion(vector3d.scale((double)0.99F));
        if (!this.hasNoGravity()) {
            Vector3d vector3d1 = this.getMotion();
            this.setMotion(vector3d1.x, vector3d1.y - (double)0.02F, vector3d1.z);
        }

        super.tick();
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

    public List<BlockPos> getSizedPos(BlockPos pos) {
        if(getSizeMultiplier() > 1) {
            double offset = getSizeMultiplier() * 0.5f;
            List<BlockPos> positionList = new ArrayList<>();
            Iterable<BlockPos> positions = BlockPos.getAllInBoxMutable(pos.add(-offset, -offset, -offset), pos.add(offset, offset, offset));
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
            AxisAlignedBB hitbox = new AxisAlignedBB(hitEntity.getPosX() - 0.5f, hitEntity.getPosY() - 0.5f, hitEntity.getPosZ() - 0.5f, hitEntity.getPosX() + 0.5f, hitEntity.getPosY() + 0.5f, hitEntity.getPosZ() + 0.5f)
                    .expand(-offset, -offset, -offset).expand(offset, offset, offset);

            return world.getEntitiesInAABBexcluding(this, hitbox, Entity::isAlive);
        }
        return Collections.singletonList(hitEntity);
    }

    public void shootSpell(Vector3d lookVec) {
        this.setMotion(lookVec);
        this.accelerationX = lookVec.x * 0.1D;
        this.accelerationY = lookVec.y * 0.1D;
        this.accelerationZ = lookVec.z * 0.1D;
    }
}
