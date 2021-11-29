package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpellHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void projectileImpact(ProjectileImpactEvent event) {
        if(event.getProjectile() instanceof SpellEntity) {
            SpellEntity spell = (SpellEntity)event.getProjectile();
            if(spell.isCold() || spell.isWater()) {
                event.setCanceled(true);
            }
        }
    }
}
