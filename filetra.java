import java.io.Serializable;
public class filetra implements Serializable
{
    int seq_no,len;
    byte[] data=new byte[2000];
    public filetra(int seq_no,byte[] data,int len)
    {
        this.data=data;
        this.seq_no=seq_no;
        this.len=len;
    }
}
