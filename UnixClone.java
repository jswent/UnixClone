import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;

public class UnixClone {

    public static void main(String[] args) throws Exception {
        //cpDir("./testDir", "./testDir2");
        //touch("newfile.txt");
        //lsa();

        if (args[0].equals("lsa")) {
            lsa();
        }
        else if (args[0].equals("cp")) {
            cp(args[1], args[2]);
        }
        else if (args[0].equals("cpDir")) {
            cpDir(args[1], args[2]);
        }
        else if (args[0].equals("touch")) {
            touch(args[1]);
        }
        else if (args[0].equals("rm")) {
            rm(args[1]);
        }
        else if (args[0].equals("ls")) {
            ls();
        }
        else if (args[0].equals("rmRf")) {
            rmRf(args[1]);
        }
        else if (args[0].equals("mv")) {
            mv(args[1], args[2]);
        }
        else if (args[0].equals("cat")) {
            cat(args[1]);
        }
    }

    public static boolean cp(String copyFrom, String copyTo) throws IOException {
        //System.out.println(copyFrom + " " + copyTo);
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

        if (copied.exists()) {
            return true;
        }
        else {
            return false;
        }
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

    public static void ls() throws Exception {
        File[] fileArr = listFiles(System.getProperty("user.dir"));

        for (File file : fileArr) {
            System.out.println(file.getName());
        }
    }

    private static File[] listFiles(String path) throws Exception {
        File dir = new File(path);

        return dir.listFiles();
    }

    public static void rm (String path) throws Exception {
        File file = new File(path);

        if (file.exists()) {
            file.delete();
        }
    }

    public static void rmRf (String path) throws Exception {
        File[] fileArr = listFiles(path);

        for (File file : fileArr) {
            if (file.isDirectory()) {
                rmRf(file.getAbsolutePath());
            }
    
            file.delete();        
        }

        File file = new File(path);
        file.delete();
    }

    public static void mv (String moveFrom, String moveTo) throws Exception {
        if (cp(moveFrom, moveTo)) {
            rm(moveFrom);
        }
        else {
            System.out.println("failed to copy file");
        }

    } 

    public static void cat (String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;

        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}