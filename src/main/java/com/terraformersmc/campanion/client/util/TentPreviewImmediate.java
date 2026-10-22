package com.terraformersmc.campanion.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * A buffer source that renders everything into the translucent moving-block layer, tinted and half transparent.
 */
public class TentPreviewImmediate implements MultiBufferSource {
	public static final TentPreviewImmediate STORAGE = new TentPreviewImmediate();

	private MultiBufferSource.BufferSource source;
	private float green = 1F;
	private float blue = 1F;

	private TentPreviewImmediate() {
	}

	public void begin(MultiBufferSource.BufferSource source) {
		this.source = source;
	}

	public void setApplyModifiers(boolean applyModifiers) {
		this.green = applyModifiers ? 0.1F : 1F;
		this.blue = applyModifiers ? 0.1F : 1F;
	}

	@Override
	public VertexConsumer getBuffer(RenderType type) {
		return new Tinted(this.source.getBuffer(RenderType.translucentMovingBlock()), this.green, this.blue);
	}

	public void endBatch() {
		if (this.source != null) {
			this.source.endBatch(RenderType.translucentMovingBlock());
		}
	}

	private record Tinted(VertexConsumer delegate, float g, float b) implements VertexConsumer {
		@Override
		public VertexConsumer addVertex(float x, float y, float z) {
			this.delegate.addVertex(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer setColor(int red, int green, int blue, int alpha) {
			this.delegate.setColor(red, (int) (green * this.g), (int) (blue * this.b), (int) (alpha * 0.5F));
			return this;
		}

		@Override
		public VertexConsumer setUv(float u, float v) {
			this.delegate.setUv(u, v);
			return this;
		}

		@Override
		public VertexConsumer setUv1(int u, int v) {
			this.delegate.setUv1(u, v);
			return this;
		}

		@Override
		public VertexConsumer setUv2(int u, int v) {
			this.delegate.setUv2(u, v);
			return this;
		}

		@Override
		public VertexConsumer setNormal(float x, float y, float z) {
			this.delegate.setNormal(x, y, z);
			return this;
		}
	}
}
