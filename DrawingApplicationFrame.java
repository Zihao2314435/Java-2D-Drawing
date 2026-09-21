/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{
   
   
    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private final JPanel topPanel;
    private final JPanel line1;
    private final JPanel line2;
    
    // create the widgets for the firstLine Panel.
    private final JLabel shape;
    private final JComboBox shapeChoose;
    private final JButton firstColor;
    private final JButton secColor;
    private final JButton undo;
    private final JButton clear;

    //create the widgets for the secondLine Panel.
    private final JLabel options;
    private final JCheckBox filled;
    private final JCheckBox gradient;
    private final JCheckBox dashed;
    private final JLabel widthText;
    private final JSpinner width;
    private final JLabel lengthText;
    private final JSpinner length;

    // Variables for drawPanel.
    private JPanel drawPanel;
    private final ArrayList<MyShapes> shapes;
    private MyShapes currentShape;
    private Paint paint;
    private Stroke stroke;
    private Point startPoint;
    private Point endpoint;
    private Color color1 = Color.BLACK;
    private Color color2 = Color.BLACK;
    
    // add status label
    private JLabel status;
   
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        super("Java 2D Drawings");
        setLayout(new BorderLayout());

        // add widgets to panels
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        
        line1 = new JPanel();
        line1.setBackground(Color.CYAN);
        
        line2 = new JPanel();
        line2.setBackground(Color.CYAN);
        
        drawPanel = new DrawPanel();
        drawPanel.setBackground(Color.WHITE);
        
        status = new JLabel("(0,0)");
        status.setBackground(Color.GRAY);
        
        // firstLine widgets
        shape = new JLabel("Shape:");
        line1.add(shape);
        
        shapes = new ArrayList<>();
        
        shapeChoose = new JComboBox((new String[]{"Line", "Oval", "Rectangle"}));
        line1.add(shapeChoose);
        
        firstColor = new JButton("1st Color");
        line1.add(firstColor);
        
        secColor = new JButton("2nd Color");
        line1.add(secColor);
        
        undo = new JButton("Undo");
        line1.add(undo);
        
        clear = new JButton("Clear");
        line1.add(clear);
        
        // secondLine widgets
        options = new JLabel("Options:");
        line2.add(options);
        
        filled = new JCheckBox("Filled");
        line2.add(filled);
        
        gradient = new JCheckBox("Use Gradient");
        line2.add(gradient);
        
        dashed = new JCheckBox("Dashed");
        line2.add(dashed);
        
        widthText = new JLabel("Line Width:");
        line2.add(widthText);
        
        
        width = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        line2.add(width);
        
        lengthText = new JLabel("Dash Length:");
        line2.add(lengthText);
        
        length = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        line2.add(length);
        
        
        // add top panel of two panels
        topPanel.add(line1);
        topPanel.add(line2);
        
       
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        
        //add listeners and event handlers
        firstColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                color1 = JColorChooser.showDialog
                (null,"Choose First Color", firstColor.getBackground());
                }
        });
        
        secColor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                color2 = JColorChooser.showDialog
               (null, "Choose Second Color", secColor.getBackground());
                }
        });
        
        undo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (!shapes.isEmpty()) {
                    shapes.remove(shapes.size() - 1);
                    repaint();
                }
                }
        });

        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                shapes.clear();
                repaint();
            }
        });

        
    }
  
    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {   
        public DrawPanel()
        {
            MouseHandler handler = new MouseHandler();
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for(MyShapes shape : shapes){
                shape.draw(g2d); 
            }
            if(currentShape != null){
                currentShape.draw(g2d);
            }
        }
        
        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                startPoint = event.getPoint();
                if(gradient.isSelected()){
                    paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                } 
                else{
                    paint = color1;
                }
                int lineWidth = (Integer) width.getValue();
                float[] dashLength = {((Integer) length.getValue()).floatValue()};

                if(dashed.isSelected()){
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLength, 0);
                } 
                else{
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
            }
            

            public void mouseReleased(MouseEvent event)
            {
                endpoint = event.getPoint();
                String selectedShape = (String) shapeChoose.getSelectedItem();
                boolean isFilled = filled.isSelected();

                if(selectedShape.equals("Line")){
                    currentShape = new MyLine(startPoint, endpoint, paint, stroke);
                } 
                else if (selectedShape.equals("Oval")){
                    currentShape = new MyOval(startPoint, endpoint, paint, stroke, isFilled);
                } 
                else if (selectedShape.equals("Rectangle")){
                    currentShape = new MyRectangle(startPoint, endpoint, paint, stroke, isFilled);
                }
                shapes.add(currentShape);
                currentShape = null;
                repaint();
            }


            public void mouseDragged(MouseEvent event)
            {
                endpoint = event.getPoint();
                String selectedShape = (String) shapeChoose.getSelectedItem();
                boolean isFilled = filled.isSelected();

                if(selectedShape.equals("Line")){
                    currentShape = new MyLine(startPoint, endpoint, paint, stroke);
                } 
                else if (selectedShape.equals("Oval")){
                    currentShape = new MyOval(startPoint, endpoint, paint, stroke, isFilled);
                } 
                else if (selectedShape.equals("Rectangle")){
                    currentShape = new MyRectangle(startPoint, endpoint, paint, stroke, isFilled);
                }
                repaint();
            }

 
            public void mouseMoved(MouseEvent event)
            {
                Point mousePosition = event.getPoint();
                status.setText(mousePosition.x + ", " + mousePosition.y);
            }
        }
    }
}
