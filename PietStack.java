import java.util.Scanner;
import java.util.Stack;

public class PietStack
{
    Stack<Integer> memory =  new Stack<Integer>();
    Scanner br;
    public PietStack(){
        br = new Scanner(System.in);
    }
    
    public PietStack(Scanner scan){
        br = scan;
    }

    public int viewTop(){
        return memory.pop();
    }
    //for testing
    public boolean empty(){
        return memory.empty();
    }
    //push
    public void push(byte num){
        memory.push((int)num);
    }
    //pop
    public void pop(){
        int num = memory.pop();
    }
    //add
    public void add(){
        int first = memory.pop();
        int second = memory.pop();
        int sum = second + first;
        memory.push(sum);
    }
    //subtract
    public void subtract(){
        int first = memory.pop();
        int second = memory.pop();
        int difference = second - first;
        memory.push(difference);
    }
    //multiply
    public void multiply(){
        int first = memory.pop();
        int second = memory.pop();
        int product = second * first;
        memory.push(product);
    }
    //divide
    public void divide(){
        int first = memory.pop();
        int second = memory.pop();
        int quotent = second / first;
        memory.push(quotent);
    }
    //mod
    public void mod(){
        int first = memory.pop();
        int second = memory.pop();
        int modulus = second % first;
        memory.push(modulus);
    }
    //not
    public void not(){
        int top = memory.pop();
        if(top == 1){
            memory.push(0);
        }else if(top == 0){
            memory.push(1);
        }else{
            memory.push(top);
        }
    }
    //greater
    public void greater(){
        int first = memory.pop();
        int second = memory.pop();
        boolean topBigger = first - second > 0;
        int num = topBigger? 1:0;
        memory.push(num);
    }
    //dupicate
    public void duplicate(){
        int num = memory.peek();
        memory.push(num);
    }
    //roll
    public void roll(){
        int first = (int) memory.pop();
        int second = (int) memory.pop();
        //"roll" the first number (second) down in the stack
        //make another stack
        Stack<Integer> tempStack = new Stack<Integer>();
        for(int i = second; i > 0; i--){
            int temp = memory.pop();
            tempStack.push(temp);
        }
        memory.push(first);
        for(int j = second; j > 0; j--){
            int temp = tempStack.pop();
            memory.push(temp);
        }
    }
    //in(int)
    public void intIn(){
        System.out.print("Input some stuff: ");
        String input = br.next();
        int num = Integer.parseInt(input);
        memory.push(num);
    }
    //in(char)
    public void charIn(){
        System.out.print("Input some stuff: ");
        String input = br.next();
        char num = input.toCharArray()[0];
        memory.push((int)num);
    }
    //out(int)
    public void intOut(){
        int num = memory.pop();
        System.out.print(num);
    }
    //out(char)
    public void charOut(){
        //wrap the int around 127 for ascii reasons
        int popped = (int) memory.pop();
        while(popped > 127){
            popped -= 127;
        }
        while(popped < 0){
            popped += 127;
        }
        char c = (char) popped;//got a class cast exception once? don't know why. think i fixed it. yeah pretty sure
        System.out.print(c);
    }

    public static void main(String[] args){
        new PietStack();
    }
}
