package com.mrbysco.spelled.capability;

import com.mrbysco.spelled.Reference;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SpellDataStorage implements Capability.IStorage<ISpellData> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISpellData> capability, ISpellData instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(Reference.characterLevel, instance.getLevel());
        tag.put(Reference.characterUnlocks, instance.getUnlocked());
        tag.putInt(Reference.characterCooldown, instance.getCastCooldown());

        return tag;
    }

    @Override
    public void readNBT(Capability<ISpellData> capability, ISpellData instance, Direction side, INBT nbt) {
        CompoundNBT tag = ((CompoundNBT)nbt);
        int level = tag.getInt(Reference.characterLevel);
        CompoundNBT characterUnlocks = (CompoundNBT)tag.get(Reference.characterUnlocks);
        int castCooldown = tag.getInt(Reference.characterCooldown);

        instance.setLevel(level);
        instance.setUnlocked(characterUnlocks);
        instance.setCastCooldown(castCooldown);
    }
}
