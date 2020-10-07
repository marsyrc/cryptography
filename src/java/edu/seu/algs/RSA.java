package edu.seu.algs;

import java.math.BigInteger;
import java.security.SecureRandom;


public class RSA {
    /**
     * 定义操作模式常数
     */
    private final int ENCRYPT = 1;
    private final int DECRYPT = 2;
    private final int SIGN = 3;
    private final int VERIFY = 4;

    private int mode;
    private RSAKey.PublicKey publicKey;
    private RSAKey.PrivateKey privateKey;
    private int blockSize;

    /**
     * mode只能为1或4
     *
     * @param mode
     * @param publicKey
     */
    public RSA(int mode, RSAKey.PublicKey publicKey) {
        this.mode = mode;
        this.publicKey = publicKey;
        this.blockSize = getByteLength(publicKey.getModule());
    }

    /**
     * mode只能为2或3
     *
     * @param mode
     * @param privateKey
     */
    public RSA(int mode, RSAKey.PrivateKey privateKey) {
        this.mode = mode;
        this.privateKey = privateKey;
        this.blockSize = getByteLength(privateKey.getModule());
    }

    /**
     * PKCS#1 v2.1 中定义的RSAEP模式
     * 注意必须指定正负号signum，理由见函数toByteArray
     * @param date
     * @return
     */
    private byte[] encrypt(byte[] date){
        BigInteger m = new BigInteger(1, date);
        BigInteger c = m.modPow(publicKey.getPublicExponent(), publicKey.getModule());
        return toByteArray(c, blockSize);
    }

    /**
     * RSADP模式
     * @param date
     * @return
     */
    private byte[] decrypt(byte[] date) {
        BigInteger c = new BigInteger(1, date);
        BigInteger m = c.modPow(privateKey.getPrivateExponent(), privateKey.getModule());
        return toByteArray(m, blockSize);
    }

    /**
     * RSASP1模式
     * @param date
     * @return
     */
    private byte[] sign(byte[] date) {
        return decrypt(date);
    }

    /**
     * RSAVP1模式
     * @param date
     * @return
     */
    private byte[] verify(byte[] date) {
        return encrypt(date);
    }

    /**
     * 参考rfc2313-pkcs#1 v1.5
     * EB = 00 || BT || PS || 00 || D
     *
     * @param date
     * @return
     */
    private byte[] pad(byte[] date, int dateOfs, int len) {
        byte[] padded = new byte[blockSize];
        System.arraycopy(date, dateOfs, padded, blockSize - len, len);
        int psLen = blockSize - len - 3;
        int index = 0;
        padded[index++] = 0x00;
        if (mode == DECRYPT) {
            padded[index++] = 0x01;
            while (psLen-- > 0) {
                padded[index++] = (byte)0xff;
            }
        } else {
            padded[index++] = 0x02;
            SecureRandom random = new SecureRandom();
            while (psLen > 0) {
                byte[] r = new byte[psLen + 4];
                random.nextBytes(r);
                for (int i = 0; i < r.length && psLen > 0; i++) {
                    if (r[i] != 0) {
                        padded[index++] = r[i];
                        psLen--;
                    }
                }
            }
        }
        return padded;
    }

    /**
     * @param padded
     * @return
     */
    private byte[] unpad(byte[] padded) throws Exception {
        int index = 2;
        while (padded[index] != 0 && index < padded.length) {
            index++;
        }
        if (index == padded.length) {
            throw new  Exception("解填充错误");
        }
        index++;
        byte[] date = new byte[padded.length - index];
        System.arraycopy(padded, index, date, 0, date.length);
        return date;
    }

    private int getByteLength(BigInteger bi) {
        int bitLen = bi.bitLength();
        return (bitLen + 7) >> 3;
    }

