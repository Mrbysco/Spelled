package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractSpellEntity extends AbstractHurtingProjectile {
	private static final EntityDataAccessor<CompoundTag> SPELL_ORDER = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.COMPOUND_TAG);
	private static final EntityDataAccessor<Integer> SPELL_TYPE = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<OptionalInt> COLOR = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
	private static final EntityDataAccessor<Boolean> FIERY = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> LAVA = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> WATER = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> COLD = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SNOW = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SMOKY = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> INKY = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SILKY = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> SIZE_MULTIPLIER = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> POWER = SynchedEntityData.defineId(AbstractSpellEntity.class, EntityDataSerializers.INT);

	public AbstractSpellEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public AbstractSpellEntity(EntityType<? extends AbstractHurtingProjectile> entityType, LivingEntity shooter, Level level) {
		super(SpelledRegistry.SPELL.get(), level);
		this.setPos(shooter.getX(), shooter.getEyeY() - (double) 0.1F, shooter.getZ());
		this.setOwner(shooter);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SPELL_ORDER, new CompoundTag());
		this.entityData.define(SPELL_TYPE, 0);
		this.entityData.define(COLOR, OptionalInt.empty());
		this.entityData.define(FIERY, false);
		this.entityData.define(LAVA, false);
		this.entityData.define(WATER, false);
		this.entityData.define(COLD, false);
		this.entityData.define(SNOW, false);
		this.entityData.define(SMOKY, false);
		this.entityData.define(INKY, false);
		this.entityData.define(SILKY, false);
		this.entityData.define(SIZE_MULTIPLIER, 1.0F);
		this.entityData.define(POWER, 0);
	}

	public void setSpellOrder(CompoundTag order) {
		this.getEntityData().set(SPELL_ORDER, order);
	}

	public void insertAction(String action) {
		CompoundTag order = this.getSpellOrder();
		order.putString(order.isEmpty() ? String.valueOf(0) : String.valueOf(order.size()), action);
		this.setSpellOrder(order);
	}

	public CompoundTag getSpellOrder() {
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

	public boolean hasColor() {
		return this.getEntityData().get(COLOR).isPresent();
	}

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

	public void setSilky(boolean snow) {
		this.getEntityData().set(SILKY, snow);
	}

	public boolean isSilky() {
		return this.getEntityData().get(SILKY);
	}

	public void setSizeMultiplier(float sizeMultiplier) {
		this.getEntityData().set(SIZE_MULTIPLIER, sizeMultiplier);
		this.reapplyPosition();
		this.refreshDimensions();
	}

	public float getSizeMultiplier() {
		return this.getEntityData().get(SIZE_MULTIPLIER);
	}

	public float getSizeMultiplier(float max) {
		float sizeMultiplier = getSizeMultiplier();
		return sizeMultiplier <= max ? sizeMultiplier : max;
	}

	public void setPower(int power) {
		this.getEntityData().set(POWER, power);
	}

	public int getPower() {
		return this.getEntityData().get(POWER);
	}

	public void refreshDimensions() {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		super.refreshDimensions();
		this.setPos(d0, d1, d2);
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (SIZE_MULTIPLIER.equals(key)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(key);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return super.getDimensions(poseIn).scale(this.getSizeMultiplier(8.0F));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if (compound.contains("SpellOrder", 10))
			this.setSpellOrder(compound.getCompound("SpellOrder"));

		if (compound.contains("colorPresent"))
			setColor(compound.getInt("Color"));

		setFiery(compound.getBoolean("Fiery"));
		setLava(compound.getBoolean("Lava"));
		setWater(compound.getBoolean("Water"));
		setCold(compound.getBoolean("Cold"));
		setSnow(compound.getBoolean("Snow"));
		setSmoky(compound.getBoolean("Smoky"));
		setInky(compound.getBoolean("Inky"));
		setSilky(compound.getBoolean("Silky"));

		setSizeMultiplier(compound.getFloat("SizeMultiplier"));
		setPower(compound.getInt("PowerAdditive"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
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
		compound.putBoolean("Silky", isSilky());

		compound.putFloat("SizeMultiplier", getSizeMultiplier());
		compound.putInt("PowerAdditive", getPower());
	}

	@Override
	protected boolean shouldBurn() {
		return isFiery();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		if (isLava() && (isCold() || isSnow() || isWater())) {
			return new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
		} else {
			if (isWater() || (isFiery() && isSnow()))
				return ParticleTypes.DRIPPING_WATER;
			if (isSmoky())
				return ParticleTypes.SMOKE;
			if (isInky())
				return ParticleTypes.SQUID_INK;
			if (isFiery())
				return ParticleTypes.FLAME;
			if (isSnow())
				return ParticleTypes.ITEM_SNOWBALL;

			return ParticleTypes.WITCH;
		}
	}

	@Override
	public float getLightLevelDependentMagicValue() {
		return (isFiery() || isLava()) ? 1.0F : 0.5F;
	}

	@Override
	public void tick() {
		if (tickCount > 200) {
			this.discard();
		}

		if (isCold() || isWater()) {
			Entity entity = this.getOwner();
			if (this.level().isClientSide || (entity == null || entity.isAlive()) && this.level().hasChunkAt(this.blockPosition())) {
				HitResult result = rayTraceWater(this::canHitEntity);
				if (result.getType() != HitResult.Type.MISS) {
					this.onHit(result);
				}
			}
		}
		Vec3 vector3d = this.getDeltaMovement();
		this.updateRotation();
		this.setDeltaMovement(vector3d.scale((double) 0.99F));
		if (!this.isNoGravity()) {
			Vec3 vector3d1 = this.getDeltaMovement();
			this.setDeltaMovement(vector3d1.x, vector3d1.y - (double) 0.02F, vector3d1.z);
		}

		super.tick();
	}

	public HitResult rayTraceWater(Predicate<Entity> entityPredicate) {
		Vec3 deltaMovement = this.getDeltaMovement();
		Level level = this.level();
		Vec3 position = this.position();
		Vec3 vector3d2 = position.add(deltaMovement);
		HitResult raytraceresult = level.clip(new ClipContext(position, vector3d2, ClipContext.Block.COLLIDER, Fluid.SOURCE_ONLY, this));
		if (raytraceresult.getType() != HitResult.Type.MISS) {
			vector3d2 = raytraceresult.getLocation();
		}

		HitResult result = ProjectileUtil.getEntityHitResult(level, this, position, vector3d2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), entityPredicate);
		if (result != null) {
			raytraceresult = result;
		}

		return raytraceresult;
	}

	public void explode() {
		boolean flag = !(isSnow() || isWater()) && isFiery();
		int size = (int) Math.ceil(1 * getSizeMultiplier());
		this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) size, flag, flag ? Level.ExplosionInteraction.NONE : Level.ExplosionInteraction.BLOCK);
	}

	public List<BlockPos> getSizedPos(BlockPos pos) {
		if (getSizeMultiplier() > 1) {
			int offset = Math.round(getSizeMultiplier(16F) * 0.5f);
			List<BlockPos> positionList = BlockPos.betweenClosedStream(
					pos.offset(-offset, -offset, -offset),
					pos.offset(offset, offset, offset)).map(BlockPos::immutable).collect(Collectors.toList());
			return positionList;
		}
		return Collections.singletonList(pos);
	}

	public List<Entity> getRangedEntities(Entity hitEntity) {
		if (getSizeMultiplier() > 1) {
			double offset = getSizeMultiplier(16F);
			AABB hitbox = new AABB(hitEntity.getX() - 0.5f, hitEntity.getY() - 0.5f, hitEntity.getZ() - 0.5f, hitEntity.getX() + 0.5f, hitEntity.getY() + 0.5f, hitEntity.getZ() + 0.5f)
					.expandTowards(-offset, -offset, -offset).expandTowards(offset, offset, offset);

			return this.level().getEntities(this, hitbox, Entity::isAlive);
		}
		return Collections.singletonList(hitEntity);
	}
}
