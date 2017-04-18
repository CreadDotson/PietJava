import java.awt.image.*;
import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Scanner;
/*
Instruction Pointer Algorithm:
-find an array of codels on the farthest edge in the direction of the DP
-compare x values of those codels to find the farthest in the direction of the CC
-from that pixel move in the direction of the DP and execute the command given
-if the pointer tries to move onto a black or edge toggle the CC and try again
-if there is no escaping the trap (tried 3 time) end the program
 */

public class PietInterpreter
{
    int[][] colors = {//<------hue------>
            {16192, 64, 4128832, 4128769, 4144897, 16129},//-----
            {65536, 256, 16711936, 16711681, 16776961, 65281},//lightness
            {4194304, 4145152, 16728064, 16727872, 16777024, 4194112} //-----
        };
    int white = 16777215; //0xFFFFFF
    int black = 0; //0x000000
    int DP = 0;//0-right, 1-down, 2-left, 3-up
    boolean CC = false;//false = right, true = left
    byte codelValue = 0;//value of the current codel block
    int[] pointerLocation = new int[]{0,0};//current location of the pointer
    int[][] codels;
    PietStack stack = new PietStack(new Scanner(System.in));
    
    LinkedList<int[]> trace = new LinkedList<int[]>();
    boolean debug = false;
    
    public PietInterpreter(BufferedImage img, boolean debug){
        //System.out.println("Created the interpreter");
        this.debug = debug;
        toCodels(img);
    }

