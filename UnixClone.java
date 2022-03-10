import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipal;

public class UnixClone {

    public static void main(String[] args) throws Exception {
        //cpDir("./testDir", "./testDir2");
        //touch("newfile.txt");
        lsa();
    }

    public static void cp(String copyFrom, String copyTo) throws IOException {
        System.out.println(copyFrom + " " + copyTo);
        File original = new File(copyFrom);
        File copied = new File(copyTo);
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(original));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(copied));

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }
        finally{};
    }
    
    public static void cpDir(String copyFrom, String copyTo) throws IOException {
        File original = new File(copyFrom);
        File copied = new File(copyTo);

        if (!copied.isDirectory()) {
            Files.createDirectory(Paths.get(copyTo));
        }

        File[] fileArr = original.listFiles();

        for (int i = 0; i < fileArr.length; i++) {
            System.out.println(fileArr[i].getName());
            if (fileArr[i].isFile()) {
                cp(copyFrom + "/" + fileArr[i].getName(), (copyTo + "/" + fileArr[i].getName()));
            }
            else {
                cpDir(copyFrom + "/" + fileArr[i].getName(), copyTo + "/" + fileArr[i].getName());
            }
        }
    }

    public static void touch(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
    }

    public static void lsa() throws Exception {
        File dir = new File(System.getProperty("user.dir"));

        File[] fileArr = dir.listFiles();

        for (int i = 0; i < fileArr.length; i++) {   
            if (fileArr[i].isFile()) {
                Path filePath = Paths.get(fileArr[i].getAbsolutePath());

                UserPrincipal owner = Files.getOwner(filePath, LinkOption.NOFOLLOW_LINKS);
                BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);

                System.out.println(fileArr[i].getName() + "\t" + owner.getName() + "\t" + attr.lastModifiedTime());
            }
        }
    }
}