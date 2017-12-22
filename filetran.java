import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static java.lang.System.exit;

public class filetran
{
    public void  SendFile()
    {
        CreateReceiveThread();
        CreateSendThread();
    }
    public static final int TIMER=100;

    public void sendWindow(int seq,int n)
    {
        try
        {
            RandomAccessFile raf=new RandomAccessFile("/home/ankush/fi","rw");
            int i,value=-2;
            byte[] packetdata = new byte[500];
            for (int win =0; win <n; win++)
            {
                for (i = 0; i < 500; i++)
                {
                    raf.seek(500 * seq);
                    value = raf.read(packetdata, 0, 500);
                }
                if (value == -1)
                {
                    System.out.println("FILE TRANSFERRED!");
                    exit(0);
                }
                filetra f1 = new filetra(seq, packetdata, value);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = null;
                byte[] yourBytes = new byte[5000];
                try {
                    out = new ObjectOutputStream(bos);
                    out.writeObject(f1);
                    out.flush();
                    yourBytes = bos.toByteArray();
                }
                finally
                {
                    try
                    {
                        bos.close();
                    }
                    catch (IOException ex)
                    {
                        // ignore close exception.
                    }
                }
                DatagramSocket ss = new DatagramSocket();
                InetAddress add=InetAddress.getByName("192.168.0.66");
                ss.setSoTimeout(TIMER);
                DatagramPacket sendpacket = new DatagramPacket(yourBytes, yourBytes.length,add, 5000);
                ss.send(sendpacket);
                System.out.println(value+" bytes read from packet no. " + seq);
                seq++;
            }
            Thread.sleep(1000);
        }
        catch(Exception e)
        {
            System.out.println(e);

        }
    }
    static volatile int seq;
    public void CreateSendThread() {
        Thread ReceiveThread = new Thread() {
            public void run() {
                try {
                    File file = new File("/home/ankush/fi");
                    //File file = new File(FilesListFromFolder.fileNames.get(0));
                    //System.out.print(FilesListFromFolder.fileNames.get(0));
                    int fileLength = (int) file.length();
                    //int n=fileLength/500+1;
                    int n=4;
                    DataInputStream dis = new DataInputStream(new FileInputStream(file));
                    byte[] packetdata = new byte[500];
                    seq = 0;
                    while (500 * seq < fileLength)
                    {
                        sendWindow(seq, n);
                        seq=seq+4;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        ReceiveThread.start();
    }
    static volatile int new_ack=0;


    public void CreateReceiveThread() {
        Thread SendThread = new Thread() {
            public void run() {
                try {
                    demo d2 = null;
                    DatagramSocket ss = new DatagramSocket(null);
                    ss.setReuseAddress(true);
                    ss.bind(new InetSocketAddress(2345));
                    int k = 999999999,l_seq=seq             ;
                    while (k > 0) {
                        byte[] incomingData = new byte[5000];
                        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                        ss.receive(incomingPacket);
                        byte[] inpack = incomingPacket.getData();
                        ByteArrayInputStream bis = new ByteArrayInputStream(inpack);
                        ObjectInput in = null;
                        try {
                            in = new ObjectInputStream(bis);
                            d2 = (demo) in.readObject();
                        } finally {
                            try {
                                if (in != null)
                                    in.close();
                            } catch (IOException x) {

                            }
                        }
                        //System.out.println("Ack returned " + d2.ack);
                        if (d2.ack!=l_seq)
                        {
                            sendWindow(d2.ack-1,4);
                            //resend(d2.ack-1);
                        }
                        k -= 1;
                    }
                } catch (Exception x) {
                    //System.out.println(x);
                }
            }
        };
        SendThread.start();
    }
  /*  public void resend(int seq)
    {
        int diff=seq-new_ack;
        if(diff<3)
        {
            sendWindow(seq+1,diff);
        }
        else
        {
            sendWindow(seq,4);
        }
    }*/
    public static void main(String[] args) throws Exception
    {
        try
        {
            filetran f = new filetran();
            filetran NewFile=new filetran();
            NewFile.SendFile();
        }
        catch (Exception x)
        {
            System.out.println(x);
        }
    }
}
