import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;

public class Crack {

    //
    public static void main(String[] args) {
//        String result = "39";
//        for (int i = 0; i < 100000000; i++) {
//            result = DigestUtils.md2Hex(result);
//        }
//        System.out.println(result);

        // 94b1cf2c8f4aa239dd69e90f0850b2f9

        String res = "94b1cf2c8f4aa239dd69e90f0850b2f9" + "yVvdcU4sgi21E78zYuXVbsWndm23c1MYaV8drw";
        System.out.println(MD4.get(res));

        // 9c114945a8d3fe62540faadbe7734dd5
    }
}
