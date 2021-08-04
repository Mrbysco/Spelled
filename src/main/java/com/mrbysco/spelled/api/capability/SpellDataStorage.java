package com.mrbysco.spelled.api.capability;

import com.mrbysco.spelled.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SpellDataStorage implements Capability.IStorage<ISpellData> {

    @Nullable
    @Override
    public Tag writeNBT(Capability<ISpellData> capability, ISpellData instance, Direction side) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(Reference.characterLevel, instance.getLevel());
        tag.put(Reference.characterUnlocks, instance.getUnlocked());
        tag.putInt(Reference.characterCooldown, instance.getCastCooldown());

        return tag;
    }

    @Override
    public void readNBT(Capability<ISpellData> capability, ISpellData instance, Direction side, Tag nbt) {
        CompoundTag tag = ((CompoundTag)nbt);
        int level = tag.getInt(Reference.characterLevel);
        CompoundTag characterUnlocks = (CompoundTag)tag.get(Reference.characterUnlocks);
        int castCooldown = tag.getInt(Reference.characterCooldown);

        instance.setLevel(level);
        instance.setUnlocked(characterUnlocks);
        instance.setCastCooldown(castCooldown);
    }
}
