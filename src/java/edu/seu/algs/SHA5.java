package edu.seu.algs;

import java.math.BigInteger;


public class SHA5 {
    // 轮常数
    private static final long[] ROUND_CONSTS = {
            0x428A2F98D728AE22L, 0x7137449123EF65CDL, 0xB5C0FBCFEC4D3B2FL,
            0xE9B5DBA58189DBBCL, 0x3956C25BF348B538L, 0x59F111F1B605D019L,
            0x923F82A4AF194F9BL, 0xAB1C5ED5DA6D8118L, 0xD807AA98A3030242L,
            0x12835B0145706FBEL, 0x243185BE4EE4B28CL, 0x550C7DC3D5FFB4E2L,
            0x72BE5D74F27B896FL, 0x80DEB1FE3B1696B1L, 0x9BDC06A725C71235L,
            0xC19BF174CF692694L, 0xE49B69C19EF14AD2L, 0xEFBE4786384F25E3L,
            0x0FC19DC68B8CD5B5L, 0x240CA1CC77AC9C65L, 0x2DE92C6F592B0275L,
            0x4A7484AA6EA6E483L, 0x5CB0A9DCBD41FBD4L, 0x76F988DA831153B5L,
            0x983E5152EE66DFABL, 0xA831C66D2DB43210L, 0xB00327C898FB213FL,
            0xBF597FC7BEEF0EE4L, 0xC6E00BF33DA88FC2L, 0xD5A79147930AA725L,
            0x06CA6351E003826FL, 0x142929670A0E6E70L, 0x27B70A8546D22FFCL,
            0x2E1B21385C26C926L, 0x4D2C6DFC5AC42AEDL, 0x53380D139D95B3DFL,
            0x650A73548BAF63DEL, 0x766A0ABB3C77B2A8L, 0x81C2C92E47EDAEE6L,
            0x92722C851482353BL, 0xA2BFE8A14CF10364L, 0xA81A664BBC423001L,
            0xC24B8B70D0F89791L, 0xC76C51A30654BE30L, 0xD192E819D6EF5218L,
            0xD69906245565A910L, 0xF40E35855771202AL, 0x106AA07032BBD1B8L,
            0x19A4C116B8D2D0C8L, 0x1E376C085141AB53L, 0x2748774CDF8EEB99L,
            0x34B0BCB5E19B48A8L, 0x391C0CB3C5C95A63L, 0x4ED8AA4AE3418ACBL,
            0x5B9CCA4F7763E373L, 0x682E6FF3D6B2B8A3L, 0x748F82EE5DEFB2FCL,
            0x78A5636F43172F60L, 0x84C87814A1F0AB72L, 0x8CC702081A6439ECL,
            0x90BEFFFA23631E28L, 0xA4506CEBDE82BDE9L, 0xBEF9A3F7B2C67915L,
            0xC67178F2E372532BL, 0xCA273ECEEA26619CL, 0xD186B8C721C0C207L,
            0xEADA7DD6CDE0EB1EL, 0xF57D4F7FEE6ED178L, 0x06F067AA72176FBAL,
            0x0A637DC5A2C898A6L, 0x113F9804BEF90DAEL, 0x1B710B35131C471BL,
            0x28DB77F523047D84L, 0x32CAAB7B40C72493L, 0x3C9EBE0A15C9BEBCL,
            0x431D67C49C100D4CL, 0x4CC5D4BECB3E42B6L, 0x597F299CFC657E2AL,
            0x5FCB6FAB3AD6FAECL, 0x6C44198C4A475817L
    };

    // 运算轮数
    private static final int ROUND = 80;
    // 一次处理块长度
    private static final int BLOCK_SIZE = 128;
    // 填充使用的字节数组
    private static final byte[] padding;

    static {
        padding = new byte[128];
        padding[0] = (byte) 0x80;
    }

    // state数组的初始值
    private final long[] initialState;
    // 计算时使用的8个寄存器值
    private long[] state;
    // 临时缓存
    private long[] w;
    // 字节缓存器
    private byte[] buffer;
    // 已处理的字节数
    private BigInteger bytesProcessed;
    // 字节缓存器已用字节偏移量，即已用字节数
    private int bufferOffset;
    // 消息摘要字节长度
    private int digestLen;

