import java.awt.*;       // Using AWT's Graphics and Color
import java.awt.event.*; // Using AWT's event classes and listener interface
import java.awt.geom.Arc2D;

import javax.swing.*;    // Using Swing's components and containers
/**
 * Custom Graphics Example: Using key/button to move a line left or right.
 */
@SuppressWarnings("serial")
public class DashDisplay extends JFrame {
   // Define constants for the various dimensions
   public static final int CANVAS_WIDTH = 480;
   public static final int CANVAS_HEIGHT = 320;
   public static final Color LINE_COLOR = Color.WHITE;
   //public static final Color CANVAS_BACKGROUND = Color.CYAN;
 
   // The moving speedometer arc static ints
   private static final int R = 0;
   private static final int G = 23;
   private static final int B = 172;
   private static final int angleStopSpeed = -160;
   private static final int angleMaxSpeed = -220;
   private static final int absoluteMaxSpeed = 45;
   // The moving line from (x1, y1) to (x2, y2), initially position at the center
   private int x1 = CANVAS_WIDTH / 2;
   private int y1 = CANVAS_HEIGHT / 8;
   private int x2 = x1;
   private int y2 = CANVAS_HEIGHT / 8 * 7;
   
   // The moving speedometer arc
   private int angleCurrentSpeed = 0; // Set as an object that represents the current speed of vehicle.
   private int absoluteCurrentSpeed = 0;
   
   private int prev_absoluteCurrentSpeed = 0;
   private int draw_absoluteCurrentSpeed = 0;
   
   // Basing positions on a point.
   private int xbasePoint = 240;
   private int ybasePoint = 160;
   
   private int outterRadius = 100;
   private int innerRadius = 90;
   
   
   
   private DrawCanvas canvas; // The custom drawing canvas (an innder class extends JPanel)
 
   // Background Gradient Colors
   Color color1 = new Color(1,58,148);
   Color color2 = Color.BLACK;
   
   // Constructor to set up the GUI components and event handlers
   public DashDisplay() {
      
	  /*// Set up a panel for the buttons
      JPanel btnPanel = new JPanel(new FlowLayout());
      JButton btnLeft = new JButton("Move Left ");
      btnPanel.add(btnLeft);
      btnLeft.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            x1 -= 10;
            x2 -= 10;
            canvas.repaint();
            requestFocus(); // change the focus to JFrame to receive KeyEvent
         }
      });
      JButton btnRight = new JButton("Move Right");
      btnPanel.add(btnRight);
      btnRight.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            x1 += 10;
            x2 += 10;
            canvas.repaint();
            requestFocus(); // change the focus to JFrame to receive KeyEvent
         }
      });
 	  */
	   
      // Set up a custom drawing JPanel
      canvas = new DrawCanvas();
      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
      
      // Add both panels to this JFrame's content-pane
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER);
      //cp.add(btnPanel, BorderLayout.SOUTH);
      
      // "super" JFrame fires KeyEvent
      addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent evt) {
            switch(evt.getKeyCode()) {
               case KeyEvent.VK_LEFT:
            	  if(absoluteCurrentSpeed  > 0){
            		  absoluteCurrentSpeed -=1;
            		  
            		  //if(timer out){
            		  	angleCurrentSpeed = -(220 * absoluteCurrentSpeed)/absoluteMaxSpeed;
            		  //}
            		  
            		  repaint();
            	  }            	  
                  
                  break;
               case KeyEvent.VK_RIGHT:
            	   if(absoluteCurrentSpeed < 45){
            	   absoluteCurrentSpeed +=1;
            	   
            	   draw_absoluteCurrentSpeed = (int) lowPass((float)prev_absoluteCurrentSpeed,(float)absoluteCurrentSpeed);
    			   prev_absoluteCurrentSpeed = absoluteCurrentSpeed;	   
            			   
            	   //prev_angleCurrentSpeed = angleCurrentSpeed;
         		  	angleCurrentSpeed = -(220 * draw_absoluteCurrentSpeed)/absoluteMaxSpeed;
	         		  	repaint();
            	   }
                  break;
            }
         }
      });
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle the CLOSE button
      
      //Added to make fullscreen, no ifs ands or butts (aka no taskbar)
      //setExtendedState(JFrame.MAXIMIZED_BOTH); 
      //setUndecorated(true);
      //setVisible(true);
      
      setTitle("Move a Line");
      pack();           // pack all the components in the JFrame
      setVisible(true); // show it
      requestFocus();   // set the focus to JFrame to receive KeyEvent
   }
   
