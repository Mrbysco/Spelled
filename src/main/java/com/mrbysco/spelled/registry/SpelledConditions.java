package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.modifiers.BookDisabledCondition;

public class SpelledConditions {
	public static void registerLootConditions() {
		BookDisabledCondition.register();
	}
}
