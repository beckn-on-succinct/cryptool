import com.venky.core.security.Crypt;
import com.venky.core.util.ObjectUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
public class Signer {
    public static void main(String[] args){
        Options options = getOptions();
        
        DefaultParser parser= new DefaultParser();
        CommandLine commandLine =null ;
        try {
            commandLine = parser.parse(options,args);
        }catch (Exception ex){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Signer.class.getName(),options);
            Runtime.getRuntime().exit(-1);
        }
        
        if (commandLine == null) throw new AssertionError( "Incorrect usage!");
        
        String data = commandLine.getOptionValue("d");
        String operation = commandLine.getOptionValue("o");
        String algo = commandLine.getOptionValue("a");
        
        if (ObjectUtil.equals(operation,"verify")){
            String pub = commandLine.getOptionValue("p");
            String signature = commandLine.getOptionValue("s");
            if (!Crypt.getInstance().verifySignature(data,signature,algo,Crypt.getInstance().getPublicKey(algo,pub))){
                throw new RuntimeException("Verification Failed");
            }
        }else if (ObjectUtil.equals(operation,"sign")){
            String pvt = commandLine.getOptionValue("v");
            System.out.println(Crypt.getInstance().generateSignature(data,algo,Crypt.getInstance().getPrivateKey(algo,pvt)));
        }else {
            throw new RuntimeException("Invalid operation");
        }
    }
    
    private static Options getOptions() {
        Options options = new Options();
        
        Option option = new Option("o", "operation", true, "sign|verify");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("d","data",true, "Exact Data to sign or verify");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("a", "algo", true, "Crypto Algo name");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("p", "public", true, "Public Key");
        option.setRequired(false);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("s", "signature", true, "Signature");
        option.setRequired(false);
        option.setArgs(1);
        options.addOption(option);
        
        
        option = new Option("v","private",true, "Private key");
        option.setRequired(false);
        option.setArgs(1);
        options.addOption(option);
        
        
        return options;
    }
}
