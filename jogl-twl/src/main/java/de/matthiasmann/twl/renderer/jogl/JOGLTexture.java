/*
 * Copyright (c) 2008-2014, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.matthiasmann.twl.renderer.jogl;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.QueriablePixels;
import de.matthiasmann.twl.renderer.Resource;
import de.matthiasmann.twl.renderer.Texture;
import de.matthiasmann.twl.utils.PNGDecoder;

/**
 * Simple texture implementation for TWL using LWJGL.
 *
 * @author Matthias Mann+ Mahesh Kurmi
 */
public class JOGLTexture implements Texture, Resource, QueriablePixels {

    public enum Format {
        ALPHA(GL2.GL_ALPHA, GL2.GL_ALPHA8, PNGDecoder.Format.ALPHA),
        LUMINANCE(GL2.GL_LUMINANCE, GL2.GL_LUMINANCE8, PNGDecoder.Format.LUMINANCE),
        LUMINANCE_ALPHA(GL2.GL_LUMINANCE_ALPHA, GL2.GL_LUMINANCE8_ALPHA8, PNGDecoder.Format.LUMINANCE_ALPHA),
        RGB(GL2.GL_RGB, GL2.GL_RGB8, PNGDecoder.Format.RGB),
        RGB_SMALL(GL2.GL_RGB, GL2.GL_RGB5_A1, PNGDecoder.Format.RGB),
        RGBA(GL2.GL_RGBA, GL2.GL_RGBA8, PNGDecoder.Format.RGBA),
        BGRA(GL2.GL_BGRA, GL2.GL_RGBA8, PNGDecoder.Format.BGRA),
        ABGR(GL2.GL_ABGR_EXT, GL2.GL_RGBA8, PNGDecoder.Format.ABGR),
        COLOR(-1, -1, null);

        final int glFormat;
        final int glInternalFormat;
        final PNGDecoder.Format pngFormat;

        Format(int fmt, int ifmt, PNGDecoder.Format pf) {
            this.glFormat = fmt;
            this.glInternalFormat = ifmt;
            this.pngFormat = pf;
        }

        public int getPixelSize() {
            return pngFormat.getNumComponents();
        }

        public PNGDecoder.Format getPngFormat() {
            return pngFormat;
        }
    }

    public enum Filter {
        NEAREST(GL2.GL_NEAREST),
        LINEAR(GL2.GL_LINEAR);

        final int glValue;
        Filter(int value) {
            this.glValue = value;
        }
    }

    final JOGLRenderer renderer;
    private int id;
    private final int width;
    private final int height;
    private final int texWidth;
    private final int texHeight;
    private ByteBuffer texData;
    private Format texDataFmt;
    private ArrayList<SWCursor> cursors;

