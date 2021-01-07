package com.mrbysco.spelled.capability;

import com.mrbysco.spelled.registry.KeywordRegistry;
import net.minecraft.nbt.CompoundNBT;

import java.util.Locale;

public class SpellDataCapability implements ISpellData {
    private int level;
    private CompoundNBT unlockedKeywords;
    private int castCooldown;

    public SpellDataCapability() {
        this.level = 0;
        this.unlockedKeywords = getDefaultUnlocks();
        this.castCooldown = 0;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public CompoundNBT getUnlocked() {
        return this.unlockedKeywords;
    }

    @Override
    public void setUnlocked(CompoundNBT nbt) {
        this.unlockedKeywords = nbt;
    }

    @Override
    public boolean knowsKeyword(String keyword) {
        return this.unlockedKeywords.contains(keyword);
    }

    @Override
    public void unlockKeyword(String keyword) {
        this.unlockedKeywords.putBoolean(keyword.toLowerCase(Locale.ROOT), true);
    }

    @Override
    public void lockKeyword(String keyword) {
        this.unlockedKeywords.remove(keyword.toLowerCase(Locale.ROOT));
    }

    @Override
    public int getCastCooldown() {
        return this.castCooldown;
    }

    @Override
    public void setCastCooldown(int cooldown) {
        this.castCooldown = cooldown;
    }

    private CompoundNBT getDefaultUnlocks() {
        KeywordRegistry registry = KeywordRegistry.instance();
        CompoundNBT tag = new CompoundNBT();
        registry.getTypes().forEach(type -> tag.putBoolean(type, true));
        return tag;
    }
}
