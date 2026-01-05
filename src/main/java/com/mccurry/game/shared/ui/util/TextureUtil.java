package com.mccurry.game.shared.ui.util;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TextureUtil {
    public static Texture2D toGrayscale(Texture texture) {
        if (!(texture instanceof Texture2D)) {
            throw new IllegalArgumentException("Only Texture2D supported");
        }

        Image img = ((Texture2D) texture).getImage();
        Image.Format fmt = img.getFormat();

        final int components;
        // We'll handle formats explicitly so we know channel ordering.
        if (fmt == Image.Format.RGBA8) {
            components = 4;
        } else if (fmt == Image.Format.RGB8) {
            components = 3;
        } else if (fmt == Image.Format.ABGR8) {
            components = 4;
        } else if (fmt == Image.Format.BGR8) {
            components = 3;
        } else {
            throw new IllegalArgumentException("Unsupported image format: " + fmt);
        }

        int width = img.getWidth();
        int height = img.getHeight();

        ByteBuffer src = img.getData(0); // level 0 (base)
        ByteBuffer s = src.duplicate().order(ByteOrder.nativeOrder());
        s.rewind();

        // Create output buffer sized to same format (so we preserve ordering/alpha).
        ByteBuffer out = BufferUtils.createByteBuffer(width * height * components);
        out.order(ByteOrder.nativeOrder());

        // NTSC luminance weights
        final float wr = 0.299f;
        final float wg = 0.587f;
        final float wb = 0.114f;

        for (int i = 0; i < width * height; i++) {
            int r = 0, g = 0, b = 0, a = 255;

            // Read according to format's byte order
            if (fmt == Image.Format.RGBA8) {
                r = s.get() & 0xFF;
                g = s.get() & 0xFF;
                b = s.get() & 0xFF;
                a = s.get() & 0xFF;
            } else if (fmt == Image.Format.RGB8) {
                r = s.get() & 0xFF;
                g = s.get() & 0xFF;
                b = s.get() & 0xFF;
            } else if (fmt == Image.Format.ABGR8) {
                a = s.get() & 0xFF; // A
                b = s.get() & 0xFF; // B
                g = s.get() & 0xFF; // G
                r = s.get() & 0xFF; // R
            } else if (fmt == Image.Format.BGR8) {
                b = s.get() & 0xFF;
                g = s.get() & 0xFF;
                r = s.get() & 0xFF;
            }

            // compute luminance (rounded)
            int lum = Math.round(wr * r + wg * g + wb * b);
            if (lum < 0) lum = 0;
            else if (lum > 255) lum = 255;

            // Write back in same format ordering (preserve alpha if present).
            if (fmt == Image.Format.RGBA8) {
                out.put((byte) lum); // R
                out.put((byte) lum); // G
                out.put((byte) lum); // B
                out.put((byte) a);   // A
            } else if (fmt == Image.Format.RGB8) {
                out.put((byte) lum); // R
                out.put((byte) lum); // G
                out.put((byte) lum); // B
            } else if (fmt == Image.Format.ABGR8) {
                out.put((byte) a);   // A
                out.put((byte) lum); // B
                out.put((byte) lum); // G
                out.put((byte) lum); // R
            } else if (fmt == Image.Format.BGR8) {
                out.put((byte) lum); // B
                out.put((byte) lum); // G
                out.put((byte) lum); // R
            }
        }

        out.rewind();

        Image newImg = new Image(fmt, width, height, out);
        Texture2D newTex = new Texture2D(newImg);
        return newTex;
    }
}
