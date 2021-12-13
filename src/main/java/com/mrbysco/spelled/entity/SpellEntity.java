package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.behavior.ISpellBehavior;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

import java.util.HashMap;
import java.util.List;

public class SpellEntity extends AbstractSpellEntity {
    public SpellEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SpellEntity(LivingEntity shooter, Level worldIn) {
        super(SpelledRegistry.SPELL.get(), shooter, worldIn);
    }

    public SpellEntity(SpawnEntity spawnEntity, Level worldIn) {
        this(SpelledRegistry.SPELL.get(), worldIn);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level.isClientSide) {
            super.onHit(result);
            this.level.addParticle(getTrailParticle(), this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityResult) {
        super.onHitEntity(entityResult);
        Entity hitEntity = entityResult.getEntity();
        this.handleEntityHit(hitEntity);
    }

    public void handleEntityHit(Entity hitEntity) {
        List<Entity> rangedEntities = getRangedEntities(hitEntity);
        HashMap<String, ISpellBehavior> behaviors = BehaviorRegistry.instance().getBehaviors();

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            ISpellBehavior behavior = behaviors.get(action);
            if(behavior != null) {
                if(behavior.appliedMultiple()) {
                    for(Entity entity : rangedEntities) {
                        behavior.onEntityHit(this, entity);
                    }
                } else {
                    behavior.onEntityHit(this, hitEntity);
                }
            }
        }
    }

    //On block hit
    @Override
    protected void onHitBlock(BlockHitResult blockResult) {
        super.onHitBlock(blockResult);
        BlockPos pos = blockResult.getBlockPos();
        List<BlockPos> multiplePos = getSizedPos(pos);
        HashMap<String, ISpellBehavior> behaviors = BehaviorRegistry.instance().getBehaviors();

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            ISpellBehavior behavior = behaviors.get(action);
            if(behavior != null) {
                if(behavior.appliedMultiple()) {
                    for(BlockPos boxPos : multiplePos) {
                        behavior.onBlockHit(this, boxPos, boxPos.relative(blockResult.getDirection()));
                    }
                } else {
                    behavior.onBlockHit(this, pos, pos.relative(blockResult.getDirection()));
                }
            }
        }
    }
}
