package newt;



import gameui.GameUIDemo;
import inventory.InventoryDemo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import nodes.Node;
import nodes.NodeArea;
import nodes.NodeDemo;
import nodes.Pad;
import keyevents.ListKeyEvents;
import login.LoginDemo;
import mines.Mines;
import test.SimpleTest;
import textarea.TextAreaDemo;
import actions.ActionDemo;
import bgtest.BackgroundTest;
import chat.ChatDemo;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.input.jogl.JOGLInput;
import de.matthiasmann.twl.renderer.jogl.JOGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twleffects.jogl.JOGLEffectsRenderer;
import demo.Demo;




public class NewtFrame extends JFrame implements GLEventListener, ActionListener{

	static final boolean glDebug = false;
	static final boolean glTrace = false;
	// data
	/** The canvas to draw to */
	public NewtCanvasAWT canvas;

	/** The OpenGL animator */
	private FPSAnimator animator;

	/** Simulation's frame pr second */
	private int FPS = 60;
	
	/** The time stamp for the last iteration */
	private long last;
	   
    
	private boolean verticalSynchronisation=true;
	
	public GUI gui=null;
	
	private Widget root;
	
	private ThemeManager themeManager=null;
	
	/**if true TWL Effect Render is used*/
	private boolean useEffects=false;
	
	private JToolBar toolbar;
	/**
	 * Minimal constructor
	 * creates display 800x600 with effects disabled
	 * @param root
	 */
	public NewtFrame(Widget root) { 
		this(800,600,root,false);
	}
	
	/**
	 * creates display width x height with effects disabled
	 * @param width
	 * @param height
	 * @param root
	 */
	public NewtFrame(int width,int height,Widget root) { 
		this(width,height,root,false);
	}
	
	/**
	 * Full constructor
	 * @param width
	 * @param height
	 * @param root
	 * @param useEffects
	 */
	public NewtFrame(int width,int height,Widget root,boolean useEffects) { 
		super();	
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		// setup the stencil buffer to outline shapes
		caps.setStencilBits(1);
		// setting multisampling allows for better looking body outlines
		caps.setSampleBuffers(true);
		caps.setNumSamples(2);
		caps.setHardwareAccelerated(true);
		// create a NEWT window
		GLWindow window = GLWindow.create(caps);
		window.setUndecorated(true);
		window.addGLEventListener(this);
		this.root=root;
		this.useEffects=useEffects;
		Dimension canvasSize = new Dimension(width, height);
		// Use the newt/awt bridge to allow us to use swing gui elements
		// and the fast rendering capabilities of NEWT
		this.canvas = new NewtCanvasAWT(window);
		// create a canvas to paint to
		this.canvas.setPreferredSize(canvasSize);
		this.canvas.setMinimumSize(new Dimension(100, 100));
		this.canvas.setIgnoreRepaint(false);
		JPanel pnlRoot=new JPanel();
		pnlRoot.setLayout(new BorderLayout());
		pnlRoot.add(canvas, BorderLayout.CENTER);
		createToolBar();
		pnlRoot.add(toolbar,BorderLayout.NORTH);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout(0,0));
		container.add(pnlRoot,BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// initialize the last update time
		this.last = System.nanoTime();
		this.setSize(canvasSize);
		// create an animator to animated the canvas
		this.animator = new FPSAnimator(window, FPS, true);
		this.setVisible(true);	
		this.animator.start();
	}
	
	
	private void createToolBar(){
		toolbar = new JToolBar();
		for (int i=0;i<13;i++){
			JButton btn=new JButton("test"+i);
			btn.setActionCommand("test"+i);
			btn.addActionListener(this);
			toolbar.add(btn);
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		String s=ae.getActionCommand();
		if(s.startsWith("test")){
			
			int i=Integer.parseInt(s.substring(4));
			updateGui(i);
			this.setTitle("Jogl TWL Demo: test "+i);
			canvas.requestFocus();
		}
	}

	private void updateGui(int i){
	    	useEffects=false;
	    	switch(i){
	    	case 0:
	    		ActionDemo  actionDemo = new ActionDemo();
	            actionDemo.requestKeyboardFocus();
	        	this.root=actionDemo;
	        	this.themeURL=ActionDemo.class.getResource("/actions/actiondemo.xml");
	    		break;
	    	case 1:
	    		BackgroundTest chat = new BackgroundTest();
	    		this.root=chat;
	        	this.themeURL=BackgroundTest.class.getResource("/bgtest/bgtest.xml");
	    	    break;
	    	case 2:
	    		ChatDemo chat1 = new ChatDemo();
	    		this.root=chat1;
	        	this.themeURL=ChatDemo.class.getResource("/chat/chat.xml");
	    	    break;
	    	case 3:
	   		 	Demo demo = new Demo();
	   		 	this.root=demo;
	        	this.themeURL=Demo.class.getResource("/demo/demo.xml");
	    	    break;
	    	case 4:
	    		GameUIDemo gameui = new GameUIDemo();
	    		this.root=gameui;
	        	this.themeURL=GameUIDemo.class.getResource("/gameui/gameui.xml");
	    	    break;
	    	case 5:
	    		InventoryDemo inventory = new InventoryDemo();
	    		this.root=inventory;
	        	this.themeURL=InventoryDemo.class.getResource("/inventory/inventory.xml");

	    	    break;
	    	case 6:
	    		ListKeyEvents keyevents = new ListKeyEvents();
	    		this.root=keyevents;
	        	this.themeURL=ListKeyEvents.class.getResource("/keyevents/ListKeyEvents.xml");
	    	    break;
	    	case 7:
	    		LoginDemo login = new LoginDemo();
	    		this.root=login;
	        	this.themeURL=LoginDemo.class.getResource("/login/login.xml");
	    	    break;
	    	case 8:
	    		Mines mines = new Mines();
	    		this.root=mines;
	        	this.themeURL=Mines.class.getResource("/mines/mines.xml");
	        	mines.getOrCreateActionMap().addMapping(mines);
	            mines.startGame(16, 16, 40);
	    	    break;
	    	case 9:
	    		NodeArea nodeArea = new NodeArea();
	    		ScrollPane scrollPane = new ScrollPane(nodeArea);
	            scrollPane.setExpandContentSize(true);
	           
	            Node nodeSource = nodeArea.addNode("Source");
	            nodeSource.setPosition(50, 50);
	            Pad nodeSourceColor = nodeSource.addPad("Color", false);
	            Pad nodeSourceAlpha = nodeSource.addPad("Alpha", false);

	            Node nodeSink = nodeArea.addNode("Sink");
	            nodeSink.setPosition(350, 200);
	            Pad nodeSinkColor = nodeSink.addPad("Color", true);
	            nodeArea.addConnection(nodeSourceColor, nodeSinkColor);
	            
	    		this.root=scrollPane;
	        	this.themeURL=NodeArea.class.getResource("/nodes/nodes.xml");
	    	    break;
	    	case 10:
	    		sourceviewer.demo.Demo demo2 = new sourceviewer.demo.Demo();
	    		this.root=demo2;
	        	this.themeURL=Demo.class.getResource("/demo/demo.xml");
	    	    break;
	    	case 11:
	    		this.root=SimpleTest.createRootPane(this);;
	        	this.themeURL=GameUIDemo.class.getResource("/test/simple_demo.xml");
	    	    break;
	    	case 12:
	    		TextAreaDemo  textareaDemo = new TextAreaDemo();
	    		this.root=textareaDemo;
	        	this.themeURL=TextAreaDemo.class.getResource("/textarea/demo.xml");
	    	    break;
	    	    
	       	}
	    	
	    	guiChanged=true;

	}
	
	
	public void init(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		if (verticalSynchronisation) {
			gl.setSwapInterval(1);
		} else {
			gl.setSwapInterval(0);
		}
		initGui();
	}


	public void display(GLAutoDrawable glDrawable) {
		// get the current time
		long time = System.nanoTime();
		// get the elapsed time from the last iteration
		long diff = time - this.last;
		// set the last time
		this.last = time;
		// get the OpenGL context
				GL2 gl = glDrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
	
		if(gui!=null){
			if(guiChanged){
				guiChanged=false;
				changeGui();
			}
			if(themeChanged){
		        themeChanged=false;
				loadTheme();
			}

			gui.update(last/1000000);
		}
	}

	public void dispose(GLAutoDrawable glDrawable) {
		if(gui!=null)gui.destroy();
	}


	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		if(gui!=null)gui.getRenderer().setViewport(0,0,width,height);
	}


