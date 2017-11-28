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

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import de.matthiasmann.twl.GUI;

/**
 * Simple vertex array class.
 * 
 * <p>This class manages an interleaved vertex array in float format: {@code tx, ty, x, y}</p>
 * 
 * @author Matthias Mann + Mahesh Kurmi
 */
public class VertexArray {
    
    private FloatBuffer va;
    
    public FloatBuffer allocate(int maxQuads) {
        int capacity = 4 * 4 * maxQuads;
        if(va == null || va.capacity() < capacity) {
            va = BufferUtils.createFloatBuffer(capacity);
        }
        va.clear();
        return va;
    }
    
    public void bind() {
    	GL2 gl=GUI.gl;
        va.position(2);
        gl.glVertexPointer(2,GL2.GL_FLOAT, 4*4, va);// gl.glVertexPointer(2, 4*4, va);size,stride,floatbuffet
        va.position(0);
        
        gl.glTexCoordPointer(2, GL2.GL_FLOAT,4*4, va);//gl.glTexCoordPointer(2, 4*4, va);
        
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    }
    
    public void drawVertices(int start, int count) {
    	GL2 gl=GUI.gl;
        gl.glDrawArrays(GL2.GL_QUADS, start, count);
    }
    
    public void drawQuads(int start, int count) {
    	GL2 gl=GUI.gl;
        gl.glDrawArrays(GL2.GL_QUADS, start*4, count*4);
    }
    
    public void unbind() {
    	GL2 gl=GUI.gl;
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    }
    
}
