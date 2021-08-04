package com.mrbysco.spelled.api.capability;

import com.mrbysco.spelled.api.SpelledAPI;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class SpelledCapProvider implements ICapabilitySerializable<Tag> {
    private LazyOptional<ISpellData> instance;
    private ISpellData data;

    public SpelledCapProvider() {
        this.data = SpelledAPI.SPELL_DATA_CAP.getDefaultInstance();
        this.instance = LazyOptional.of(() -> data);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SpelledAPI.SPELL_DATA_CAP.orEmpty(cap, instance);
    }

    @Override
    public Tag serializeNBT() {
        return SpelledAPI.SPELL_DATA_CAP.writeNBT(data, null);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        SpelledAPI.SPELL_DATA_CAP.readNBT(data, null, nbt);
    }
}
