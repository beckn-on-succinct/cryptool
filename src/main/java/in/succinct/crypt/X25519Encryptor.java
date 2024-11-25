package in.succinct.crypt;

import com.venky.core.security.Crypt;
import com.venky.core.util.ObjectUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Logger;

public class X25519Encryptor {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        Options options = getOptions();
        
        DefaultParser parser= new DefaultParser();
        CommandLine commandLine =null ;
        try {
            commandLine = parser.parse(options,args);
        }catch (Exception ex){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(X25519Encryptor.class.getName(),options);
            Runtime.getRuntime().exit(-1);
        }
        
        if (commandLine == null) throw new AssertionError( "Incorrect usage!");
        
        String data = commandLine.getOptionValue("d");
        String operation = commandLine.getOptionValue("o");
        String pub = commandLine.getOptionValue("p");
        String pvt = commandLine.getOptionValue("v");
        
        if (ObjectUtil.equals(operation,"encrypt")){
           System.out.println(encrypt(data.getBytes(StandardCharsets.UTF_8),pub,pvt));
        }else if (ObjectUtil.equals(operation,"decrypt")){
            System.out.println(decrypt(data.getBytes(StandardCharsets.UTF_8),pub,pvt));
        }else {
            throw new RuntimeException("Invalid operation");
        }
    }
    public static String encrypt(byte[] data, String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return Crypt.getInstance().encrypt(data, "AES", getAesKey(publicKey,privateKey));
    }
    public static SecretKey getAesKey(String publicKey , String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        PublicKey pbKey = getEncryptionPublicKey(publicKey);
        
        PrivateKey pvKey = getEncryptionPrivateKey(privateKey);
        
        KeyAgreement agreement = KeyAgreement.getInstance(XDHParameterSpec.X25519);
        agreement.init(pvKey);
        agreement.doPhase(pbKey, true);
        return agreement.generateSecret("TlsPremasterSecret");
        
    }
    
    public static String decrypt(byte[] data, String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return Crypt.getInstance().decrypt(data, "AES", getAesKey(publicKey,privateKey));
    }
    
    public static PrivateKey getEncryptionPrivateKey(String keyPassed){
        return Crypt.getInstance().getPrivateKey(XDHParameterSpec.X25519, keyPassed);
    }
    public static PublicKey getEncryptionPublicKey(String keyFromRegistry){
        PublicKey publicKey = null;
        try {
            publicKey = Crypt.getInstance().getPublicKey(XDHParameterSpec.X25519, keyFromRegistry);
        }catch (Exception ex){
            try {
                byte[] bcBytes = Base64.getDecoder().decode(keyFromRegistry);
                byte[] jceBytes = new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), bcBytes).getEncoded();
                String pemKey = Base64.getEncoder().encodeToString(jceBytes);
                publicKey = Crypt.getInstance().getPublicKey(XDHParameterSpec.X25519,pemKey);
            }catch (Exception jceEx){
                //publicKey =  null;
            }
        }
        return publicKey;
    }
    
    
    private static Options getOptions() {
        Options options = new Options();
        
        Option option = new Option("o", "operation", true, "encrypt|decrypt");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        option = new Option("d","data",true, "Exact payload");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        
        option = new Option("p", "public", true, "Public Key of other party");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);

        option = new Option("v","private",true, "Private key to use in X25519");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);
        
        
        
        return options;
    }
}