    public JOGLTexture(JOGLRenderer renderer, int width, int height,
            ByteBuffer buf, Format fmt, Filter filter) {
        this.renderer = renderer;

        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        GL2 gl=GUI.gl;
        int[] tex_arr=new int[1];
        gl.glGenTextures(1,tex_arr,0);
        id=tex_arr[0];
        if(id == 0) {
            throw new OpenGLException("failed to allocate texture ID");
        }

        gl.glBindTexture(GL2.GL_TEXTURE_2D, id);
        gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, 0);
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);

        if(gl.getContext().isGL2()) {
        	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        } else {
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        }

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, filter.glValue);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, filter.glValue);

        this.texWidth = roundUpPOT(width);
        this.texHeight = roundUpPOT(height);

        if(texWidth != width || texHeight != height) {
        	gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0,
                    fmt.glInternalFormat, texWidth, texHeight,
                    0, fmt.glFormat, GL2.GL_UNSIGNED_BYTE,
                    (ByteBuffer)null);
            if(buf != null) {
                Util.checkGLError();
                gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0,
                        0, 0, width, height, fmt.glFormat,
                        GL2.GL_UNSIGNED_BYTE, buf);
            }
        } else {
        	gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0,
                    fmt.glInternalFormat, texWidth, texHeight,
                    0, fmt.glFormat, GL2.GL_UNSIGNED_BYTE, buf);
        }

        Util.checkGLError();

        this.width = width;
        this.height = height;
        this.texData = buf;
        this.texDataFmt = fmt;
    }

    @Override
	public void destroy() {
    	 GL2 gl=GUI.gl;
        if(id != 0) {
            // make sure that our texture is not bound when we try to delete it
        	gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        	int[] tmp = new int[1];
            tmp[0] = id;
            gl.glDeleteTextures(1, tmp, 0);
            id = 0;
        }
        if(cursors != null) {
            for(SWCursor cursor : cursors) {
                //cursor.destroy();
            }
            cursors.clear();
        }
    }

    @Override
	public int getWidth() {
        return width;
    }

    @Override
	public int getHeight() {
        return height;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public boolean bind(Color color) {
        if(id != 0) {
        	 GL2 gl=GUI.gl;
        	gl.glBindTexture(GL2.GL_TEXTURE_2D, id);
            renderer.tintStack.setColor(color);
            return true;
        }
        return false;
    }

    public boolean bind() {
        if(id != 0) {
        	 GL2 gl=GUI.gl;
        	gl.glBindTexture(GL2.GL_TEXTURE_2D, id);
            return true;
        }
        return false;
    }

    @Override
	public Image getImage(int x, int y, int width, int height, Color tintColor, boolean tiled, Rotation rotation) {
        if(x < 0 || x >= getWidth()) {
            throw new IllegalArgumentException("x");
        }
        if(y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("y");
        }
        if(x + Math.abs(width) > getWidth()) {
            throw new IllegalArgumentException("width");
        }
        if(y + Math.abs(height) > getHeight()) {
            throw new IllegalArgumentException("height");
        }
        if(rotation != Rotation.NONE || (tiled && (width < 0 || height < 0))) {
            return new TextureAreaRotated(this, x, y, width, height, tintColor, tiled, rotation);
        } else if(tiled) {
            return new TextureAreaTiled(this, x, y, width, height, tintColor);
        } else {
            return new TextureArea(this, x, y, width, height, tintColor);
        }
    }

    @Override
	public MouseCursor createCursor(int x, int y, int width, int height, int hotSpotX, int hotSpotY, Image imageRef) {
        if(renderer.isUseSWMouseCursors() || imageRef != null) {
            return new SWCursor(this, x, y, width, height, hotSpotX, hotSpotY, imageRef);
        }
        /*
        if(texData != null) {
            LWJGLCursor cursor = new SWCursor(texData, texDataFmt,
                    texDataFmt.getPixelSize() * this.width, x, y, width, height, hotSpotX, hotSpotY);

            if(cursors == null) {
                cursors = new ArrayList<LWJGLCursor>();
            }
            cursors.add(cursor);

            return cursor;
        }
        */
        return null;
    }

    @Override
	public int getPixelValue(int x, int y) {
        if(x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException();
        }
        
        int stride = texDataFmt.getPixelSize() * this.width;
        int offset;
        int a, r, g, b;
        
        switch(texDataFmt) {
        case RGB:
            offset = y*stride + x*3;
            r = texData.get(offset + 0) & 255;
            g = texData.get(offset + 1) & 255;
            b = texData.get(offset + 2) & 255;
            a = 255;
            break;
        case RGBA:
            offset = y*stride + x*4;
            r = texData.get(offset + 0) & 255;
            g = texData.get(offset + 1) & 255;
            b = texData.get(offset + 2) & 255;
            a = texData.get(offset + 3) & 255;
            break;
        case ABGR:
            offset = y*stride + x*4;
            r = texData.get(offset + 3) & 255;
            g = texData.get(offset + 2) & 255;
            b = texData.get(offset + 1) & 255;
            a = texData.get(offset + 0) & 255;
            break;
        case LUMINANCE:
            offset = y*stride + x;
            g = texData.get(offset) & 255;
            r = g;
            b = g;
            a = 255;
            break;
        case LUMINANCE_ALPHA:
            offset = y*stride + x*2;
            g = texData.get(offset + 0) & 255;
            r = g;
            b = g;
            a = texData.get(offset + 1) & 255;
            break;
        case ALPHA:
            offset = y*stride + x;
            r = 255;
            g = 255;
            b = 255;
            a = texData.get(offset) & 255;
            break;
        default:
            throw new IllegalStateException("Unsupported color format");
        }
        
        return ((a & 255) << 24) |
                ((r & 255) << 16) |
                ((g & 255) <<  8) |
                ((b & 255)      );
    }

    @Override
	public void themeLoadingDone() {
        // don't clear this to enable theme switching
        // this.texData = null;
    }

    static int roundUpPOT(int value) {
        return 1 << (32 - Integer.numberOfLeadingZeros(value-1));
    }
}
