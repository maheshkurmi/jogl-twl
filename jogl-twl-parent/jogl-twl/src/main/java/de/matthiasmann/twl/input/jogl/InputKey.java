package de.matthiasmann.twl.input.jogl;

/**
 * Represents an input of an input device.
 * @author:Mahesh kurmi
 */
public class InputKey {
		
	/**
	 * This enumeration represents the state of an input.
	 */
	public static enum State {
		/** the key has been released */
		RELEASED,
		/** the key has been pressed */
		PRESSED
	}


	/** The input state */
	private InputKey.State state = InputKey.State.RELEASED;
	
	/** The keyCode id */
	private int keyCode = 0;
	
	/** The input count  (the number of times key event  is generated) */
	private int value = 0;

	/**key char associated with key if any*/
	private char keyChar=0;
	

	private boolean isActive=false;
	/**
	 * constructor.
	 * @param keyCode keyCode constant defined in {@link com.jogamp.newt.event.KeyEvent} for the key
	 * @param keyChar unicode char for the key
	 */
	public InputKey(int keyCode,char keyChar) {
		this.keyCode = keyCode;
		this.keyChar = keyChar;
	}

	
	/** 
	 * Resets the input.
	 */
	public synchronized void reset() {
		this.state = InputKey.State.RELEASED;
		isActive=false;
		this.value = 0;
	}
	
	/** 
	 * Notify that the input has been released.
	 */
	public synchronized void release() {
		isActive=true;
		this.state = InputKey.State.RELEASED;
	}
	
	/** 
	 * Notify that the input was pressed.
	 */
	public synchronized void press(char keyChar) {
		if(this.state ==InputKey.State.RELEASED)this.value = this.value + 1;
		isActive=true;
		this.keyChar=keyChar;
		this.state = InputKey.State.PRESSED;
	}

	
	/**
	 * Returns true if the input has been pressed since the last check.
	 * <p>
	 * Calling this method will clear the value of this input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @return boolean
	 */
	public synchronized boolean isPressed() {
		return  this.state ==InputKey.State.PRESSED;
	}

	/**
	 * returns true if event is yet to be consumed
	 * @return
	 */
	public boolean isActive(){
		return isActive;
	}
	
	
	/**
	 * <p>
	 * Returns the value of the input, number of times key is pressed, since {@method InputKey.reset} is called 
	 * @return int
	 */
	public synchronized int getValue() {
		return this.value;
	}
	

	/**
	 * Returns the state.
	 * @return {@link InputKey.State}
	 */
	public InputKey.State getState() {
		return state;
	}
	

	/**
	 * returns unicode keychar for the event
	 * @return
	 */
	public char getKeyChar() {
		return keyChar;
	}
	
	/**
	 * Returns the keyCode.
	 * @return int
	 */
	public int getKeyCode() {
		return this.keyCode;
	}
}