    /**
     * BigInteger转为byte array，处理加密后长度大于模数长度的情况，供加解密函数使用、
     * 正常情况下加密长度大于模数长度只可能出现在最高位字节需补零以指定正负数的情况，因此在encrypt和decrypt中进行
     * byte array转BigInteger时必须指定正负号，因为在此处高位字节零可能被移除，导致数变为负数
     * @param bi
     * @param len
     * @return
     */
    public byte[] toByteArray(BigInteger bi, int len) {
        byte[] b = bi.toByteArray();
        if (b.length == len) {
            return b;
        } else if ((b.length == len + 1) && (b[0] == 0x00)) {
            byte[] tmp = new byte[len];
            System.arraycopy(b, 1, tmp, 0, len);
            return tmp;
        } else {
            return b;
        }
    }

    /**
     * 根据操作模式mode处理消息
     * @param msg
     * @return
     */
    public byte[] process(byte[] msg) throws Exception {
        switch (mode) {
            case ENCRYPT:
                int dateSize = blockSize - 11;
                int numBlocks = (msg.length + dateSize - 1) / dateSize;
                byte[] output = new byte[numBlocks * blockSize];
                int blockCount = 1;
                while (blockCount * dateSize - 1 < msg.length) {
                    byte[] padded = pad(msg, (blockCount - 1) * dateSize, dateSize);
                    byte[] encrypted = encrypt(padded);
                    // 保证填充长度为blockSize
                    System.arraycopy(encrypted, 0, output, blockCount * blockSize - encrypted.length, encrypted.length);
                    blockCount++;
                }
                if (blockCount == numBlocks) {
                    int remainder = msg.length - (blockCount - 1) * dateSize;
                    byte[] padded = pad(msg, (blockCount - 1) * dateSize, remainder);
                    byte[] encrypted = encrypt(padded);
                    System.arraycopy(encrypted, 0, output, blockCount * blockSize - encrypted.length, encrypted.length);
                }
                return output;
            case DECRYPT:
                if (msg.length % blockSize != 0) {
                    // Todo
                }
                numBlocks = msg.length / blockSize;
                byte[] buffer = new byte[numBlocks * (blockSize - 11)];
                int ml = 0;  // 记录实际明文长度
                for (int i = 0; i < numBlocks; i++) {
                    byte[] input = new byte[blockSize];
                    System.arraycopy(msg, i * blockSize, input, 0, blockSize);
                    byte[] decrypted = decrypt(input);
                    byte[] unpadded = unpad(decrypted);
                    ml += unpadded.length;
                    System.arraycopy(unpadded, 0, buffer, i * (blockSize - 11), unpadded.length);
                }
                output = new byte[ml];
                System.arraycopy(buffer, 0, output, 0, ml);
                return output;
            case SIGN:
                dateSize = blockSize - 11;
                numBlocks = (msg.length + dateSize - 1) / dateSize;
                output = new byte[numBlocks * blockSize];
                blockCount = 1;
                while (blockCount * dateSize - 1 < msg.length) {
                    byte[] padded = pad(msg, (blockCount - 1) * dateSize, dateSize);
                    byte[] signed = sign(padded);
                    // 保证填充长度为blockSize
                    System.arraycopy(signed, 0, output, blockCount * blockSize - signed.length, signed.length);
                    blockCount++;
                }
                if (blockCount == numBlocks) {
                    int remainder = msg.length - (blockCount - 1) * dateSize;
                    byte[] padded = pad(msg, (blockCount - 1) * dateSize, remainder);
                    byte[] signed = sign(padded);
                    System.arraycopy(signed, 0, output, blockCount * blockSize - signed.length, signed.length);
                }
                return output;
            case VERIFY:
                if (msg.length % blockSize != 0) {
                    // Todo
                }
                numBlocks = msg.length / blockSize;
                buffer = new byte[numBlocks * (blockSize - 11)];
                ml = 0;
                for (int i = 0; i < numBlocks; i++) {
                    byte[] input = new byte[blockSize];
                    System.arraycopy(msg, i * blockSize, input, 0, blockSize);
                    byte[] verified = verify(input);
                    byte[] unpadded = unpad(verified);
                    ml += unpadded.length;
                    System.arraycopy(unpadded, 0, buffer, i * (blockSize - 11), unpadded.length);
                }
                output = new byte[ml];
                System.arraycopy(buffer, 0, output, 0, ml);
                return output;
        }
        return null;
    }
}
