package de.matthiasmann.twl.input.jogl;

import java.util.HashMap;
import java.util.Map;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/**
 * Represents a keyboard input device.
 * 
 * @author:Mahesh kurmi
 */
public class Keyboard implements KeyListener {
	/** The keys */
	protected Map<Integer, InputKey> keys = new HashMap<Integer, InputKey>();

	public Map<Integer, InputKey> getActiveKeys() {
		return this.keys;
	}

	public Keyboard() {
		for (int i = 0; i < 256; i++) {
			this.keys.put(i, new InputKey(i, (char) i));
		}
	}


	/**
	 * Returns true if the given input has been signaled.
	 * <p>
	 * Calling this method will clear the value of the input if the input has
	 * already been released or if the input is {@link Hold#NO_HOLD} and the
	 * input has not been released.
	 * 
	 * @param code
	 *            the event code
	 * @return boolean
	 */
	public boolean isPressed(int keyCode) {
		synchronized (this.keys) {
			if (this.keys.containsKey(keyCode)) {
				return this.keys.get(keyCode).isPressed();
			}
		}
		return false;
	}

	/**
	 * Returns the the number of times key is called since last reset() of the
	 * given input.
	 * 
	 * @param code
	 *            the event code
	 * @return int
	 */
	public int getValue(int keyCode) {
		synchronized (this.keys) {
			if (this.keys.containsKey(keyCode)) {
				return this.keys.get(keyCode).getValue();
			}
		}
		return 0;
	}

	/**
	 * Resets the given input, and assumed as consumed
	 * 
	 * @param code
	 *            the event code
	 */
	public void clear(int keyCode) {
		synchronized (this.keys) {
			this.keys.remove(keyCode);
		}
	}

	/**
	 * Resets all the keys, assumes are are consumed
	 */
	public void reset() {
		synchronized (this.keys) {
			for (InputKey key : this.keys.values())
				key.reset();
		}
	}

	/*
	 * Resets the given input, and assumed as consumed
	 * 
	 * @param code the event code
	 */
	public void reset(int keyCode) {
		synchronized (this.keys) {
			if (this.keys.containsKey(keyCode)) {
				this.keys.get(keyCode).reset();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// do not listen auto repeat keys
		if (0 == (InputEvent.AUTOREPEAT_MASK & e.getModifiers())) {
			if (this.keys.containsKey(keyCode)) {
				this.keys.get(keyCode).press(e.getKeyChar());
		         //System.out.println("key df="+e.getKeyChar()+", code="+e.getKeyCode()+", pressed=");//+input.isPressed()+", handled="+handled);
			}

		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// do not listen auto repeat keys
		if (0 == (InputEvent.AUTOREPEAT_MASK & e.getModifiers())) {
			if (this.keys.containsKey(keyCode)) {
				this.keys.get(keyCode).release();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {

	}

}
