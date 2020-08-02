import org.apache.commons.codec.digest.DigestUtils;

public class Crack {

    // b64488729b831330072b051ce45172c8
    public static void main(String[] args) {
        String result = "73.25%";
        for (int i = 0; i < 100000000; i++) {
            result = DigestUtils.md2Hex(result);
        }
        System.out.println(result);
    }
}
