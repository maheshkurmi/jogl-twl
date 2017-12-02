package de.matthiasmann.twl.input.jogl;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;


/**
 * Input handling based on LWJGL's Mouse & Keyboard classes.
 *
 * @author Mahesh Kurmi
 */
public class JOGLInput implements Input, WindowListener {

	/** The mouse to accept and store mouse events */
	private Mouse mouse;

	/** The keyboard to accept and store key events */
	private Keyboard keyboard;

	private boolean active=false;
	
	public JOGLInput(NewtCanvasAWT canvas){
		this.mouse=new Mouse();
		this.keyboard=new Keyboard();
		canvas.getNEWTChild().addMouseListener(mouse);
		canvas.getNEWTChild().addKeyListener(keyboard);
		canvas.getNEWTChild().addWindowListener(this);
	}
	
    @Override
	public boolean pollInput(GUI gui) {
       if(!active)return false;
       boolean handled=false;
       for( InputKey input:this.keyboard.keys.values()) {
    	   if(input.isActive()==false)continue;
    	   handled= gui.handleKey(
    			   input.getKeyCode(),
    			   input.getKeyChar(),
    			   input.isPressed());
           
          //System.out.println("key="+input.getKeyChar()+", code="+input.getKeyCode()+", pressed="+input.isPressed()+", handled="+handled);
       }
       
       keyboard.reset();
       
       handled=false;
       for(MouseButton btn:this.mouse.getActiveButtons().values()) {
    	  if(btn.isPressed()||btn.wasReleased()||mouse.hasMoved()) {
    		  handled= gui.handleMouse(  mouse.getLocation().x,
                      mouse.getLocation().y,
                      btn.getCode()-1,
                      btn.isPressed());
    	  // System.out.println("btn="+(btn.getCode()-1)+", pressed="+btn.isPressed());
    	  }
                    
       }
       
       if(mouse.hasScrolled()){
    	   handled= gui.handleMouseWheel(mouse.getScrollAmount());
       }
       mouse.clear();

       //very important 
       return true;
    }

	@Override
	public void windowResized(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowMoved(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDestroyNotify(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDestroyed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		active=true;
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
        active=false;
	}

	@Override
	public void windowRepaint(WindowUpdateEvent e) {
		// TODO Auto-generated method stub
		
	}

}
