/*
 * Copyright (c) 2008-2013, Matthias Mann
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

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.CacheContext;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 *
 * @author Matthias Mann +Mahesh Kurmi
 */
public class JOGLCacheContext implements CacheContext {

    final JOGLRenderer renderer;
    final HashMap<String, JOGLTexture> textures;
    final HashMap<String, BitmapFont> fontCache;
    final ArrayList<JOGLTexture> allTextures;
    boolean valid;

    protected JOGLCacheContext(JOGLRenderer renderer) {
        this.renderer = renderer;
        this.textures = new HashMap<String, JOGLTexture>();
        this.fontCache = new HashMap<String, BitmapFont>();
        this.allTextures = new ArrayList<JOGLTexture>();
        valid = true;
    }

    JOGLTexture loadTexture(URL url, JOGLTexture.Format fmt, JOGLTexture.Filter filter) throws IOException {
        String urlString = url.toString();
        JOGLTexture texture = textures.get(urlString);
        if(texture == null) {
            texture = createTexture(url, fmt, filter, null);
            textures.put(urlString, texture);
        }
        return texture;
    }

    JOGLTexture createTexture(URL textureUrl, JOGLTexture.Format fmt, JOGLTexture.Filter filter, TexturePostProcessing tpp) throws IOException {
        if(!valid) {
            throw new IllegalStateException("CacheContext already destroyed");
        }
        TextureDecoder decoder = (TextureDecoder)textureUrl.getContent(new Class<?>[]{TextureDecoder.class});
        if(decoder != null) {
            return createDecoderTexture(textureUrl, decoder, fmt, filter, tpp);
        } else {
            return createPNGTexture(textureUrl, fmt, filter, tpp);
        }
    }
    
    private JOGLTexture createDecoderTexture(URL textureUrl, TextureDecoder dec, JOGLTexture.Format fmt, JOGLTexture.Filter filter, TexturePostProcessing tpp) throws IOException {
        dec.open();
        try {
            fmt = dec.decideTextureFormat(fmt);
            if(fmt == null) {
                throw new NullPointerException("TextureDecoder.decideTextureFormat() returned null");
            }
            int width = dec.getWidth();
            int height = dec.getHeight();
            int maxTextureSize = renderer.maxTextureSize;

            if(width > maxTextureSize || height > maxTextureSize) {
                throw new IOException("Texture size too large. Maximum supported texture by this system is " + maxTextureSize);
            }

            int stride = width * fmt.getPixelSize();
            ByteBuffer buf = BufferUtils.createByteBuffer(stride * height);
            dec.decode(buf, stride, fmt);
            buf.flip();

            if(tpp != null) {
                tpp.process(buf, stride, width, height, fmt);
            }

            JOGLTexture texture = new JOGLTexture(renderer, width, height, buf, fmt, filter);
            allTextures.add(texture);
            return texture;
        } catch (IOException ex) {
            throw (IOException)(new IOException("Unable to load texture via decoder: " + textureUrl).initCause(ex));
        } finally {
            try {
                dec.close();
            } catch (IOException ex) {
            }
        }
    }
    
    private JOGLTexture createPNGTexture(URL textureUrl, JOGLTexture.Format fmt, JOGLTexture.Filter filter, TexturePostProcessing tpp) throws IOException {
        InputStream is = textureUrl.openStream();
        try {
            PNGDecoder dec = new PNGDecoder(is);
            fmt = decideTextureFormat(dec, fmt);
            int width = dec.getWidth();
            int height = dec.getHeight();
            int maxTextureSize = renderer.maxTextureSize;

            if(width > maxTextureSize || height > maxTextureSize) {
                throw new IOException("Texture size too large. Maximum supported texture by this system is " + maxTextureSize);
            }

            GL2 gl=GUI.gl;
           
            if( gl.getContext().isExtensionAvailable("GL_EXT_abgr")){//gl..getCapabilities().GL_EXT_abgr) {
                if(fmt == JOGLTexture.Format.RGBA) {
                    fmt = JOGLTexture.Format.ABGR;
                }
            } else if(fmt == JOGLTexture.Format.ABGR) {
                fmt = JOGLTexture.Format.RGBA;
            }

            int stride = width * fmt.getPixelSize();
            ByteBuffer buf = BufferUtils.createByteBuffer(stride * height);
            dec.decode(buf, stride, fmt.getPngFormat());
            buf.flip();

            if(tpp != null) {
                tpp.process(buf, stride, width, height, fmt);
            }

            JOGLTexture texture = new JOGLTexture(renderer, width, height, buf, fmt, filter);
            allTextures.add(texture);
            return texture;
        } catch (IOException ex) {
            throw (IOException)(new IOException("Unable to load PNG file: " + textureUrl).initCause(ex));
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
            }
        }
    }

    BitmapFont loadBitmapFont(URL url) throws IOException {
        String urlString = url.toString();
        BitmapFont bmFont = fontCache.get(urlString);
        if(bmFont == null) {
            bmFont = BitmapFont.loadFont(renderer, url);
            fontCache.put(urlString, bmFont);
        }
        return bmFont;
    }

    public boolean isValid() {
        return valid;
    }

    public void destroy() {
        try {
            for(JOGLTexture t : allTextures) {
                t.destroy();
            }
            for(BitmapFont f : fontCache.values()) {
                f.destroy();
            }
        } finally {
            textures.clear();
            fontCache.clear();
            allTextures.clear();
            valid = false;
        }
    }

    private static JOGLTexture.Format decideTextureFormat(PNGDecoder decoder, JOGLTexture.Format fmt) {
        if(fmt == JOGLTexture.Format.COLOR) {
            fmt = autoColorFormat(decoder);
        }
        
        PNGDecoder.Format pngFormat = decoder.decideTextureFormat(fmt.getPngFormat());
        if(fmt.pngFormat == pngFormat) {
            return fmt;
        }

        switch(pngFormat) {
            case ALPHA:
                return JOGLTexture.Format.ALPHA;
            case LUMINANCE:
                return JOGLTexture.Format.LUMINANCE;
            case LUMINANCE_ALPHA:
                return JOGLTexture.Format.LUMINANCE_ALPHA;
            case RGB:
                return JOGLTexture.Format.RGB;
            case RGBA:
                return JOGLTexture.Format.RGBA;
            case BGRA:
                return JOGLTexture.Format.BGRA;
            case ABGR:
                return JOGLTexture.Format.ABGR;
            default:
                throw new UnsupportedOperationException("PNGFormat not handled: " + pngFormat);
        }
    }

    private static JOGLTexture.Format autoColorFormat(PNGDecoder decoder) {
        if(decoder.hasAlpha()) {
            if(decoder.isRGB()) {
                return JOGLTexture.Format.ABGR;
            } else {
                return JOGLTexture.Format.LUMINANCE_ALPHA;
            }
        } else if(decoder.isRGB()) {
            return JOGLTexture.Format.ABGR;
        } else {
            return JOGLTexture.Format.LUMINANCE;
        }
    }
}
