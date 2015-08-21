import java.io.IOException;

/**
 * Created by srinivas_dhanraj on 8/19/15.
 */
public class lukeLookupProcess {


    public lukeLookupProcess() {
    }

    public static void main(String[] args){
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void process() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        String java_home = System.getenv("JAVA_HOME");
        //pb.directory(new File("//Library//Java//JavaVirtualMachines//jdk1.8.0_25.jdk//Contents//Home//bin"));
        String[] command = {java_home + "bin//java", "-jar", "//Users//srinivas_dhanraj//Applications//lucene-5.2.1//lukeall-4.0.0-ALPHA.jar"};
//        pb.command("java -jar //Users//srinivas_dhanraj//Applications//lucene-5.2.1//lukeall-4.0.0-ALPHA.jar");
        pb.start();
        System.out.println("Done invoking command");
        Thread.sleep(100000);
    }
}
