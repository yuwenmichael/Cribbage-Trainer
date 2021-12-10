package cribbage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogPrinter {
    private static LogPrinter logPrinter = new LogPrinter();

    // constructor is private because we cannot instantiate this class outside
    // LogPrinter
    private LogPrinter(){
        // delete the file if exist
        Path path = Path.of("cribbage.log");
        try{
            Files.deleteIfExists(path);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LogPrinter getInstance(){
        return logPrinter;
    }

    public void writeLog(String str) {
        try(PrintWriter pw = new PrintWriter(new FileWriter("cribbage.log", true))){
            pw.println(str);
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
