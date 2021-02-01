package com.mrbysco.spelled.entity;

import com.mrbysco.spelled.api.behavior.BehaviorRegistry;
import com.mrbysco.spelled.api.behavior.ISpellBehavior;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.HashMap;
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
        HashMap<String, ISpellBehavior> behaviors = BehaviorRegistry.instance().getBehaviors();

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            ISpellBehavior behavior = behaviors.get(action);
            if(behavior != null) {
                for(Entity entity : rangedEntities) {
                    behavior.onEntityHit(this, entity);
                }
            }
        }
    }

    //On block hit
    @Override
    protected void func_230299_a_(BlockRayTraceResult blockResult) {
        super.func_230299_a_(blockResult);
        BlockPos pos = blockResult.getPos();
        List<BlockPos> multiplePos = getSizedPos(pos);
        HashMap<String, ISpellBehavior> behaviors = BehaviorRegistry.instance().getBehaviors();

        for(int i = 0; i < getSpellOrder().size(); i++) {
            String action = getSpellOrder().getString(String.valueOf(i));
            ISpellBehavior behavior = behaviors.get(action);
            if(behavior != null) {
                for(BlockPos boxPos : multiplePos) {
                    behavior.onBlockHit(this, boxPos, boxPos.offset(blockResult.getFace()));
                }
            }
        }
    }
}