///////// Low-Pass filter method.
   /*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
   static final float ALPHA = 0.15f;
    
   /**
    * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
    * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
    */
   public float lowPass( float input, float output ) {
       if ( output == input ) return input;
       
       //for ( int i=0; i<input.length; i++ ) {
           output = output + ALPHA * (input - output);
       //}
       return output;
   }
   /////////////
 
   /**
    * Define inner class DrawCanvas, which is a JPanel used for custom drawing.
    */
   class DrawCanvas extends JPanel {
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         setDoubleBuffered(true);
         //Added to have a gradient background color
         Graphics2D g2d = (Graphics2D) g;
         
         g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         int w = getWidth();
         int h = getHeight();
         
         GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
         g2d.setPaint(gp);
         g2d.fillRect(0, 0, w, h);
         
         // Speedometer Outline
         g2d.setRenderingHint(
        		    RenderingHints.KEY_ANTIALIASING,
        		    RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.setStroke(new BasicStroke(2));
         g2d.setColor(Color.GRAY);
         Arc2D arcOUTTER = new Arc2D.Double();
         arcOUTTER.setArcByCenter(xbasePoint, ybasePoint, outterRadius ,0, 360, Arc2D.OPEN);
         g2d.draw(arcOUTTER);
         
         // Speedometer Interior
        
         /*
         g2d.setRenderingHint(
        		    RenderingHints.KEY_ANTIALIASING,
        		    RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.setStroke(new BasicStroke(10));
         g2d.setColor(new Color(R,G,B));
         Arc2D arcINNER = new Arc2D.Double();
         arcINNER.setArcByCenter(150, 150, 90 ,angleStopSpeed, angleCurrentSpeed, Arc2D.OPEN);
         g2d.draw(arcINNER);
         */
         
         g2d.setRenderingHint(
     		    RenderingHints.KEY_ANTIALIASING,
     		    RenderingHints.VALUE_ANTIALIAS_ON);
		  g2d.setStroke(new BasicStroke(13));
		  g2d.setColor(Color.WHITE);
		  Arc2D arcINNER = new Arc2D.Double();
		  arcINNER.setArcByCenter(xbasePoint, ybasePoint, innerRadius ,angleStopSpeed, angleCurrentSpeed, Arc2D.OPEN);
		  g2d.draw(arcINNER);
		  
         // Speedometer Text Speed Interior
		
		 g2d.setRenderingHint(
		     RenderingHints.KEY_TEXT_ANTIALIASING,
		     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		 
         g2d.setColor(Color.WHITE);
         
         Integer intCurrentSpeed = new Integer(absoluteCurrentSpeed);
         String intString = intCurrentSpeed.toString();
         Font f = new Font(intString, Font.BOLD, 30);
         g2d.setFont(f);
		 g2d.drawString(intString, xbasePoint - 35, ybasePoint + 15);
		 
         Font ff = new Font("KPH", Font.BOLD, 15);
         g2d.setFont(ff);
		 g2d.drawString("KPH", xbasePoint + 10, ybasePoint + 10 );
		 
		 // Turn signals
		 
         
         
      }
   }
 
   // The entry main() method
   public static void main(String[] args) {
      // Run GUI codes on the Event-Dispatcher Thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new DashDisplay(); // Let the constructor do the job
         }
      });
   }
}