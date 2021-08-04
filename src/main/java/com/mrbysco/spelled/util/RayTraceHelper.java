package com.mrbysco.spelled.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/*
 * Credits go to the MKCore repository from ChaosBuffalo
 */

public class RayTraceHelper {

    private static final Predicate<Entity> defaultFilter = e -> EntitySelector.ENTITY_STILL_ALIVE.test(e) && EntitySelector.NO_SPECTATORS.test(e);

    public static <E extends Entity> HitResult getLookingAt(Class<E> clazz, final Entity mainEntity, double distance, final Predicate<E> entityPredicate) {
        Predicate<E> finalFilter = e -> e != mainEntity && defaultFilter.test(e) && e.isPickable() && entityPredicate.test(e);
        HitResult position = null;
        if (mainEntity.level != null) {
            Vec3 look = mainEntity.getLookAngle().scale(distance);
            Vec3 from = mainEntity.position().add(0, mainEntity.getEyeHeight(), 0);
            Vec3 to = from.add(look);
            position = rayTraceBlocksAndEntities(clazz, mainEntity, from, to, finalFilter);
        }
        return position;
    }

    public static BlockHitResult rayTraceBlocks(Entity entity, Vec3 from, Vec3 to) {
        ClipContext context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        return entity.getCommandSenderWorld().clip(context);
    }

    public static <E extends Entity> EntityHitResult rayTraceEntities(Class<E> clazz, Level world, Vec3 from, Vec3 to, Vec3 aaExpansion, float aaGrowth,
                                                                           float entityExpansion, final Predicate<E> filter) {

        Predicate<E> predicate = input -> defaultFilter.test(input) && filter.test(input);

        Entity nearest = null;
        double distance = 0;

        AABB bb = new AABB(new BlockPos(from), new BlockPos(to))
                .expandTowards(aaExpansion.x, aaExpansion.y, aaExpansion.z)
                .inflate(aaGrowth);
        List<E> entities = world.getEntitiesOfClass(clazz, bb, predicate);
        for (Entity entity : entities) {
            AABB entityBB = entity.getBoundingBox().inflate(entityExpansion);
            Optional<Vec3> intercept = entityBB.clip(from, to);
            if (intercept.isPresent()) {
                double dist = from.distanceTo(intercept.get());
                if (dist < distance || distance == 0.0D) {
                    nearest = entity;
                    distance = dist;
                }
            }
        }

        if (nearest != null)
            return new EntityHitResult(nearest);
        return null;
    }

    private static <E extends Entity> HitResult rayTraceBlocksAndEntities(Class<E> clazz, Entity mainEntity, Vec3 from, Vec3 to,final Predicate<E> entityFilter) {
        BlockHitResult block = rayTraceBlocks(mainEntity, from, to);
        if (block.getType() == HitResult.Type.BLOCK)
            to = block.getLocation();

        EntityHitResult entity = rayTraceEntities(clazz, mainEntity.getCommandSenderWorld(), from, to, Vec3.ZERO, 0.5f, 0.5f, entityFilter);

        if (block.getType() == HitResult.Type.MISS) {
            return entity;
        } else {
            if (entity == null) {
                return block;
            } else {
                double blockDist = block.getLocation().distanceTo(from);
                double entityDist = entity.getLocation().distanceTo(from);
                if (blockDist < entityDist) {
                    return block;
                } else {
                    return entity;
                }
            }
        }
    }
}