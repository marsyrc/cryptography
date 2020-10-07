package edu.seu.algs;

import java.math.BigInteger;
import java.security.SecureRandom;


public class RSAKey {
    // OpenSSL提供的公钥指数之一
    private final BigInteger RSA_F4 = BigInteger.valueOf(65537);

    // RSA算法中模数n的bit位数
    private int keySize;

    /**
     *
     * @param keySize 建议大小2048位
     */
    public RSAKey(int keySize) {
        this.keySize = keySize;
    }

    public RSAKey() {
        this.keySize = 2048;
    }

    public KeyPair generateKeyPair() {
        int pl = (keySize + 1) >> 2;
        int ql = keySize - pl;
        // 公钥指数
        BigInteger e = RSA_F4;

        SecureRandom random = new SecureRandom();

        while (true) {
            BigInteger p = BigInteger.probablePrime(pl, random);
            BigInteger q, n;

            do {
                q = BigInteger.probablePrime(ql, random);

                if (p.compareTo(q) < 0) {
                    BigInteger tmp = p;
                    p = q;
                    q = tmp;
                }

                n = p.multiply(q);
            } while (n.bitLength() < keySize);

            BigInteger p1 = p.subtract(BigInteger.ONE);
            BigInteger q1 = q.subtract(BigInteger.ONE);
            BigInteger lambda = p1.multiply(q1);
            // 检查GCD(e,λ(n))=1是否满足，如不满足则重新计算，其中λ(n)=(p-1)*(q-1)
            if (e.gcd(lambda).equals(BigInteger.ONE) == false) {
                continue;
            }

            BigInteger d = e.modInverse(lambda);

            PublicKey publicKey = new PublicKey(n, e);
            PrivateKey privateKey = new PrivateKey(n, d);
            KeyPair keyPair = new KeyPair(publicKey, privateKey);
            return keyPair;
        }
    }

    public static final class PublicKey {
        // 模数
        private BigInteger n;
        // 公钥指数
        private BigInteger e;

        public PublicKey(BigInteger n, BigInteger e) {
            this.n = n;
            this.e = e;
        }

        public BigInteger getModule() {
            return this.n;
        }

        public BigInteger getPublicExponent() {
            return this.e;
        }
    }

    public static final class PrivateKey {
        // 模数
        private BigInteger n;
        // 私钥指数
        private BigInteger d;

        public PrivateKey(BigInteger n, BigInteger d) {
            this.n = n;
            this.d = d;
        }

        public BigInteger getModule() {
            return this.n;
        }

        public BigInteger getPrivateExponent() {
            return this.d;
        }
    }

    public static final class KeyPair {
        private PublicKey publicKey;
        private PrivateKey privateKey;

        public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }
    }
}
