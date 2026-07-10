package com.mrbysco.spelled.client.gui.state;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record AltarBookRenderState(
		BookModel bookModel,
		Identifier texture,
		float open,
		float flip,
		int x0,
		int y0,
		int x1,
		int y1,
		float scale,
		int color,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
	public AltarBookRenderState(
			BookModel bookModel, Identifier texture, float open, float flip, int x0, int y0, int x1, int y1, float scale, int color, @Nullable ScreenRectangle scissorArea
	) {
		this(bookModel, texture, open, flip, x0, y0, x1, y1, scale, color, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
	}
}