    private SHA5(int digestLen, long[] initialState) {
        this.digestLen = digestLen;
        this.initialState = initialState;
        state = new long[8];
        w = new long[ROUND];
        buffer = new byte[BLOCK_SIZE];
        this.reset();
    }

    /**
     * 逻辑函数CH
     * CH( x, y, z) = (x AND y) XOR ( (NOT x) AND z)
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static long ch(long x, long y, long z) {
        return (x & y) ^ ((~x) & z);
    }

    /**
     * 逻辑函数MAJ
     * MAJ( x, y, z) = (x AND y) XOR (x AND z) XOR (y AND z)
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static long maj(long x, long y, long z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    /**
     * 循环右移
     *
     * @param x
     * @param s
     * @return
     */
    private static long rotR(long x, int s) {
        return (x >>> s) | (x << (64 - s));
    }

    /**
     * 逻辑右移
     *
     * @param x
     * @param s
     * @return
     */
    private static long shR(long x, int s) {
        return (x >>> s);
    }

    /**
     * 逻辑函数BSIG0
     * BSIG0(x) = ROTR^28(x) XOR ROTR^34(x) XOR ROTR^39(x)
     *
     * @param x
     * @return
     */
    private static long bsig0(long x) {
        return rotR(x, 28) ^ rotR(x, 34) ^ rotR(x, 39);
    }

    /**
     * 逻辑函数BSIG1
     * BSIG1(x) = ROTR^14(x) XOR ROTR^18(x) XOR ROTR^41(x)
     *
     * @param x
     * @return
     */
    private static long bsig1(long x) {
        return rotR(x, 14) ^ rotR(x, 18) ^ rotR(x, 41);
    }

    /**
     * 逻辑函数SSIG0
     * SSIG0(x) = ROTR^1(x) XOR ROTR^8(x) XOR SHR^7(x)
     *
     * @param x
     * @return
     */
    private static long ssig0(long x) {
        return rotR(x, 1) ^ rotR(x, 8) ^ shR(x, 7);
    }

    /**
     * 逻辑函数SSIG1
     * SSIG1(x) = ROTR^19(x) XOR ROTR^61(x) XOR SHR^6(x)
     *
     * @param x
     * @return
     */
    private static long ssig1(long x) {
        return rotR(x, 19) ^ rotR(x, 61) ^ shR(x, 6);
    }

    private static void b2lBig(byte[] in, int inOfs, long[] out, int outOfs, int len) {
        len += outOfs;
        while (outOfs < len) {
            out[outOfs++] = ((in[inOfs++] & 0xffL) << 56) | ((in[inOfs++] & 0xffL) << 48) |
                    ((in[inOfs++] & 0xffL) << 40) | ((in[inOfs++] & 0xffL) << 32) | ((in[inOfs++] & 0xffL) << 24) |
                    ((in[inOfs++] & 0xffL) << 16) | ((in[inOfs++] & 0xffL) << 8) | (in[inOfs++] & 0xffL);
        }
    }

    private static void l2bBig(long[] in, int inOfs, byte[] out, int outOfs, int len) {
        len += outOfs;
        while (outOfs < len) {
            long i = in[inOfs++];
            out[outOfs++] = (byte) (i >>> 56);
            out[outOfs++] = (byte) (i >>> 48);
            out[outOfs++] = (byte) (i >>> 40);
            out[outOfs++] = (byte) (i >>> 32);
            out[outOfs++] = (byte) (i >>> 24);
            out[outOfs++] = (byte) (i >>> 16);
            out[outOfs++] = (byte) (i >>> 8);
            out[outOfs++] = (byte) i;
        }
    }

