import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.LinkedList;
import java.awt.Point;
public class PietInterface extends JPanel implements ActionListener, MouseListener
{
    BufferedImage program;
    BufferedImage displayProgram;
    JFrame frame;
    PietInterpreter interpreter;
    Point selected;
    boolean debug = true;
    public PietInterface(){
        frame = new JFrame("PIET");
        setPreferredSize(new Dimension(690,600));

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem o = new JMenuItem("Load");
        o.addActionListener(this);
        fileMenu.add(o);
        
        JMenuItem run = new JMenuItem("Run");
        run.addActionListener(this);

        menu.add(fileMenu);
        menu.add(run);

        addMouseListener(this);
        frame.getContentPane().add(menu, BorderLayout.NORTH);
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public PietInterface(String filename){
        super();
        load(new File(filename));
    }

    public void mouseClicked(MouseEvent e){

    }

    public void mousePressed(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        Point mouseLoc = e.getPoint();
        int xLoc = (int)mouseLoc.getX();
        int yLoc = (int)mouseLoc.getY();
        selected = new Point(xLoc / 10, yLoc / 10);
        //System.out.println(selected.getX() + ", " + selected.getY());
    }

    public void mouseEntered(MouseEvent e){

    }

    public void mouseExited(MouseEvent e){

    }

    public void load(File file){
        try{
            BufferedImage img = ImageIO.read(file);
            int width = img.getWidth();
            int height = img.getHeight();
            frame.setSize(width*10+14, height*10+50);
            Image image = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Image displayImage = img.getScaledInstance(width*10, height*10, Image.SCALE_SMOOTH);
            BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            BufferedImage displayBuffered = new BufferedImage(width*10, height*10, BufferedImage.TYPE_INT_RGB);
            buffered.getGraphics().drawImage(image, 0, 0 , null);
            displayBuffered.getGraphics().drawImage(displayImage, 0,0,null);
            program = buffered;
            displayProgram = displayBuffered;
            interpreter = new PietInterpreter(buffered, debug);
        }catch(Exception e){
            System.out.println("load error");
            System.out.println(e.getStackTrace());
        }
    }

    public void load(){
        JFileChooser files = new JFileChooser();
        try{
            int returnVal = files.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = files.getSelectedFile();
                load(file);
            }
        }catch(Exception e){
            System.out.print("choose file error\n");
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void actionPerformed(ActionEvent e){
        if(e == null)
            return;
        String event = e.getActionCommand();
        if(event.equals("Load")){
            load();
        }else if(event.equals("Run")){
            run();
        }
        repaint();
    }

    public void draw(Graphics g){
        if(displayProgram != null){
            g.drawImage(displayProgram,0,0,null);
            g.setColor(Color.BLACK);
            int xLines = displayProgram.getWidth();
            int yLines = displayProgram.getHeight();
            LinkedList<int[]> visited = interpreter.trace;
            for(int x = 0; x < xLines/10; x++){
                g.drawLine(x*10, 0, x*10, yLines);
            }
            for(int y = 0; y < yLines/10; y++){
                g.drawLine(0, y*10, xLines, y*10);
            }
            int[] start = {0,0};
            for(int[] p : visited){
                g.setColor(Color.WHITE);
                g.drawLine(start[0]*10+5,start[1]*10+5, p[0]*10+5, p[1]*10+5);
                g.fillOval(p[0]*10+3, p[1]*10+3,7,7);
                start = p;
            }
        }
    }

    public void run(){
       interpreter.start();
    }

    public static void main(String[] args){
        new PietInterface();
    }
}
