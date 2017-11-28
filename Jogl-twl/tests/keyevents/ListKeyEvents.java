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
package keyevents;

import de.matthiasmann.twl.Container;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.ScrollPane.Fixed;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;
import newt.NewtFrame;

/**
 *
 * @author Matthias Mann +Mahesh kurmi
 */
public class ListKeyEvents extends Container {
	public static void main(String[] args) {
		ListKeyEvents demo = new ListKeyEvents();
		NewtFrame guiDemo = new NewtFrame(800, 600, demo);
		guiDemo.setTitle("TWL Keyboard Event Debugger");
		guiDemo.applyTheme(ListKeyEvents.class.getResource("ListKeyEvents.xml"));
	}
 

    private final StringBuilder sb;
    private final SimpleTextAreaModel textAreaModel;
    private final TextArea textArea;
    private final ScrollPane scrollPane;

    public boolean quit;

    public ListKeyEvents() {
        sb = new StringBuilder();
        sb.append("Press any key - the keyboard events are displayed below\n");
        
        textAreaModel = new SimpleTextAreaModel(sb.toString());
        textArea = new TextArea(textAreaModel);
        scrollPane = new ScrollPane(textArea);
        scrollPane.setFixed(Fixed.HORIZONTAL);
        
        add(scrollPane);
    }
    
    @Override
    protected boolean handleEvent(Event evt) {
         sb.append(String.format("%s %s (code %d) char %c (%d)\n",
                        evt.isKeyPressedEvent() ? "PRESSED " : "RELEASED",
                        Event.getKeyNameForCode(evt.getKeyCode()),
                        evt.getKeyCode(),
                        evt.getKeyChar(),
                        (int)evt.getModifiers()));

            boolean atEnd = scrollPane.getScrollPositionY() == scrollPane.getMaxScrollPosY();
            textAreaModel.setText(sb.toString());
            if(atEnd) {
                scrollPane.validateLayout();
                scrollPane.setScrollPositionY(scrollPane.getMaxScrollPosY());
            }

            return true;
    }
    
}
