package com.mrbysco.spelled.packets;

import com.mrbysco.spelled.item.SpellbookItem;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SignSpellPacket {
    private final ItemStack book;
    private final boolean signing;
    private final int slot;

    public SignSpellPacket(ItemStack stack, boolean signing, int slot) {
        this.book = stack.copy();
        this.signing = signing;
        this.slot = slot;
    }

    private SignSpellPacket(PacketBuffer buf) {
        this.book = buf.readItem();
        this.signing = buf.readBoolean();
        this.slot = buf.readVarInt();
    }

    public void encode(PacketBuffer buf) {
        buf.writeItem(this.book);
        buf.writeBoolean(this.signing);
        buf.writeVarInt(this.slot);
    }

    public static SignSpellPacket decode(final PacketBuffer packetBuffer) {
        return new SignSpellPacket(packetBuffer);
    }

    public void handle(Supplier<Context> context) {
        Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isServer()) {
                ServerPlayerEntity player = ctx.getSender();
                if (book.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
                    CompoundNBT compoundnbt = book.getTag();
                    if (SpellbookItem.makeSureTagIsValid(player, compoundnbt)) {
                        if (PlayerInventory.isHotbarSlot(slot) || slot == 40) {
                            if(signing) {
                                this.signBook(player, compoundnbt.getString("title"), compoundnbt.getString("spell"), slot);
                            } else {
                                this.updateBookContents(player, compoundnbt.getString("spell"), slot);
                            }
                        }
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    private void updateBookContents(ServerPlayerEntity player, String spell, int slot) {
        ItemStack stack = player.inventory.getItem(slot);
        if (stack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
            stack.addTagElement("spell", StringNBT.valueOf(spell));
        }
    }

    private void signBook(ServerPlayerEntity player, String title, String spell, int slot) {
        ItemStack itemstack = player.inventory.getItem(slot);
        if (itemstack.getItem() == SpelledRegistry.SPELL_BOOK.get()) {
            ItemStack stack = new ItemStack(SpelledRegistry.SPELL_BOOK.get());
            CompoundNBT compoundnbt = itemstack.getTag();
            if (compoundnbt != null) {
                stack.setTag(compoundnbt.copy());
            }

            stack.addTagElement("author", StringNBT.valueOf(player.getName().getString()));
            stack.addTagElement("title", StringNBT.valueOf(title));
            stack.addTagElement("spell", StringNBT.valueOf(spell));
            stack.addTagElement("sealed", ByteNBT.valueOf(true));
            player.inventory.setItem(slot, stack);
        }
    }
}