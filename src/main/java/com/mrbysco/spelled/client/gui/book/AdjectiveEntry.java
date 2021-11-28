package com.mrbysco.spelled.client.gui.book;

import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import net.minecraft.util.text.ITextComponent;

public class AdjectiveEntry implements Comparable<AdjectiveEntry> {
	private final IKeyword keyword;
	private final String adjectiveName;
	private final ITextComponent adjectiveDescription;
	private final int slots;

	public AdjectiveEntry(IKeyword keyword, String name, ITextComponent description, int slots) {
		this.keyword = keyword;
		this.adjectiveName = name;
		this.adjectiveDescription = description;
		this.slots = slots;
	}

	public boolean isType() {
		return keyword instanceof TypeKeyword;
	}

	public String getAdjectiveName() {
		return adjectiveName;
	}

	public ITextComponent getAdjectiveDescription() {
		return adjectiveDescription;
	}

	public int getSlots() {
		return slots;
	}

	@Override
	public int compareTo(AdjectiveEntry otherAdjective) {
		return getAdjectiveName().compareTo(otherAdjective.getAdjectiveName());
	}
}
