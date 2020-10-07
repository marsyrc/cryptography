package edu.seu.algs;


public class SHA2 {
    /**
     * 轮常数
     */
    private static final int[] ROUND_CONSTS = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
            0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
            0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
            0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
            0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
            0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
            0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
            0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
            0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };


    // 轮数
    private static final int ROUND = 64;
    // 一次处理块长度
    private static final int BLOCK_SIZE = 64;
    // 填充使用的字节数组
    private static final byte[] padding;

    static {
        padding = new byte[64];
        padding[0] = (byte) 0x80;
    }

    // state数组的初始值
    private final int[] initialState;
    // 计算时使用的8个寄存器值
    private int[] state;
    // 临时缓存
    private int[] w;
    // 字节缓存器
    private byte[] buffer;
    // 已处理的字节数
    private long bytesProcessed;
    // 字节缓存器已用字节偏移量，即已用字节数
    private int bufferOffset;
    // 消息摘要字节长度
    private int digestLen;

    private SHA2(int digestLen, int[] initialState) {
        this.digestLen = digestLen;
        this.initialState = initialState;
        state = new int[8];
        w = new int[ROUND];
        buffer = new byte[BLOCK_SIZE];
        this.reset();
    }

    /**
     * 逻辑函数CH，使用int表示32-bit word，大端序
     * CH( x, y, z) = (x AND y) XOR ( (NOT x) AND z)
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static int ch(int x, int y, int z) {
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
    private static int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    /**
     * 循环右移
     *
     * @param x
     * @param s
     * @return
     */
    private static int rotR(int x, int s) {
        return (x >>> s) | (x << (32 - s));
    }

    /**
     * 逻辑右移
     *
     * @param x
     * @param s
     * @return
     */
    private static int shR(int x, int s) {
        return (x >>> s);
    }

    /**
     * 逻辑函数BSIG0
     * BSIG0(x) = ROTR^2(x) XOR ROTR^13(x) XOR ROTR^22(x)
     *
     * @param x
     * @return
     */
    private static int bsig0(int x) {
        return rotR(x, 2) ^ rotR(x, 13) ^ rotR(x, 22);
    }

    /**
     * 逻辑函数BSIG1
     * BSIG1(x) = ROTR^6(x) XOR ROTR^11(x) XOR ROTR^25(x)
     *
     * @param x
     * @return
     */
    private static int bsig1(int x) {
        return rotR(x, 6) ^ rotR(x, 11) ^ rotR(x, 25);
    }

    /**
     * 逻辑函数SSIG0
     * SSIG0(x) = ROTR^7(x) XOR ROTR^18(x) XOR SHR^3(x)
     *
     * @param x
     * @return
     */
    private static int ssig0(int x) {
        return rotR(x, 7) ^ rotR(x, 18) ^ shR(x, 3);
    }

    /**
     * 逻辑函数SSIG1
     * SSIG1(x) = ROTR^17(x) XOR ROTR^19(x) XOR SHR^10(x)
     *
     * @param x
     * @return
     */
    private static int ssig1(int x) {
        return rotR(x, 17) ^ rotR(x, 19) ^ shR(x, 10);
    }

    /**
     * 将byte数组转为int数组，字节序为大端序
     *
     * @param in     输入字节数组
     * @param inOfs  字节数组偏移
     * @param out    输出int数组
     * @param outOfs int数组偏移
     * @param len    转换部分的int长度
     */
    private static void b2iBig(byte[] in, int inOfs, int[] out, int outOfs, int len) {
        len += outOfs;
        while (outOfs < len) {
            out[outOfs++] = ((in[inOfs++] & 0xff) << 24) | ((in[inOfs++] & 0xff) << 16) |
                    ((in[inOfs++] & 0xff) << 8) | (in[inOfs++] & 0xff);
        }
    }

    /**
     * 将int数组转为byte数组，字节序为大端序
     *
     * @param in
     * @param inOfs
     * @param out
     * @param outOfs
     * @param len
     */
    private static void i2bBig(int[] in, int inOfs, byte[] out, int outOfs, int len) {
        len += outOfs;
        while (outOfs < len) {
            int i = in[inOfs++];
            out[outOfs++] = (byte) (i >> 24);
            out[outOfs++] = (byte) (i >> 16);
            out[outOfs++] = (byte) (i >> 8);
            out[outOfs++] = (byte) i;
        }
    }

    private static void printByteArray(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("0x" + Integer.toHexString(array[i] & 0xFF) + " ");
        }
        System.out.println();
    }

    private void reset() {
        System.arraycopy(initialState, 0, state, 0, 8);
        bytesProcessed = 0;
        bufferOffset = 0;
    }

    public void update(byte[] input) {
        update(input, 0, input.length);
    }

    /**
     * sha2的最后一部操作，进行填充，加入长度，并返回digest
     *
     * @return
     */
    public byte[] getDigest() {
        byte[] digest = new byte[digestLen];

        long bitsProcessed = bytesProcessed << 3;
        int index = (int) bytesProcessed & 0x3f;
        int padLen = (index < 56) ? (56 - index) : (120 - index);

        update(padding, 0, padLen);
        int[] lengths = {(int) (bitsProcessed >>> 32), (int) bitsProcessed};
        i2bBig(lengths, 0, buffer, 56, 8);
        compress(buffer, 0);

        i2bBig(state, 0, digest, 0, digestLen);
        reset();
        return digest;
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
        if (bytesProcessed < 0) {
            reset();
        }
        bytesProcessed += length;
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
        b2iBig(in, ofs, w, 0, 16);
        for (int t = 16; t < ROUND; t++) {
            w[t] = ssig1(w[t - 2]) + w[t - 7] + ssig0(w[t - 15]) + w[t - 16];
        }

        int a = state[0];
        int b = state[1];
        int c = state[2];
        int d = state[3];
        int e = state[4];
        int f = state[5];
        int g = state[6];
        int h = state[7];

        for (int t = 0; t < ROUND; t++) {
            int t1 = h + bsig1(e) + ch(e, f, g) + ROUND_CONSTS[t] + w[t];
            int t2 = bsig0(a) + maj(a, b, c);
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

    public static final class SHA224 extends SHA2 {
        private static final int[] INITIAL_STATE = {
                0xc1059ed8, 0x367cd507, 0x3070dd17, 0xf70e5939,
                0xffc00b31, 0x68581511, 0x64f98fa7, 0xbefa4fa4
        };

        public SHA224() {
            super(28, INITIAL_STATE);
        }
    }

    public static final class SHA256 extends SHA2 {
        private static final int[] INITIAL_STATE = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        public SHA256() {
            super(32, INITIAL_STATE);
        }
    }
}
