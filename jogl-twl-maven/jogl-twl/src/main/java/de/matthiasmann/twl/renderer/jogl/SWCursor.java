/*
 * Copyright (c) 2008-2009, Matthias Mann
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
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 *
 * @author Matthias Mann+Mahesh Kurmi
 */
class SWCursor extends TextureAreaBase implements MouseCursor {

    private final JOGLTexture texture;
    private final int hotSpotX;
    private final int hotSpotY;
    private final Image imageRef;

    SWCursor(JOGLTexture texture, int x, int y, int width, int height, int hotSpotX, int hotSpotY, Image imageRef) {
        super(x, y, width, height, texture.getTexWidth(), texture.getTexHeight());
        this.texture = texture;
        this.hotSpotX = hotSpotX;
        this.hotSpotY = hotSpotY;
        this.imageRef = imageRef;
    }
    
    void render(int x, int y) {
        if(imageRef != null) {
            imageRef.draw(texture.renderer.swCursorAnimState, x-hotSpotX, y-hotSpotY);
        } else if(texture.bind()) {
        	GL2 gl=GUI.gl;
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glBegin(GL2.GL_QUADS);
            drawQuad(x-hotSpotX, y-hotSpotY, width, height);
            gl.glEnd();
        }
    }
}
