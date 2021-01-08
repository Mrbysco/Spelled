package com.mrbysco.spelled.capability;

import net.minecraft.nbt.CompoundNBT;

public interface ISpellData {
    CompoundNBT getUnlocked();

    void setUnlocked(CompoundNBT nbt);

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
