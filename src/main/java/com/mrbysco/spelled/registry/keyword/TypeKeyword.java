package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TypeKeyword extends BaseKeyword {
    private final String TYPE_KEY = "SpellType";
    private final Type type;

    public TypeKeyword(String keyword, Type type, int level, int slots) {
        super(keyword, level, slots);
        this.type = type;
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            CompoundNBT nbt = spell.getPersistentData();
            if(!nbt.contains(TYPE_KEY)) {
                nbt.putString(TYPE_KEY, type.getName());
                spell.read(nbt);
            }
        }
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BALL("ball"),
        PROJECTILE("projectile"),
        SELF("self");

        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
