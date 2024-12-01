package in.succinct.crypt;

import com.venky.core.security.Crypt;
import com.venky.core.string.StringUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.FileInputStream;

public class HashBuilder {
    public static void main(String[] args) throws Exception{
        Options options = getOptions();
        
        DefaultParser parser= new DefaultParser();
        CommandLine commandLine =null ;
        try {
            commandLine = parser.parse(options,args);
        }catch (Exception ex){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(HashBuilder.class.getName(),options);
            Runtime.getRuntime().exit(-1);
        }
        
        if (commandLine == null) throw new AssertionError( "Incorrect usage!");
        
        String dataFile = commandLine.getOptionValue("d");
        String algo = commandLine.getOptionValue("h");
        //System.err.printf("|%s|Length %d%n", data,data.length());
        byte[] data = StringUtil.readBytes(dataFile == null ? System.in : new FileInputStream(dataFile));
        
        System.out.println(Crypt.getInstance().toBase64(Crypt.getInstance().digest(algo,data)));
    }
    
    private static Options getOptions() {
        Options options = new Options();
        
        
        
        Option option = new Option("h","hash",true, "Hashing Algo");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("d","data",true, "Data to hash");
        option.setRequired(false);
        option.setArgs(1);
        options.addOption(option);
        
        return options;
    }
}
