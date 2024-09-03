package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.entity.SpellEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public class SpellHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void projectileImpact(ProjectileImpactEvent event) {
		if (event.getProjectile() instanceof SpellEntity spell) {
			if (spell.isCold() || spell.isWater()) {
				event.setCanceled(true);
			}
		}
	}
}
