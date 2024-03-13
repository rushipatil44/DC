package EXPT_8;
import java.util.Scanner;

public class LoadBalance{
    static void printLoad(int servers, int processes){
    int each = processes/servers;
    int extra=processes%servers;
    int total=0;
    int i=0;
    for(i=0;i<extra;i++){
        System.out.println("Server"+(i+1)+" has "+(each+1)+" Processes");
    }
    for(;i<servers;i++){
        System.out.println("Server"+(i+1)+" has "+each+" Processes");
    }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of Servers: ");
        int servers = sc.nextInt();
        System.out.print("Enter the numbher of processes: ");
        int processes = sc.nextInt();
        while (true) {
            printLoad(servers,processes);
            System.out.println("\n1. Add servers \n2. Remove Servers \n3. Add Processes \n4. Remove Processes \n5. Exit");
            System.out.print(">");
            switch (sc.nextInt()) {
                case 1:
                    System.out.print("How many more servers to add? ==> ");
                    servers+=sc.nextInt();
                    break;
                    case 2:
                    System.out.println("How many more servers to remove? ==> ");
                    servers-=sc.nextInt();
                    break;
                    case 3:
                    System.out.print("How many more Processes to add? ==> ");
                    processes+=sc.nextInt();
                    break;
                    case 4:
                    System.out.println("How many more Processes to remove? ==> ");
                    processes-=sc.nextInt();
                    break;
                    case 5:
                    return;
            }
        }
        }
}