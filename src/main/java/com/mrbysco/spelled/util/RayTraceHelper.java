package com.mrbysco.spelled.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/*
 * Credits go to the MKCore repository from ChaosBuffalo
 */

public class RayTraceHelper {

    private static final Predicate<Entity> defaultFilter = e -> EntityPredicates.IS_ALIVE.test(e) && EntityPredicates.NOT_SPECTATING.test(e);

    public static <E extends Entity> RayTraceResult getLookingAt(Class<E> clazz, final Entity mainEntity, double distance, final Predicate<E> entityPredicate) {
        Predicate<E> finalFilter = e -> e != mainEntity && defaultFilter.test(e) && e.canBeCollidedWith() && entityPredicate.test(e);
        RayTraceResult position = null;
        if (mainEntity.world != null) {
            Vector3d look = mainEntity.getLookVec().scale(distance);
            Vector3d from = mainEntity.getPositionVec().add(0, mainEntity.getEyeHeight(), 0);
            Vector3d to = from.add(look);
            position = rayTraceBlocksAndEntities(clazz, mainEntity, from, to, finalFilter);
        }
        return position;
    }

    public static BlockRayTraceResult rayTraceBlocks(Entity entity, Vector3d from, Vector3d to) {
        RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity);
        return entity.getEntityWorld().rayTraceBlocks(context);
    }

    public static <E extends Entity> EntityRayTraceResult rayTraceEntities(Class<E> clazz, World world, Vector3d from, Vector3d to, Vector3d aaExpansion, float aaGrowth,
                                                                           float entityExpansion, final Predicate<E> filter) {

        Predicate<E> predicate = input -> defaultFilter.test(input) && filter.test(input);

        Entity nearest = null;
        double distance = 0;

        AxisAlignedBB bb = new AxisAlignedBB(new BlockPos(from), new BlockPos(to))
                .expand(aaExpansion.x, aaExpansion.y, aaExpansion.z)
                .grow(aaGrowth);
        List<E> entities = world.getEntitiesWithinAABB(clazz, bb, predicate);
        for (Entity entity : entities) {
            AxisAlignedBB entityBB = entity.getBoundingBox().grow(entityExpansion);
            Optional<Vector3d> intercept = entityBB.rayTrace(from, to);
            if (intercept.isPresent()) {
                double dist = from.distanceTo(intercept.get());
                if (dist < distance || distance == 0.0D) {
                    nearest = entity;
                    distance = dist;
                }
            }
        }

        if (nearest != null)
            return new EntityRayTraceResult(nearest);
        return null;
    }

    private static <E extends Entity> RayTraceResult rayTraceBlocksAndEntities(Class<E> clazz, Entity mainEntity, Vector3d from, Vector3d to,final Predicate<E> entityFilter) {
        BlockRayTraceResult block = rayTraceBlocks(mainEntity, from, to);
        if (block.getType() == RayTraceResult.Type.BLOCK)
            to = block.getHitVec();

        EntityRayTraceResult entity = rayTraceEntities(clazz, mainEntity.getEntityWorld(), from, to, Vector3d.ZERO, 0.5f, 0.5f, entityFilter);

        if (block.getType() == RayTraceResult.Type.MISS) {
            return entity;
        } else {
            if (entity == null) {
                return block;
            } else {
                double blockDist = block.getHitVec().distanceTo(from);
                double entityDist = entity.getHitVec().distanceTo(from);
                if (blockDist < entityDist) {
                    return block;
                } else {
                    return entity;
                }
            }
        }
    }
}