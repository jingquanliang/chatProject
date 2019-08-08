package Utility;

/**
 * @description:
 * @author:Administrator
 * @date: 2018/5/5
 **/
public class Decryption {
    public static String parseTokenToPassword(String token){
        char c[] = token.toCharArray();
        for(int i=0;i<c.length;i++) {
            c[i] = (char)(c[i] ^ 'M');//将密文还原为明文
        }
        String string1 = new String(c, 0, c.length);
//        System.out.println("明文：" + "\n" + string1);
        return string1;
    }
}
