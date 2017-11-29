/*
 * Copyright (c) 2008-2010, Matthias Mann
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
package bgtest;

import de.matthiasmann.twl.DesktopArea;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import newt.NewtFrame;

import com.jogamp.opengl.GL2;

/**
 *
 * @author Matthias Mann + Mahesh Kurmi
 */
public class BackgroundTest extends DesktopArea {

    public static void main(String[] args) {
        BackgroundTest chat = new BackgroundTest();
        NewtFrame guiDemo=new NewtFrame(800,600,chat);
        guiDemo.setTitle("Bg demo");
        guiDemo.applyTheme(BackgroundTest.class.getResource("bgtest.xml"));
    }

    private final FPSCounter fpsCounter;
    private final Label mouseCoords;

    private Image gridBase;
    private Image gridMask;
    private DynamicImage lightImage;

    public BackgroundTest() {
        fpsCounter = new FPSCounter();
        mouseCoords = new Label();

        add(fpsCounter);
        add(mouseCoords);
    }

    @Override
    protected void layout() {
        super.layout();

        // fpsCounter is bottom right
        fpsCounter.adjustSize();
        fpsCounter.setPosition(
                getInnerWidth() - fpsCounter.getWidth(),
                getInnerHeight() - fpsCounter.getHeight());

        mouseCoords.adjustSize();
        mouseCoords.setPosition(0, getInnerHeight() - fpsCounter.getHeight());
    }

    @Override
    protected void applyTheme(ThemeInfo themeInfo) {
        super.applyTheme(themeInfo);
        gridBase = themeInfo.getImage("grid.base");
        gridMask = themeInfo.getImage("grid.mask");
    }

    @Override
    protected void paintBackground(GUI gui) {
        if(lightImage == null) {
            createLightImage(gui.getRenderer());
        }
        if(gridBase != null && gridMask != null) {
            int time = (int)(gui.getCurrentTime() % 2000);
            int offset = (time * (getInnerHeight() + 2*lightImage.getHeight()) / 2000) - lightImage.getHeight();
            gridBase.draw(getAnimationState(), getInnerX(), getInnerY(), getInnerWidth(), getInnerHeight());
            gui.gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
            lightImage.draw(getAnimationState(), getInnerX(), getInnerY() + offset, getInnerWidth(), lightImage.getHeight());
            gui.gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gridMask.draw(getAnimationState(), getInnerX(), getInnerY(), getInnerWidth(), getInnerHeight());
        }
    }

    private void createLightImage(Renderer renderer) {
        lightImage = renderer.createDynamicImage(1, 128);
        ByteBuffer bb = ByteBuffer.allocateDirect(128 * 4);
        IntBuffer ib = bb.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        for(int i=0 ; i<128 ; i++) {
            int value = (int)(255 * Math.sin(i * Math.PI / 127.0));
            ib.put(i, (value * 0x010101) | 0xFF000000);
        }
        lightImage.update(bb, DynamicImage.Format.BGRA);
    }

    @Override
    protected boolean handleEvent(Event evt) {
        if(evt.isMouseEvent()) {
            mouseCoords.setText("x: " + evt.getMouseX() + "  y: " + evt.getMouseY());
        }
        return super.handleEvent(evt) || evt.isMouseEventNoWheel();
    }
}
