package edu.seu.app;

import edu.seu.algs.*;
import edu.seu.app.panel.SendModule;

import java.math.BigInteger;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AlgUtil {
    private String symEncAlg;
    private String symKeySeed;
    private int blockLen;
    private String hashAlg;
    private String rsaKeySize1;
    private String rsaKeySize2;
    private byte[] symKey;
    private RSAKey.KeyPair rsaKey1;
    private RSAKey.KeyPair rsaKey2;

    final Base64.Encoder encoder = Base64.getEncoder();
    final Base64.Decoder decoder = Base64.getDecoder();

    public byte[] sendByte = "".getBytes();
    public static int sessionkeyL;
    public static int signatureL;
    public static int digestL;
    private  byte[] signatureSend = new byte[0];
    private byte[] ukedKeySend = new byte[0];


    public AlgUtil(String symEncAlg, String symKeySeed,
                   String hashAlg, String rsaKeySize1, String rsaKeySize2) {
        this.symEncAlg = symEncAlg;
        this.symKeySeed = symKeySeed;
        this.hashAlg = hashAlg;
        this.rsaKeySize1 = rsaKeySize1;
        this.rsaKeySize2 = rsaKeySize2;
        initialize();
    }

    private byte[] generateSymKey(String symKeySeed, int len) {
        byte[] symKey = new byte[len];
        if (symKeySeed == null) {
            SecureRandom random = new SecureRandom();
            random.nextBytes(symKey);
        } else {
            SecureRandom random = new SecureRandom(symKeySeed.getBytes());
            random.nextBytes(symKey);
        }
        return symKey;
    }

    private void initialize() {
        if (symEncAlg.equals("DES")) {
            symKey = generateSymKey(symKeySeed, 8);
            blockLen = 8;
        } else {
            symKey = generateSymKey(symKeySeed, 16);
            blockLen = 16;
        }
        rsaKey1 = new RSAKey(Integer.parseInt(rsaKeySize1)).generateKeyPair();
        rsaKey2 = new RSAKey(Integer.parseInt(rsaKeySize2)).generateKeyPair();
    }

    /**
     * 获取加密参数信息
     *
     * @param user
     * @return
     */
    public String getParameterInfo(int user) {
        StringBuilder builder = new StringBuilder();
        builder.append("对称加密算法：" + symEncAlg + "\n");
        //builder.append("对称加密密钥：" + encoder.encode(symKey) + "\n");
        builder.append("对称加密密钥：" + encoder.encodeToString(symKey) + "\n");
        builder.append("Hash算法：" + hashAlg + "\n");
        if (user == 1) {
            builder.append("RSA算法公钥(n,e)：(" + rsaKey1.getPublicKey().getModule() + rsaKey1.getPublicKey().getPublicExponent() + ")\n");
            builder.append("RSA算法私钥(n,d)：(" + rsaKey1.getPrivateKey().getModule() + rsaKey1.getPrivateKey().getPrivateExponent() + ")\n");
        } else {
            builder.append("RSA算法公钥(n,e)：(" + rsaKey2.getPublicKey().getModule() + rsaKey2.getPublicKey().getPublicExponent() + ")\n");
            builder.append("RSA算法私钥(n,d)：(" + rsaKey2.getPrivateKey().getModule() + rsaKey2.getPrivateKey().getPrivateExponent() + ")\n");
        }
        return builder.toString();
    }

    public byte[] getDigest(byte[] msg) {
        byte[] digest = {};
        if (hashAlg.equals("MD5")) {
            MD5 md5 = new MD5();
            md5.update(msg);
            digest = md5.getDigest();
        } else if (hashAlg.equals("SHA224")) {
            SHA2.SHA224 sha224 = new SHA2.SHA224();
            sha224.update(msg);
            digest = sha224.getDigest();
        } else if (hashAlg.equals("SHA256")) {
            SHA2.SHA256 sha256 = new SHA2.SHA256();
            sha256.update(msg);
            digest = sha256.getDigest();
        } else if (hashAlg.equals("SHA384")) {
            SHA5.SHA384 sha384 = new SHA5.SHA384();
            sha384.update(msg);
            digest = sha384.getDigest();
        } else if (hashAlg.equals("SHA512")) {
            SHA5.SHA512 sha512 = new SHA5.SHA512();
            sha512.update(msg);
            digest = sha512.getDigest();
        }

        return digest;
    }

    /**
     * 对称加密
     */
    private byte[] symEnc(byte[] m, byte[] symKey) {
        if (symEncAlg.equals("DES")) {
            return new DES(symKey).encrypt(m);
        } else {
            return new AES(symKey).encrypt(m);
        }
    }

    public byte[] symDec(byte[] c) {
        if (symEncAlg.equals("DES")) {
            return new DES(symKey).decrypt(c);
        } else {
            return new AES(symKey).decrypt(c);
        }
    }


    /**
     * 发送方处理，将明文消息处理为E[K,M||E[RK1,H(M)]]||E[UK2,K]的形式，
     * 其中K为会话密钥，M为明文，RK1为发送方私钥，UK2为接收方公钥
     * @param
     * @return
     */
    public byte[] sendProcess(byte[] msg) {
        //byte[] msg = m.getBytes();
        System.out.println("len send msg:"+msg.length);
        byte[] digest = getDigest(msg);
        System.out.println("len digest:"+digest.length);
//        byte[] signature = new byte[0];
        try {
            signatureSend = new RSA(3, rsaKey1.getPrivateKey()).process(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("len signature : " + signatureSend.length);
//        byte[] ukedKey = new byte[0];
        try {
            ukedKeySend = new RSA(1, rsaKey2.getPublicKey()).process(symKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("len ukekey:"+ukedKeySend.length);
        byte[] t = new byte[msg.length + signatureSend.length];
        System.out.println("len msg+sign : " + t.length);
        System.arraycopy(msg, 0, t, 0, msg.length);
        System.arraycopy(signatureSend, 0, t, msg.length, signatureSend.length);
        byte[] encrypted = symEnc(t, symKey);
        System.out.println("len key encode msg+sign : " + encrypted.length);
        sendByte = new byte[encrypted.length + ukedKeySend.length];
        System.out.println("len total : " + sendByte.length);
        System.arraycopy(encrypted, 0, sendByte, 0, encrypted.length);
        System.arraycopy(ukedKeySend, 0, sendByte, encrypted.length, ukedKeySend.length);
        return sendByte;
    }


    //解密文件
    public byte[] receiverDecrypt(byte[] s) {
        //String[] result = new String[2];
        if (s.equals("")) {
            return ("没有接收到任何消息！\n").getBytes();
        }
        byte[] c = s;
        byte[] ukedKey = new byte[getByteLength(rsaKey2.getPrivateKey().getModule())];
        if (c.length < ukedKey.length) {
            return "消息遭到篡改，无法解密!\n".getBytes();
        }
        System.arraycopy(c, c.length - ukedKey.length, ukedKey, 0, ukedKey.length);
        byte[] sessionKey;
        try {
            sessionKey = new RSA(2, rsaKey2.getPrivateKey()).process(ukedKey);
            System.out.println("L sessionKey in recei: "+sessionKey.length);
        } catch (Exception e) {
            return "消息遭到篡改，无法解密!\n".getBytes();
        }
        byte[] t = new byte[c.length - ukedKey.length];
        if (t.length % blockLen != 0) {
            return "消息遭到篡改，无法解密!\n".getBytes();
        }
        System.arraycopy(c, 0, t, 0, t.length);
        byte[] decrypted = symDec(t);
        byte[] signature = new byte[getByteLength(rsaKey1.getPublicKey().getModule())];
        System.arraycopy(decrypted, decrypted.length - signature.length, signature, 0, signature.length);
        byte[] digest = new byte[0];
        try {
            digest = new RSA(4, rsaKey1.getPublicKey()).process(signature);
        } catch (Exception e) {
            return "消息遭到篡改，无法解密!\n".getBytes();
        }
        byte[] m = new byte[decrypted.length - signature.length];

        System.arraycopy(decrypted, 0, m, 0, m.length);

        byte[] out = new byte[m.length+signature.length+sessionKey.length+digest.length];
        System.arraycopy(m, 0, out, 0, m.length);
        System.out.println("m.length:"+m.length);
        System.arraycopy(sessionKey, 0, out, m.length, sessionKey.length);
        System.arraycopy(signature,0,out,m.length+sessionKey.length,signature.length);
        System.arraycopy(digest,0,out, m.length+sessionKey.length+signature.length,digest.length);
        System.out.println("message digest： " + encoder.encodeToString(digest));
        sessionkeyL = sessionKey.length;
        signatureL = signature.length;
        digestL = digest.length;
        return out;
    }


    public byte[] receiverVerify(byte[] s) {
        if (s.equals("")) {
            return "没有接收到任何消息！\n".getBytes();
        }
        byte[] c = s;

        byte[] ukedKey = new byte[getByteLength(rsaKey2.getPrivateKey().getModule())];
        if (c.length < ukedKey.length) {
            return "认证失败，消息遭到篡改！\n".getBytes();
        }
        System.arraycopy(c, c.length - ukedKey.length, ukedKey, 0, ukedKey.length);
        byte[] sessionKey = new byte[0];
        try {
            sessionKey = new RSA(2, rsaKey2.getPrivateKey()).process(ukedKey);
        } catch (Exception e) {
            return "认证失败，消息遭到篡改！\n".getBytes();
        }
        byte[] t = new byte[c.length - ukedKey.length];
        if (t.length % blockLen != 0) {
            return "认证失败，消息遭到篡改！\n".getBytes();
        }
        System.arraycopy(c, 0, t, 0, t.length);
        byte[] decrypted;
        if (sessionKey.length == 8) {
            decrypted = new DES(sessionKey).decrypt(t);
        } else {
            decrypted = new AES(sessionKey).decrypt(t);
        }
        byte[] signature = new byte[getByteLength(rsaKey1.getPublicKey().getModule())];
        System.arraycopy(decrypted, decrypted.length - signature.length, signature, 0, signature.length);
        byte[] digest = new byte[0];
        try {
            digest = new RSA(4, rsaKey1.getPublicKey()).process(signature);
        } catch (Exception e) {
            return "认证失败，消息遭到篡改！".getBytes();
        }
        byte[] m = new byte[decrypted.length - signature.length];
        System.arraycopy(decrypted, 0, m, 0, m.length);
        byte[] mdigest = getDigest(m);
        if (Arrays.equals(digest, mdigest)) {
            return "认证通过，消息没有遭到篡改！\n".getBytes();
        } else {
            return "认证失败，消息遭到篡改！\n".getBytes();
        }
    }

    /**
     *
     * @param s
     * @return
     */
    public String ByteTOString(byte[] s){
        if(new String(s).equals( "消息遭到篡改，无法解密!\n")){
            return "消息遭到篡改，无法解密!\n";
        }
        byte[] sessionKey = new byte[sessionkeyL];
        System.arraycopy(s,s.length-sessionkeyL-signatureL-digestL, sessionKey,0, sessionkeyL);
        byte[] signature = new byte[signatureL];
        System.arraycopy(s,s.length-signatureL-digestL, signature,0, signatureL);
        byte[] digest = new byte[digestL];
        System.arraycopy(s,s.length-digestL, digest,0, digestL);
        byte[] mes = new byte[s.length-sessionkeyL-signatureL-digestL];
        System.arraycopy(s,0, mes,0, s.length-sessionkeyL-signatureL-digestL);
        StringBuilder builder = new StringBuilder();
        builder.append("解密得会话密钥：" + encoder.encodeToString(sessionKey) + "\n");
        builder.append("解密得数字签名：" + encoder.encodeToString(signature) + "\n");
        builder.append("解密得消息摘要：" + encoder.encodeToString(digest) + "\n");
        builder.append("解密得消息内容："+ new String(mes) + "\n");
        return new String(builder);
    }

    private int getByteLength(BigInteger bi) {
        int bitLen = bi.bitLength();
        return (bitLen + 7) >> 3;
    }

    private static void printByteArray(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("0x" + Integer.toHexString(array[i] & 0xFF) + " ");
        }
        System.out.println();
    }

    /**
     *
     * @param publicKey
     * @param msg
     * @param signature
     * @param HASHALG
     * @return
     */
    public boolean LongTermValidation(RSAKey.PublicKey publicKey, byte[] msg, byte[] signature, String HASHALG){
        //对明文产生摘要
        byte[] digest = {};
        if (HASHALG.equals("MD5")) {
            MD5 md5 = new MD5();
            md5.update(msg);
            digest = md5.getDigest();
        } else if (HASHALG.equals("SHA224")) {
            SHA2.SHA224 sha224 = new SHA2.SHA224();
            sha224.update(msg);
            digest = sha224.getDigest();
        } else if (HASHALG.equals("SHA256")) {
            SHA2.SHA256 sha256 = new SHA2.SHA256();
            sha256.update(msg);
            digest = sha256.getDigest();
        } else if (HASHALG.equals("SHA384")) {
            SHA5.SHA384 sha384 = new SHA5.SHA384();
            sha384.update(msg);
            digest = sha384.getDigest();
        } else if (HASHALG.equals("SHA512")) {
            SHA5.SHA512 sha512 = new SHA5.SHA512();
            sha512.update(msg);
            digest = sha512.getDigest();
        }
        //解开签名
        byte[] digestRecv = new byte[0];
        try {
            digestRecv = new RSA(4,publicKey).process(signature);     //这里先抢强行用本身体的代替
        } catch (Exception e) {
            return false;
        }
        if(Arrays.equals(digest,digestRecv)){
            return true;
        }else{
            return false;
        }
    }
    /**
     *
     * @param n
     * @param e
     * @return
     */
    public RSAKey.PublicKey PublicKeyGen(BigInteger n,BigInteger e){
        RSAKey.PublicKey publicKey = new RSAKey.PublicKey(n, e);
        return publicKey;
    }

    public byte[] getSignatureSend(byte[] s){
        byte[] signature = new byte[signatureL];
        System.arraycopy(s,s.length-signatureL-digestL, signature,0, signatureL);
        return  signature;
    }

    public String getSendPublicKey(){
        StringBuilder builder = new StringBuilder();
        builder.append(hashAlg+"\n");
        builder.append(rsaKey1.getPublicKey().getModule()+"\n");
        builder.append(rsaKey1.getPublicKey().getPublicExponent()+"\n");
        return builder.toString();
    }
}
