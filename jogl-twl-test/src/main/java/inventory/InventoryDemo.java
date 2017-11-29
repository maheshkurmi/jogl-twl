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
package inventory;

import de.matthiasmann.twl.DesktopArea;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.ResizableFrame;
import newt.NewtFrame;

/**
 * A simple login panel
 * 
 * @author Matthias Mann + Mahesh kurmi
 */
public class InventoryDemo extends DesktopArea {
	 public static void main(String[] args) {
		 InventoryDemo demo = new InventoryDemo();
	        NewtFrame guiDemo=new NewtFrame(800,600,demo);
	        guiDemo.setTitle("TWL Login Panel Demo");
	        guiDemo.applyTheme(InventoryDemo.class.getResource("inventory.xml"));
	        demo.positionFrame();
	    }

   

    final FPSCounter fpsCounter;
    final ResizableFrame frame;
    final InventoryPanel inventoryPanel;
    
    boolean quit;

    public InventoryDemo() {
        fpsCounter = new FPSCounter();
        
        inventoryPanel = new InventoryPanel(10, 5);
        
        frame = new ResizableFrame();
        frame.setTitle("Inventory");
        frame.setResizableAxis(ResizableFrame.ResizableAxis.NONE);
        frame.add(inventoryPanel);
        
        add(fpsCounter);
        add(frame);
    }

    void positionFrame() {
    	//frame.setSize(460,260);
        frame.adjustSize();
        frame.setPosition(
                getInnerX() + (getInnerWidth() - frame.getWidth())/2,
                getInnerY() + (getInnerHeight() - frame.getHeight())/2);
    }
    
    @Override
    protected void layout() {
        super.layout();
        frame.adjustSize();
        // fpsCounter is bottom right
        fpsCounter.adjustSize();
        fpsCounter.setPosition(
                getInnerRight() - fpsCounter.getWidth(),
                getInnerBottom() - fpsCounter.getHeight());
    }
    
}
