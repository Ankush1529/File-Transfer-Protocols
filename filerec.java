import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
public class filerec
{
    public void fileread()
    {
        sendiAckThread();
    }
    public void sendiAckThread()
    {
        Thread sendThread=new Thread()
        {
            public void run()
            {
                int l_seq=0,m=99999999;
                filetra f2=null;
                try
                {
                    DatagramSocket ss = new DatagramSocket(null);
                    File f=new File("/home/ankush/res");
                    RandomAccessFile raf=new RandomAccessFile("/home/ankush/res","rw");
                    ss.setReuseAddress(true);
                    ss.bind(new InetSocketAddress(5000));
                    while (m>0)
                    {
                        byte[] incomingData = new byte[5000];
                        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                        ss.receive(incomingPacket);
                        int fileLength = (int) f.length();
                        byte[] indata = incomingPacket.getData();
                        ByteArrayInputStream bis = new ByteArrayInputStream(indata);
                        ObjectInputStream in = null;
                        try
                        {
                            in = new ObjectInputStream(bis);
                            f2 = (filetra) in.readObject();
                        }
                        finally
                        {
                            try
                            {
                                if (in != null)
                                {
                                    in.close();
                                }
                            }
                            catch (IOException ex)
                            {
                                // ignore close exception
                            }
                        }
                        System.out.println(f2.len+" Bytes written");
                        if (f2.seq_no == l_seq)
                        {
                            raf.seek(l_seq*500);
                            raf.write(f2.data,0,f2.len);
                            l_seq++;
                        }
                        if(f2.len<500)
                        {
                            break;
                        }
                            demo d1 = new demo(f2.seq_no+1);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ObjectOutput out = null;
                            byte[] myBytes = new byte[1024];
                            try
                            {
                                out = new ObjectOutputStream(bos);
                                out.writeObject(d1);
                                out.flush();
                                myBytes = bos.toByteArray();
                            } finally
                            {
                                try
                                {
                                    bos.close();
                                } catch (IOException ex)
                                {
                                    // ignore close exception
                                }
                            }
                        DatagramSocket ds = new DatagramSocket();
                        InetAddress addr=InetAddress.getByName("localhost");
                        DatagramPacket sendpacket = new DatagramPacket(myBytes, myBytes.length,addr, 2345);
                        ds.send(sendpacket);
                        m-=1;
                    }
                }
                catch (Exception x)
                {
                    System.out.println(x);
                }
            }
        };
        sendThread.start();
    }
    public static void main(String[] args) throws Exception
    {
        filerec filesend = new filerec();
        filesend.fileread();
    }
}
