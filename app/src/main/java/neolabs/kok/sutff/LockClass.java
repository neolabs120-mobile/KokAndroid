package neolabs.kok.sutff;

import java.math.BigInteger;
import java.security.MessageDigest;

public class LockClass {
    //SHA-512방식으로 문자열 암호화
    public static String getSHA512(String input){
        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
