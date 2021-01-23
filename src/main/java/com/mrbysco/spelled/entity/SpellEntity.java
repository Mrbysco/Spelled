package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.block.BlockState;
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

        if(isSmoky() || isInky()) {
            //Smoke em out!
            ((LivingEntity)hitEntity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 5*20));
        }

        if(isCold()) {
            ((LivingEntity)hitEntity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 4*20));
        }

        if(isSnow()) {
            ((LivingEntity)hitEntity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 1*20));
        }
    }

    //On block hit
    @Override
    protected void func_230299_a_(BlockRayTraceResult blockResult) {
        super.func_230299_a_(blockResult);
        BlockPos pos = blockResult.getPos();
        BlockState hitState = this.world.getBlockState(pos);
        BlockPos offPos = pos.offset(blockResult.getFace());
        Iterable<BlockPos> multiplePos = getSizedPos(pos.offset(blockResult.getFace().getOpposite()));

        if(isCold()) {
            if(getSizeMultiplier() > 1) {
                for(BlockPos boxPos : multiplePos) {
                    executeColdBehavior(boxPos);
                }
            } else {
                executeColdBehavior(pos);
            }
        }

        if(doesHarvest()) {
            if(getSizeMultiplier() > 1) {
                for(BlockPos boxPos : multiplePos) {
                    executeBreakBehavior(boxPos);
                }
            } else {
                executeBreakBehavior(pos);
            }
        }

        if(isFiery() && !isLava()) {
            if(getSizeMultiplier() > 1) {
                for(BlockPos boxPos : multiplePos) {
                    executeFireBehavior(boxPos.offset(blockResult.getFace()));
                }
            } else {
                executeFireBehavior(offPos);
            }
        }

        if(isLava()) {
            if(getSizeMultiplier() > 1) {
                for(BlockPos boxPos : multiplePos) {
                    executeLavaBehavior(boxPos.offset(blockResult.getFace()));
                }
            } else {
                executeLavaBehavior(offPos);
            }
        }

        if(isWater() && !this.world.getDimensionType().isUltrawarm()) {
            if(getSizeMultiplier() > 1) {
                for(BlockPos boxPos : multiplePos) {
                    executeWaterBehavior(boxPos, boxPos.offset(blockResult.getFace()));
                }
            } else {
                executeWaterBehavior(pos, offPos);
            }
        }

        if(isSnow()) {
            if(hitState.getHarvestLevel() <= 2) {
                if(getSizeMultiplier() > 1) {
                    for(BlockPos boxPos : multiplePos) {
                        executeSnowBehavior(boxPos, boxPos.offset(blockResult.getFace()));
                    }
                } else {
                    executeSnowBehavior(pos, offPos);
                }
            }
        }
    }
}
