package test;

import newt.NewtFrame;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.DialogLayout.Group;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TabbedPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.jogl.JOGLRenderer;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.theme.ThemeManager;

/**
 * 
 * @author NateS
 */
public class TwlTest {

    private JOGLRenderer renderer;
    private ThemeManager theme;
    private GUI gui;
    private Widget root;

    public TwlTest() {
        root = new Widget();
        root.setTheme("");

        addTestAlert(10, 10, "&lt;minwidth");

        addTestAlert(10, 100, "Between 2 min and max width");

        addTestAlert(10, 180, "Past max width but less than max height. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. ");

        addTestAlert(10, 350, "Past max width and past max height. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. "
                + "This is a lot of text. This is a lot of text. This is a lot of text. This is a lot of text. ");
    }
    
 
    private void addTestAlert(int x, int y, String text) {
        Alert alert = new Alert(text);
        alert.addButton("OK");
        alert.addButton("Cancel");
        alert.setPosition(x, y);
        root.add(alert);
	alert.adjustSize();
	alert.setTitle("hi hello");
    }

    public class Alert extends ResizableFrame {

        private Group buttonGroupH, buttonGroupV;
        private TextArea textArea;
        private ScrollPane scrollPane;

        public Alert(String text) {
            setTheme("/resizableframe");

            final HTMLTextAreaModel textAreaModel = new HTMLTextAreaModel(text);
            textArea = new TextArea(textAreaModel);

            scrollPane = new ScrollPane(textArea);
            scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);

            DialogLayout layout = new DialogLayout();

            buttonGroupH = layout.createSequentialGroup();
            buttonGroupH.addGap();
            buttonGroupV = layout.createParallelGroup();

            layout.setTheme("/alertbox");
            layout.setHorizontalGroup(layout.createParallelGroup()
                    .addWidget(scrollPane)
                    .addGroup(buttonGroupH));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addWidget(scrollPane)
                    .addGroup(buttonGroupV));
            add(layout);
        }

        public void addButton(String text) {
            Button button = new Button(text);
            buttonGroupH.addWidget(button);
            buttonGroupV.addWidget(button);
        }
    }


    
    public static void main(String[] args) {
    	TwlTest twlTest = new TwlTest();
		NewtFrame guiDemo = new NewtFrame(800, 600, twlTest.root);
		guiDemo.setTitle("TWL Examples");
		guiDemo.applyTheme(SimpleTest.class.getResource("simple.xml"));
	}
}
