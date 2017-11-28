package de.matthiasmann.twl.input.jogl;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseEvent.PointerType;
import com.jogamp.newt.event.MouseListener;

/**
 * Represents a polled Mouse input device.
 * @author:Mahesh kurmi
 */
public class Mouse implements MouseListener, MouseWheelListener {
	/** The map of mouse buttons */
	private Map<Integer, MouseButton> buttons = new Hashtable<Integer, MouseButton>();
	
	/** The current mouse location */
	private Point location=new Point(0,0);
	
	/** Whether the mouse has moved */
	private boolean moved;
	
	/** The scroll amount */
	private int scroll;
	private double zoom=1;
	private double angle=0;
	private boolean hasGestures=false;
	private int activePointerId=-1;
	

	
	public Map<Integer, MouseButton> getActiveButtons(){
		return this.buttons;
	}
	/**
	 * Returns true if the given MouseEvent code was clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the clicked state
		return mb.wasClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was double clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasDoubleClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasDoubleClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and is waiting to be released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean isPressed(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.isPressed();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and was waiting to be released
	 * but is now released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasReleased(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasReleased();
	}
	
	/**
	 * Returns the current location of the mouse relative to
	 * the listening component.
	 * @return Point
	 */
	public Point getLocation() {
		return this.location;
	}
	
	/**
	 * Returns true if the mouse has moved.
	 * @return boolean
	 */
	public boolean hasMoved() {
		return this.moved;
	}
	
	/**
	 * Clears the state of the given MouseEvent code.
	 * @param code the MouseEvent code
	 */
	public void clear(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return;
		}
		// clear the state
		mb.clear();
	}
	
	/**
	 * Clears the state of all MouseEvents.
	 */
	public void clear() {
		Iterator<MouseButton> buttons = this.buttons.values().iterator();
		while (buttons.hasNext()) {
			buttons.next().clear();
		}
	
		this.moved = false;
		this.scroll = 0;
		this.zoom=1;
		this.angle=0;
	}
	
	/**
	 * Returns true if the user has scrolled the mouse wheel.
	 * @return boolean
	 */
	public boolean hasScrolled() {
		return this.scroll != 0;
	}
	
	/**
	 * Returns true if the user has scrolled the mouse wheel.
	 * @return boolean
	 */
	public boolean hasGestures() {
		return hasGestures ;
	}
	/**
	 * Returns the number of 'clicks' the mouse wheel has scrolled.
	 * @return int
	 */
	public int getScrollAmount() {
		return this.scroll;
	}
	
	/**
	 * Returns the zoom due to pinchScreen gesture
	 */
	public double getZoom() {
		return this.zoom;
	}
	
	/** 
	 * return angle due to due to pinchScreen gesture
	 */
	public double getAngle() {
		return this.angle;
	}
	
	
	/*s
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println("Mouse clicked :"+ "type="+e.getPointerType(0)+"ptr_id "+e.getPointerId(0)+" activeid="+activePointerId);
		this.location = new Point(e.getX(), e.getY());
		
		int code = e.getButton();
		if(e.getPointerType(0)!=PointerType.Mouse){
			code=MouseEvent.BUTTON1;
		}
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		// set the value directly (since this can be a single/double/triple etc click)
		mb.setValue(e.getClickCount());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// called when a mouse button is pressed and is waiting for release
		//System.out.println("Mouse pressed :"+ "type="+e.getPointerType(0)+"ptr_id "+e.getPointerId(0)+" activeid="+activePointerId);
		this.location = new Point(e.getX(), e.getY());
		
		//	System.out.println(e.toString());//
		// set the mouse state to pressed + held for the button
		int code =e.getButton();
		if(e.getPointerType(0)!=PointerType.Mouse){
			hasGestures=e.getPointerCount()>1;
			
			if(activePointerId==-1){
				activePointerId=e.getPointerId(0);
				code=MouseEvent.BUTTON1;
			}
		}
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(true);
	
	}

	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseReleased(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// called when a mouse button is waiting for release and was released
		//System.out.println("Mouse released :"+ "type="+e.getPointerType(0)+"ptr_id "+e.getPointerId(0)+" activeid="+activePointerId);
		this.location = new Point(e.getX(), e.getY());
		
		// set the mouse state to released for the button
		
		int code = e.getButton();
		hasGestures=e.getPointerCount()>1;
		
		if(e.getPointerType(0)!=PointerType.Mouse){
			boolean released =true;
			for(int i=e.getPointerCount()-1; i>=0; i--) {
				if(activePointerId== e.getPointerId(i) ) {
	                released =false;
	                break;
	            }
	        }
			if(released || activePointerId== e.getPointerId(0)){
				activePointerId=-1;
				code=MouseEvent.BUTTON1;
			}
		}
		
		// set the mouse state to released for the button
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(false);
		mb.setWasReleased(true);
		
	}


	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseDragged(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// called when a mouse button is waiting for release and the mouse is moving
		//System.out.println("Mouse dragged :"+ "type="+e.getPointerType(0)+"ptr_id "+e.getPointerId(0)+" activeid="+activePointerId);
		this.location = new Point(e.getX(), e.getY());
		
		int code = e.getButton();
		if(e.getPointerType(0)!=PointerType.Mouse){
			hasGestures=e.getPointerCount()>1;
			
			if(activePointerId==e.getPointerId(0)){
				code=MouseEvent.BUTTON1; 
			}
		}
		// set the mouse location
		this.moved = true;
		this.location = new Point(e.getX(), e.getY());
		// set the mouse button pressed flag
		
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		
		mb.setPressed(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseMoved(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		this.moved = true;
		this.location = new Point(e.getX(), e.getY());
		
		if(!e.isButtonDown(MouseEvent.BUTTON1) && this.buttons.get(MouseEvent.BUTTON1)!=null){
			if(this.buttons.get(MouseEvent.BUTTON1).isPressed()){
				this.buttons.get(MouseEvent.BUTTON1).setPressed(false);
				this.buttons.get(MouseEvent.BUTTON1).setWasReleased(true);
			}
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseWheelMoved(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseEvent e) {
		this.scroll += e.getRotation()[1];
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.scroll += e.getWheelRotation();
	}
	
	// not used
	
	/* (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {}


}
