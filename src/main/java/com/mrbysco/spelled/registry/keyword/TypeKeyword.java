package com.mrbysco.spelled.registry.keyword;

import com.mrbysco.spelled.api.keywords.BaseKeyword;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TypeKeyword extends BaseKeyword {
    private final Type type;

    public TypeKeyword(String keyword, Type type, int level, int slots) {
        super(keyword, level, slots);
        this.type = type;
    }

    @Override
    public void cast(World worldIn, ServerPlayerEntity caster, SpellEntity spell, @Nullable IKeyword adjective) {
        if(spell != null) {
            spell.setSpellType(type.getId());
        }
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BALL(0, "ball"),
        PROJECTILE(1, "projectile"),
        SELF(2, "self");

        private final String name;
        private final int id;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}
