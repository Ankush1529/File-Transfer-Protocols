import java.io.File;
import java.util.ArrayList;

public class FilesListFromFolder
{
    public static ArrayList<String> fileNames=new ArrayList<String>();
    public static void main(String a[])
    {
        File file = new File("/home/ankush/ank");
        File[] files = file.listFiles();
        for(File f: files)
        {
            fileNames.add(f.getPath());
            System.out.println(f.getName());
        }
    }
}