    private void toCodels(BufferedImage img){
        //System.out.println("toCodels has run");
        int width = img.getWidth();
        int height = img.getHeight();
        //System.out.println("Width:Height " + width + ", " + height);
        codels = new int[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                //System.out.println(x + ", " + y);
                int rgb = img.getRGB(x, y);
                //int red = (rgb >> 16) & 0xFF;
                //int green = (rgb >> 8) & 0xFF;
                //int blue = rgb & 0xFF;
                codels[x][y] = Math.abs(rgb);
            }
        }
    }
    
    private void pointerToggle(){
        DP+=1;
        DP = Math.abs(DP % 4);
    }

    private void pointer(){
        int num = stack.viewTop();
        if(num == 0)
            return;
        DP += num;
        DP = Math.abs(DP % 4);
    }

    private void codelToggle(){
        int num = Math.abs(stack.viewTop());
        while(num > 0){
            CC = !CC;
            num--;
        }
    }

    private void getChange(int first, int second){
        first = Math.abs(first);
        second = Math.abs(second);
        int startx = 0, starty = 0;
        int endx = 0, endy = 0;
        //System.out.println(first + ", " + second);
        for(int r = 0; r < colors.length; r++){
            for(int c = 0; c < colors[0].length; c++){
                if(colors[r][c] == first){
                    startx = c;
                    starty = r;
                }
                if(colors[r][c] == second){
                    endx = c;
                    endy = r;
                }
            }
        }
        int diffx = endx - startx;
        int diffy = endy - starty;
        //System.out.println("diffx:diffy " + diffx + ", " + diffy);
        if(diffx == 0){
            if(diffy == 1){
                stack.push(codelValue);
                if(debug)
                    System.out.println("push " + codelValue);
            }else if(diffy == 2){
                stack.pop();
                if(debug)
                    System.out.println("pop");
            }
        }else if(diffx == 1){
            if(diffy == 0){
                stack.add();
                if(debug)
                    System.out.println("add");
            }else if(diffy == 1){
                stack.subtract();
                if(debug)
                    System.out.println("subtract");
            }else if(diffy == 2){
                stack.multiply();
                if(debug)
                    System.out.println("multiply");
            }
        }else if(diffx == 2){
            if(diffy == 0){
                stack.divide();
                if(debug)
                    System.out.println("divide");
            }else if(diffy == 1){
                stack.mod();
                if(debug)
                    System.out.println("mod");
            }else if(diffy == 2){
                stack.not();
                if(debug)
                    System.out.println("not");
            }
        }else if(diffx == 3){
            if(diffy == 0){
                stack.greater();
                if(debug)
                    System.out.println("greater");
            }else if(diffy == 1){
                pointer();
                if(debug)
                    System.out.println("pointer");
            }else if(diffy == 2){
                codelToggle();
                if(debug)
                    System.out.println("toggle");
            }
        }else if(diffx == 4){
            if(diffy == 0){
                stack.duplicate();
                if(debug)
                    System.out.println("duplicate");
            }else if(diffy == 1){
                stack.roll();
                if(debug)
                    System.out.println("roll");
            }else if(diffy == 2){
                if(debug)
                    System.out.println("int in");
                stack.intIn();
            }
        }else if(diffx == 5){
            if(diffy == 0){
                if(debug)
                    System.out.println("char in");
                stack.charIn();
            }else if(diffy == 1){
                stack.intOut();
                if(debug)
                    System.out.println("int out");
            }else if(diffy == 2){
                stack.charOut();
                if(debug)
                    System.out.println("char out");
            }
        }else{
            //System.out.println("diffx is broken");
            //does not mean diffx is broken
        }
    }

    private boolean listContains(LinkedList<int[]> list, int[] does){
        boolean contains = false;
        for(int[] l : list){
            if(l[0] == does[0] && l[1] == does[1]){
                return true;
            }
        }
        return false;
    }

    private boolean isViable(int[] testLoc){
        return (testLoc[0] >= 0 && testLoc[0] < codels.length) && (testLoc[1] >= 0 && testLoc[1] < codels[0].length);
    }

    public LinkedList<int[]> getCodels(){
        //System.out.println("getCodels has run");
        LinkedList<int[]> checked = new LinkedList<int[]>();
        checked.add(pointerLocation);
        final int[][] checks = {{1,0},{-1,0},{0,1},{0,-1}};
        int currentColor = codels[pointerLocation[0]][pointerLocation[1]];
        //System.out.println("Current Color: " + currentColor);
        while(true){
            boolean done = true;
            LinkedList<int[]> temp = new LinkedList<int[]>();
            for(int[] p : checked){
                for(int[] n : checks){
                    int[] testLoc = new int[] {p[0] + n[0], p[1] + n[1]};
                    if(isViable(testLoc) && (codels[testLoc[0]][testLoc[1]] == currentColor || codels[testLoc[0]][testLoc[1]] == white) && !listContains(checked, testLoc) && !listContains(temp, testLoc)){
                        temp.add(testLoc);
                        done = false;
                    }
                }
            }
            for(int[] t : temp){
                //System.out.println(t[0] + ", " + t[1] + " is in the codel");
                checked.add(t);
            }
            if(done)
                break;
        }
        byte value = 0;
        for(int[] c : checked){
            if(codels[c[0]][c[1]] == currentColor)
                value++;
        }
        codelValue = value;
        //System.out.println("codelValue " + value);
        return checked;
    }

    public int[] getnextPointer(LinkedList<int[]> codel){
        //System.out.println("getnextPointer has run");
        int currentColor = codels[pointerLocation[0]][pointerLocation[1]];
        int[] testDirection = new int[]{0,0};
        switch(DP){
            case 0:
                testDirection = new int[]{1,0};
                break;
            case 1:
                testDirection = new int[]{0,1};
                break;
            case 2:
                testDirection = new int[]{-1,0};
                break;
            case 3:
                testDirection = new int[]{0,-1};
                break;
        }
        LinkedList<int[]> edge = new LinkedList<int[]>();
        for(int[] c : codel){
            int[] testLoc = new int[]{c[0]+testDirection[0], c[1] + testDirection[1]};
            if(isViable(testLoc) && codels[testLoc[0]][testLoc[1]] != currentColor){
                edge.add(c);
            }
        }
        int[] best = applyCodelChooser(edge);
        int[] nextPointer = {best[0] + testDirection[0], best[1] + testDirection[1]};
        return nextPointer;
    }
    
    private int[] applyCodelChooser(LinkedList<int[]> edge){
        int[] best = {0,0};
        int[] b = {0,0};
        switch(DP){
            case 0://right
                if(CC){//left
                    b = new int[]{0, Integer.MIN_VALUE};
                    for(int[] e: edge){
                        if(e[1] > b[1])
                            b = e;
                    }
                    best = b;
                    break;
                }
                //right
                b = new int[]{0, Integer.MAX_VALUE};
                for(int[] e: edge){
                    if(e[1] < b[1])
                        b = e;
                }
                best = b;
                break;
            case 1://down
                if(CC){//left
                    b = new int[]{Integer.MIN_VALUE, 0};
                    for(int[] e: edge){
                        if(e[0] > b[0])
                            b = e;
                    }
                    best = b;
                    break;
                }
                //right
                b = new int[]{Integer.MAX_VALUE, 0};
                for(int[] e: edge){
                    if(e[0] < b[0])
                        b = e;
                }
                best = b;
                break;
            case 2://left
                if(CC){//left
                    b = new int[]{0, Integer.MAX_VALUE};
                    for(int[] e: edge){
                        if(e[1] < b[1])
                            b = e;
                    }
                    best = b;
                    break;
                }
                //right
                b = new int[]{0, Integer.MIN_VALUE};
                for(int[] e: edge){
                    if(e[1] > b[1])
                        b = e;
                }
                best = b;
                break;
            case 3://up
                if(CC){//left
                    b = new int[]{Integer.MAX_VALUE, 0};
                    for(int[] e: edge){
                        if(e[0] < b[0])
                            b = e;
                    }
                    best = b;
                    break;
                }
                //right
                b = new int[]{Integer.MIN_VALUE, 0};
                for(int[] e: edge){
                    if(e[0] > b[0])
                        b = e;
                }
                best = b;
                break;
        }
        return best;
    }

    public void runPointer()throws Exception{
        LinkedList<int[]> codel = getCodels();
        int currentColor = codels[pointerLocation[0]][pointerLocation[1]];
        boolean foundNext = false;
        int timesTried = 0;
        int[] nextPointer = null;
        int nextColor = 0;
        while(!foundNext){
            nextPointer = getnextPointer(codel);
            nextColor = codels[nextPointer[0]][nextPointer[1]];
            trace.add(nextPointer);
            if(debug)
                System.out.println(nextPointer[0] + ", " + nextPointer[1] + " : " + nextColor);
            if(nextPointer != null && isViable(nextPointer) && nextColor != black){
                break;
            }
            CC = !CC;
            timesTried++;
            if(timesTried > 3){
                throw new Exception("Program has ended");
            }
        }
        
        getChange(currentColor, nextColor);
        pointerLocation = nextPointer;
    }

    public void start(){
        while(codels != null){
            try{
                runPointer();
                if(debug)
                    System.out.println("--------------------");
            }catch(Exception e){
                System.out.println("\nProgram Ended");
                break;
            }
        }
    }
}
