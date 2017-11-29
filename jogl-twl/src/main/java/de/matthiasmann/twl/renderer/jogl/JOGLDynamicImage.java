/*
 * Copyright (c) 2008-2012, Matthias Mann
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

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.renderer.Image;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 *
 * @author Matthias Mann +Mahesh Kurmi
 */
public class JOGLDynamicImage extends TextureAreaBase implements DynamicImage {

    private final JOGLRenderer renderer;
    private final int target;
    private final Color tintColor;
    private int id;
    
    public JOGLDynamicImage(JOGLRenderer renderer, int target, int id,
            int width, int height, int texWidth, int texHeight, Color tintColor) {
        super(0, 0, width, height,
                (target == GL2.GL_TEXTURE_2D) ? texWidth : 1f,
                (target == GL2.GL_TEXTURE_2D) ? texHeight : 1f);
        
        this.renderer = renderer;
        this.tintColor = tintColor;
        this.target = target;
        this.id = id;
    }

    JOGLDynamicImage(JOGLDynamicImage src, Color tintColor) {
        super(src);
        this.renderer = src.renderer;
        this.tintColor = tintColor;
        this.target = src.target;
        this.id = src.id;
    }

    public void destroy() {
        if(id != 0) {
        	GL2 gl=GUI.gl;
        	int[] tex_arr=new int[1];
            gl.glDeleteTextures(1,tex_arr,0);
            id=tex_arr[0];
            renderer.dynamicImages.remove(this);
        }
    }

    public void update(ByteBuffer data, Format format) {
        update(0, 0, width, height, data, width*4, format);
    }
    
    public void update(ByteBuffer data, int stride, Format format) {
        update(0, 0, width, height, data, stride, format);
    }

    public void update(int xoffset, int yoffset, int width, int height, ByteBuffer data, Format format) {
        update(xoffset, yoffset, width, height, data, width*4, format);
    }
    
    public void update(int xoffset, int yoffset, int width, int height, ByteBuffer data, int stride, Format format) {
        if(xoffset < 0 || yoffset < 0 || getWidth() <= 0 || getHeight() <= 0) {
            throw new IllegalArgumentException("Negative offsets or size <= 0");
        }
        if(xoffset >= getWidth() || yoffset >= getHeight()) {
            throw new IllegalArgumentException("Offset outside of texture");
        }
        if(width > getWidth() - xoffset || height > getHeight() - yoffset) {
            throw new IllegalArgumentException("Rectangle outside of texture");
        }
        if(data == null) {
            throw new NullPointerException("data");
        }
        if(format == null) {
            throw new NullPointerException("format");
        }
        if(stride < 0 || (stride & 3) != 0) {
            throw new IllegalArgumentException("stride");
        }
        if(stride < width*4) {
            throw new IllegalArgumentException("stride too short for width");
        }
        if(data.remaining() < stride*(height-1)+width*4) {
            throw new IllegalArgumentException("Not enough data remaining in the buffer");
        }
        int glFormat = (format == Format.RGBA) ? GL2.GL_RGBA : GL2.GL_BGRA;
        bind();
        GL2 gl=GUI.gl;
        gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, stride/4);
        gl.glTexSubImage2D(target, 0, xoffset, yoffset, width, height, glFormat, GL2.GL_UNSIGNED_BYTE, data);
        gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, 0);
    }

    public Image createTintedVersion(Color color) {
        if(color == null) {
            throw new NullPointerException("color");
        }
        Color newTintColor = tintColor.multiply(color);
        if(newTintColor.equals(tintColor)) {
            return this;
        }
        return new JOGLDynamicImage(this, newTintColor);
    }

    public void draw(AnimationState as, int x, int y) {
        draw(as, x, y, width, height);
    }

    public void draw(AnimationState as, int x, int y, int width, int height) {
        bind();
        GL2 gl=GUI.gl;
        renderer.tintStack.setColor(tintColor);
        if(target != GL2.GL_TEXTURE_2D) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
            gl.glEnable(target);
        }
        gl.glBegin(GL2.GL_QUADS);
        drawQuad(x, y, width, height);
        gl.glEnd();
        if(target != GL2.GL_TEXTURE_2D) {
            gl.glDisable(target);
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
    }

    private void bind() {
        if(id == 0) {
            throw new IllegalStateException("destroyed");
        }
        GL2 gl=GUI.gl;
        gl.glBindTexture(target, id);
    }

}
