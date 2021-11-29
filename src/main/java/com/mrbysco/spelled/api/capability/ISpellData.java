package com.mrbysco.spelled.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface ISpellData extends ICapabilitySerializable<CompoundTag> {
    CompoundTag getUnlocked();

    void setUnlocked(CompoundTag nbt);

    boolean knowsKeyword(String keyword);

    void unlockKeyword(String keyword);

    void lockKeyword(String keyword);

    void resetUnlocks();

    int getLevel();

    void setLevel(int level);

    int getCastCooldown();

    void setCastCooldown(int cooldown);

//    boolean isDirty();
//
//    void setDirty(boolean dirty);
}