    private static void printByteArray(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i % 16 == 0) {
                System.out.println();
            }
            System.out.print("0x" + Integer.toHexString(array[i] & 0xFF) + " ");
        }
        System.out.println();
    }

    private void reset() {
        System.arraycopy(initialState, 0, state, 0, 8);
        bytesProcessed = BigInteger.ZERO;
        bufferOffset = 0;
    }

    /**
     * sha5的最后一部操作，进行填充，加入长度，并返回digest
     *
     * @return
     */
    public byte[] getDigest() {
        byte[] digest = new byte[digestLen];

        BigInteger bitsProcessed = bytesProcessed.shiftLeft(3);
        BigInteger mask = new BigInteger(Integer.toString(0x7f));
        int index = bytesProcessed.and(mask).intValue();
        int padLen = (index < 112) ? (112 - index) : (240 - index);

        update(padding, 0, padLen);
        // 将bitsProcessed转为大小为16的byte数组，直接使用bitsProcessed.toByteArray可能长度不够16，
        byte[] bitsLength = new byte[16];
        byte[] temp = bitsProcessed.toByteArray();
        System.arraycopy(temp, 0, bitsLength, 16 - temp.length, temp.length);

        System.arraycopy(bitsLength, 0, buffer, 112, 16);
        compress(buffer, 0);

        l2bBig(state, 0, digest, 0, digestLen);
        reset();
        return digest;
    }

    public void update(byte[] input) {
        update(input, 0, input.length);
    }

    /**
     * 内部update函数，对输入的byte数组offset后（包括）长度为length的部分进行处理
     *
     * @param input
     * @param offset
     * @param length
     */
    private void update(byte[] input, int offset, int length) {
        if (length == 0) {
            return;
        }
        if (length < 0 || offset < 0 || ((input.length - length) < offset)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        bytesProcessed = bytesProcessed.add(new BigInteger(Integer.toString(length)));
        if (bufferOffset != 0) {
            int n = Math.min(length, buffer.length - bufferOffset);
            System.arraycopy(input, offset, buffer, bufferOffset, n);
            offset += n;
            bufferOffset += n;
            length -= n;
            if (bufferOffset >= BLOCK_SIZE) {
                compress(buffer, 0);
                bufferOffset = 0;
            }
        }
        if (length > BLOCK_SIZE) {
            int i;
            for (i = 0; i < (length / BLOCK_SIZE); i++) {
                compress(input, offset + i * BLOCK_SIZE);
            }
            bufferOffset = 0;
            offset += i * BLOCK_SIZE;
            length -= i * BLOCK_SIZE;
        }
        if (length > 0) {
            System.arraycopy(input, offset, buffer, bufferOffset, length);
            bufferOffset = length;
        }
    }

    private void compress(byte[] in, int ofs) {
        b2lBig(in, ofs, w, 0, 16);

        for (int t = 16; t < ROUND; t++) {
            w[t] = ssig1(w[t - 2]) + w[t - 7] + ssig0(w[t - 15]) + w[t - 16];
        }

        long a = state[0];
        long b = state[1];
        long c = state[2];
        long d = state[3];
        long e = state[4];
        long f = state[5];
        long g = state[6];
        long h = state[7];

        for (int t = 0; t < ROUND; t++) {
            long t1 = h + bsig1(e) + ch(e, f, g) + ROUND_CONSTS[t] + w[t];
            long t2 = bsig0(a) + maj(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + t1;
            d = c;
            c = b;
            b = a;
            a = t1 + t2;
        }

        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
        state[4] += e;
        state[5] += f;
        state[6] += g;
        state[7] += h;
    }

    public static final class SHA384 extends SHA5 {
        private static final long[] INITIAL_STATE = {
                0xcbbb9d5dc1059ed8L, 0x629a292a367cd507L,
                0x9159015a3070dd17L, 0x152fecd8f70e5939L,
                0x67332667ffc00b31L, 0x8eb44a8768581511L,
                0xdb0c2e0d64f98fa7L, 0x47b5481dbefa4fa4L
        };

        public SHA384() {
            super(48, INITIAL_STATE);
        }
    }

    public static final class SHA512 extends SHA5 {
        private static final long[] INITIAL_STATE = {
                0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
                0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L,
                0x510e527fade682d1L, 0x9b05688c2b3e6c1fL,
                0x1f83d9abfb41bd6bL, 0x5be0cd19137e2179L
        };

        public SHA512() {
            super(64, INITIAL_STATE);
        }
    }
}
