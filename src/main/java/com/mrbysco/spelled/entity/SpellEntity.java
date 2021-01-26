package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.List;

public class SpellEntity extends AbstractSpellEntity {
    public SpellEntity(EntityType<? extends DamagingProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SpellEntity(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(SpelledRegistry.SPELL.get(), worldIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            super.onImpact(result);
            explode();
            this.world.addParticle(getParticle(), this.getPosX(), this.getPosY(), this.getPosZ(), 1.0D, 0.0D, 0.0D);
            this.remove();
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult entityResult) {
        super.onEntityHit(entityResult);
        Entity hitEntity = entityResult.getEntity();
        this.handleEntityHit(hitEntity);
    }

    public void handleEntityHit(Entity hitEntity) {
        List<Entity> rangedEntities = getRangedEntities(hitEntity);

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            switch (action) {
                case "fire":
                case "lava":
                    for(Entity entity : rangedEntities) {
                        entity.setFire(5);
                    }
                    break;
                case "hurt":
                    for(Entity entity : rangedEntities) {
                        entity.attackEntityFrom(Reference.causeMagicDamage(this), 1);
                    }
                    break;
                case "knockback":
                    Vector3d vector3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)1 * 0.6D);
                    for(Entity entity : rangedEntities) {
                        if (vector3d.lengthSquared() > 0.0D)
                            entity.addVelocity(vector3d.x, 0.1D, vector3d.z);
                    }
                    break;
                case "heal":
                    for(Entity entity : rangedEntities) {
                        if(entity instanceof LivingEntity) {
                            ((LivingEntity)entity).heal(1.0F);
                        }
                    }
                    break;
                case "smoke":
                case "ink":
                    for(Entity entity : rangedEntities) {
                        if(entity instanceof LivingEntity) {
                            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 5*20));
                        }
                    }
                    break;
                case "cold":
                    for(Entity entity : rangedEntities) {
                        if(entity instanceof LivingEntity) {
                            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 4*20));
                        }
                    }
                    break;
                case "snow":
                    for(Entity entity : rangedEntities) {
                        if(entity instanceof LivingEntity) {
                            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 1*20));
                        }
                    }
                    break;
            }
        }
    }

    //On block hit
    @Override
    protected void func_230299_a_(BlockRayTraceResult blockResult) {
        super.func_230299_a_(blockResult);
        BlockPos pos = blockResult.getPos();
        List<BlockPos> multiplePos = getSizedPos(pos);

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            switch (action) {
                case "cold":
                    for(BlockPos boxPos : multiplePos) {
                        executeColdBehavior(boxPos);
                    }
                    break;
                case "harvest":
                    for(BlockPos boxPos : multiplePos) {
                        executeBreakBehavior(boxPos);
                    }
                    break;
                case "fire":
                    for(BlockPos boxPos : multiplePos) {
                        executeFireBehavior(boxPos.offset(blockResult.getFace()));
                    }
                    break;
                case "lava":
                    for(BlockPos boxPos : multiplePos) {
                        executeLavaBehavior(boxPos.offset(blockResult.getFace()));
                    }
                    break;
                case "water":
                    if(!this.world.getDimensionType().isUltrawarm()) {
                        for(BlockPos boxPos : multiplePos) {
                            executeWaterBehavior(boxPos, boxPos.offset(blockResult.getFace()));
                        }
                    }
                    break;
                case "snow":
                    for(BlockPos boxPos : multiplePos) {
                        executeSnowBehavior(boxPos, boxPos.offset(blockResult.getFace()));
                    }
                    break;
            }
        }
    }
}