	/**
	 * Must be called by thread with current opengl context
	 * best place is to call at init() method in GLEventListener
	 */
	public void initGui(){
		if(gui!=null)return;
		JOGLRenderer renderer = useEffects?new JOGLEffectsRenderer():new JOGLRenderer();
	    renderer.setUseSWMouseCursors(true);
		JOGLInput input =new JOGLInput(this.canvas);
		root.setFocusKeyEnabled(false);
		gui=new GUI(root,renderer,input);
		renderer.syncViewportSize();
		loadTheme();
		guiChanged=false;
		themeChanged=false;
	}
	
	public void changeGui(){
		root.setFocusKeyEnabled(false);
		gui.setRootPane(root);
		loadTheme();
		guiChanged=false;
		themeChanged=false;
	}
	
	/**
	 * Theme can be changed in opengl thread only, so applyTheme() sets this flag and saves themeURL to be called
	 * in opengl thread in next update
	 */
	private boolean themeChanged=false;
	
	private boolean guiChanged=false;
	
	/**
	 * ThemUrl for current theme set by applyTheme function
	 */
	private URL themeURL=NewtFrame.class.getResource("/test/simple_demo.xml");;
	

	/**
	 * 
	 * @param themeURL resource url for theme xml
	 */
	public void applyTheme(URL themeURL)  {
		//load Default theme if null
		//themeUrl=null;
		if(themeURL==null){
			themeURL=NewtFrame.class.getResource("/test/simple_demo.xml");
		}
		this.themeURL=themeURL;
		
		themeChanged=true;
	}
	

	private void loadTheme(){
		try {
			ThemeManager tm = ThemeManager.createThemeManager(themeURL, gui.getRenderer());
		    //clean up memory from previous theme
			if(themeManager!=null){
				themeManager.destroy();
			}
			themeManager=tm;
		    gui.applyTheme(themeManager);
			gui.setBackground(themeManager.getImageNoWarning("gui.background"));

		} catch (IOException e) {
			e.printStackTrace();
		}
        gui.setSize();
        gui.validateLayout();
	}
	
	public void cleanupGui(){
		if(gui==null)return;
		//this.canvas.getNEWTChild().removeKeyListener(gui);
		//this.canvas.getNEWTChild().removeMouseListener(gui);
		gui=null;
	}
	
	
	/**
     * reduce input lag by polling input devices after waiting for vsync
     * 
     * Call after Display.update()
     */
    public static void reduceInputLag(GL2 gl) {
        gl.glGetError();          // this call will burn the time between vsyncs
        //Mouse.poll();               // now update Mouse events
        //Keyboard.poll();            // and Keyboard too
    }

	public static void main(String[] args){
		SimpleTest.main(args);
		
	}



}