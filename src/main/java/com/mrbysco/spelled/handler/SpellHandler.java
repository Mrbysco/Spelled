package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpellHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void projectileImpact(ProjectileImpactEvent.Fireball event) {
        if(event.getFireball() instanceof SpellEntity) {
            SpellEntity spell = (SpellEntity)event.getFireball();
            if(spell.isCold() || spell.isWater()) {
                event.setCanceled(true);
            }
        }
    }
}
