package OS2;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

class Router{
    private int buffer;
    public static semaphore semaphore;
    public boolean[] connections;
    public int devicesInConnection=0;
    Router(int buffer){
        this.buffer=buffer;
        semaphore=new semaphore(buffer);
        connections=new boolean[buffer];
    }
    public static String fileWrite(String fileName, String write) {
        String s = "";
        File OS = new File("OS.txt");
        try {
            FileWriter f = new FileWriter(System.getProperty("user.home") + "/Desktop/" + fileName,true);
            f.write(write);
            f.close();
        } catch (Exception ex) {
        }
        return s;
    }
    public synchronized int occupy(Device device) throws InterruptedException {
        for(int i=0;i<buffer;i++){
            if(!connections[i]){
                devicesInConnection++;
                device.id=i+1;
                fileWrite("OS.txt","\nConnection "+device.id+": "+device.getType()+" occupied");
                connections[i]=true;
                Thread.sleep(1000);
                break;
            }
        }
        return device.id;
    }
    public synchronized String release(Device device) throws InterruptedException {
        devicesInConnection--;
        connections[device.id - 1] = false;
        Thread.sleep(1500);
        return "logout";
    }
}
class Device extends Thread{
    public static Router router;
    public int id;
    public String type;
    public String name;
    public Device(String name, String type, Router router){
        this.name=name;
        this.type=type;
        Device.router=router;
    }
    public String getType(){
        return type;
    }
    public String GetName(){
        return name;
    }
    public String perform() throws InterruptedException {
        Thread.sleep(3000);
        return "Perform online activity";
    }
    @Override
    public void run(){
        Router.semaphore.P(this);
        try {
            id=router.occupy(this);
            Router.fileWrite("OS.txt","\nConnection "+id+": "+this.GetName()+" "+perform());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        try {
            Router.fileWrite("OS.txt","\nConnection "+id+": "+ this.GetName()+" "+router.release(this));

        }catch (InterruptedException e){e.printStackTrace();}
        Router.semaphore.V();
    }

}
class semaphore{
    public static int value;
    semaphore(){
        value=0;
    }
    semaphore(int value){
        this.value=value;
    }
    public synchronized void P (Device device){
        value--;
        if(value<0){
            try {
                Router.fileWrite("OS.txt","\n(" + device.GetName() + ") ("+ device.getType() + ")" + " arrived and waiting");
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        else {
            Router.fileWrite("OS.txt","\n(" + device.GetName() + ") ("+ device.getType() + ")" + " arrived");
        }
    }
    public synchronized void V(){
        value++;
        if(value<=0){
            notify();
        }
    }
}



class Network {
    public static void main(String[] args) {
        int buffer;
        String name,type;

        Scanner input=new Scanner(System.in);
        System.out.println("Enter buffer for router");
        buffer=input.nextInt();
        Router router=new Router(buffer);
        System.out.println("Enter number of devices wants to connect");
        int numberOfDevices;
        numberOfDevices= input.nextInt();
        Scanner s=new Scanner(System.in);
        Device[]devices=new Device[numberOfDevices];
        for (int i=0;i<numberOfDevices;i++){
            System.out.println("Device "+ (i+1)+" name: ");
            name=s.nextLine();
            System.out.println("Device "+(i+1)+" type: ");
            type=s.nextLine();
            devices[i]=new Device(name,type,router);
        }

        for (Device device : devices){
            device.start();
        }
    }
}