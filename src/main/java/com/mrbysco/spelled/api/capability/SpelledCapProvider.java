package com.mrbysco.spelled.api.capability;

import com.mrbysco.spelled.api.SpelledAPI;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class SpelledCapProvider implements ICapabilitySerializable<INBT> {
    private final LazyOptional<ISpellData> instance;
    private final ISpellData data;

    public SpelledCapProvider() {
        this.data = SpelledAPI.SPELL_DATA_CAP.getDefaultInstance();
        this.instance = LazyOptional.of(() -> data);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SpelledAPI.SPELL_DATA_CAP.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return SpelledAPI.SPELL_DATA_CAP.writeNBT(data, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        SpelledAPI.SPELL_DATA_CAP.readNBT(data, null, nbt);
    }
}
