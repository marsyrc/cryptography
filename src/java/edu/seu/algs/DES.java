package edu.seu.algs;


public class DES {
    /**
     * 1byte的bit数
     */
    private static final int BITS_OF_BYTE = 8;

    /**
     * 位计数时的位置偏移量，DES算法定义中各常量盒位置从1开始计数，与程序中的计数有偏移
     */
    private static final int POSITION_OFFSET = 1;

    /**
     * 位操作掩码
     * 1 byte中从左到右分别为bit0至bit7
     */
    private static final int BIT0_MASK = 0x80;
    private static final int BIT1_MASK = 0x40;
    private static final int BIT2_MASK = 0x20;
    private static final int BIT3_MASK = 0x10;
    private static final int BIT4_MASK = 0x08;
    private static final int BIT5_MASK = 0x04;
    private static final int BIT6_MASK = 0x02;
    private static final int BIT7_MASK = 0x01;
    private static final int HIGH_TWO_BITS_MASK = 0xC0;
    private static final int HIGH_FOUR_BITS_MASK = 0xF0;
    private static final int BIT4_BIT5_MASK = 0x0C;
    private static final int LOW_FOUR_BITS_MASK = 0x0F;
    private static final int BYTE_MASK = 0xFF;

    /**
     * 操作模式，加密或解密
     */
    private static final String ENCRYPT = "e";
    private static final String DECRYPT = "d";

    /**
     * 移位数值
     */
    private static final int SHIFT_ONE_BIT = 1;
    private static final int SHIFT_TWO_BIT = 2;
    private static final int SHIFT_THREE_BIT = 3;
    private static final int SHIFT_FOUR_BIT = 4;
    private static final int SHIFT_FIVE_BIT = 5;
    private static final int SHIFT_SIX_BIT = 6;
    private static final int SHIFT_SEVEN_BIT = 7;
    private static final int SHIFT_EIGHT_BIT = 8;
    private static final int SHIFT_TWELVE_BIT = 12;
    private static final int SHIFT_SIXTEEN_BIT = 16;
    private static final int SHIFT_TWENTY_BIT = 20;
    private static final int SHIFT_TWENTY_FOUR_BIT = 24;

    /**
     * initial permutation
     */
    private static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    /**
     * inverse initial permutation
     */
    private static final int[] IIP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    /**
     * expansion permutation
     */
    private static final int[] EP = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    /**
     * S-Box
     */
    private static final int[][] S = {
            //S1
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13},
            //S2
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9},
            //S3
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12},
            //S4
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14},
            //S5
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3},
            //S6
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13},
            //S7
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12},
            //S8
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
    };

    /**
     * P-Box
     */
    private static final int[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    /**
     * initial permutation choice for key
     */
    private static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    /**
     * permutation choice for sub key
     */
    private static final int[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    /**
     * key shift, the first number is unused
     */
    private static final int[] KS = {
            -1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    private byte[][] subkey;

    public DES(byte[] initKey) {
        subkey = getSubKey(initKey);
    }

    /**
     * 根据总的bit位置计数值计算byte位置
     *
     * @param pos 总的bit位置计数值
     * @return byte位置
     */
    private static int getBytePosition(int pos) {
        return (pos - POSITION_OFFSET) / BITS_OF_BYTE;
    }

    /**
     * 根据总的bit位置计数值计算在相应byte中的bit位置
     *
     * @param pos 总的bit位置计数值
     * @return
     */
    private static int getBitPosition(int pos) {
        return (pos - POSITION_OFFSET) % BITS_OF_BYTE;
    }

    /**
     * 返回pos处的bit，pos来自各常量盒
     *
     * @param bytes 字节数组
     * @param pos   bit位置
     * @return 0 or 1
     */
    private static int getBitFromByteArray(byte[] bytes, int pos) {
        int bytePos = getBytePosition(pos);
        int bitPos = getBitPosition(pos);
        int shift = BITS_OF_BYTE - POSITION_OFFSET - bitPos;
        int bit = (bytes[bytePos] >> shift) & BIT7_MASK;
        return bit;
    }

    /**
     * 设置pos处的bit值为value
     *
     * @param bytes byte数组
     * @param pos   bit位置
     * @param value 设置的值
     */
    private static void setBitIntoByteArray(byte[] bytes, int pos, int value) {
        int bytePos = getBytePosition(pos);
        int bitPos = getBitPosition(pos);
        int shift = BITS_OF_BYTE - POSITION_OFFSET - bitPos;
        bytes[bytePos] = (byte) ((((0xFF7F >> bitPos) & bytes[bytePos]) & 0xFF) | (value << shift));
    }

    /**
     * 执行置换操作
     *
     * @param input byte数组
     * @param p     置换操作表
     * @return 置换后的byte数组
     */
    private byte[] permute(byte[] input, int[] p) {
        byte[] output = new byte[p.length / BITS_OF_BYTE];
        for (int i = 0; i < p.length; i++) {
            int bit = getBitFromByteArray(input, p[i]);
            setBitIntoByteArray(output, i + POSITION_OFFSET, bit);
        }
        return output;
    }

    /**
     * 对输入的两个字节数组进行异或操作
     *
     * @param input1
     * @param input2
     * @return
     */
    private static byte[] XOR(byte[] input1, byte[] input2) {
        byte[] output = new byte[input1.length];
        for (int i = 0; i < input1.length; i++) {
            output[i] = (byte) (input1[i] ^ input2[i]);
        }
        return output;
    }

    /**
     * 密钥移位
     *
     * @param masterKey 密钥
     * @param round     轮数
     */
    private void shiftKey(byte[] masterKey, int round) {
        int shiftBit1, shiftBit2, mask1, mask2;
        if (KS[round] == 1) {
            shiftBit1 = SHIFT_SEVEN_BIT;
            shiftBit2 = SHIFT_THREE_BIT;
            mask1 = BIT0_MASK;
            mask2 = BIT4_MASK;
        } else {
            shiftBit1 = SHIFT_SIX_BIT;
            shiftBit2 = SHIFT_TWO_BIT;
            mask1 = HIGH_TWO_BITS_MASK;
            mask2 = BIT4_BIT5_MASK;
        }
        int lvalue = (((masterKey[3] & HIGH_FOUR_BITS_MASK) >> SHIFT_FOUR_BIT) |
                ((masterKey[2] & BYTE_MASK) << SHIFT_FOUR_BIT) |
                ((masterKey[1] & BYTE_MASK) << SHIFT_TWELVE_BIT) |
                ((masterKey[0] & BYTE_MASK) << SHIFT_TWENTY_BIT));
        lvalue = (lvalue << KS[round]) | ((masterKey[0] & mask1) >> shiftBit1);

        int rvalue = ((masterKey[6] & BYTE_MASK) |
                ((masterKey[5] & BYTE_MASK) << SHIFT_EIGHT_BIT) |
                ((masterKey[4] & BYTE_MASK) << SHIFT_SIXTEEN_BIT) |
                ((masterKey[3] & LOW_FOUR_BITS_MASK) << SHIFT_TWENTY_FOUR_BIT));
        rvalue = (rvalue << KS[round]) | ((masterKey[3] & mask2) >> shiftBit2);

        masterKey[3] = (byte) ((lvalue & LOW_FOUR_BITS_MASK) << SHIFT_FOUR_BIT);
        lvalue >>= SHIFT_FOUR_BIT;
        masterKey[2] = (byte) (lvalue & BYTE_MASK);
        lvalue >>= SHIFT_EIGHT_BIT;
        masterKey[1] = (byte) (lvalue & BYTE_MASK);
        lvalue >>= SHIFT_EIGHT_BIT;
        masterKey[0] = (byte) (lvalue & BYTE_MASK);

        masterKey[6] = (byte) (rvalue & BYTE_MASK);
        rvalue >>= SHIFT_EIGHT_BIT;
        masterKey[5] = (byte) (rvalue & BYTE_MASK);
        rvalue >>= SHIFT_EIGHT_BIT;
        masterKey[4] = (byte) (rvalue & BYTE_MASK);
        rvalue >>= SHIFT_EIGHT_BIT;
        masterKey[3] |= (byte) (rvalue & LOW_FOUR_BITS_MASK);
    }

    /**
     * S-Box 运算
     *
     * @param input
     * @return
     */
    private byte[] sBox(byte[] input) {
        byte[] output = new byte[4];
        // S1
        int row = (input[0] & BIT0_MASK) >> SHIFT_SIX_BIT;
        row |= (input[0] & BIT5_MASK) >> SHIFT_TWO_BIT;

        int column = (input[0] & 0x78) >> 3;

        output[0] |= (byte) (S[0][row * 16 + column] << SHIFT_FOUR_BIT);

        // S2
        row = (input[0] & BIT6_MASK);
        row |= ((input[1] & BIT3_MASK) >> SHIFT_FOUR_BIT);

        column = ((input[0] & BIT7_MASK) << SHIFT_THREE_BIT);
        column |= ((input[1] & 0xE0) >> SHIFT_FIVE_BIT);

        output[0] |= (byte) (S[1][row * 16 + column]);

        // S3
        row = (input[1] & BIT4_MASK) >> SHIFT_TWO_BIT;
        row |= (input[2] & BIT1_MASK) >> SHIFT_SIX_BIT;

        column = (input[1] & 0x07) << SHIFT_ONE_BIT;
        column |= (input[2] & BIT0_MASK) >> SHIFT_SEVEN_BIT;

        output[1] |= (byte) (S[2][row * 16 + column] << SHIFT_FOUR_BIT);

        // S4
        row = (input[2] & BIT2_MASK) >> SHIFT_FOUR_BIT;
        row |= (input[2] & BIT7_MASK);

        column = (input[2] & 0x1E) >> SHIFT_ONE_BIT;

        output[1] |= (byte) (S[3][row * 16 + column]);

        // S5
        row = (input[3] & BIT0_MASK) >> SHIFT_SIX_BIT;
        row |= (input[3] & BIT5_MASK) >> SHIFT_TWO_BIT;

        column = (input[3] & 0x78) >> SHIFT_THREE_BIT;

        output[2] |= (byte) (S[4][row * 16 + column] << SHIFT_FOUR_BIT);

        // S6
        row = input[3] & BIT6_MASK;
        row |= (input[4] & BIT3_MASK) >> SHIFT_FOUR_BIT;

        column = (input[3] & BIT7_MASK) << SHIFT_THREE_BIT;
        column |= (input[4] & 0xE0) >> SHIFT_FIVE_BIT;

        output[2] |= (byte) (S[5][row * 16 + column]);

        // S7
        row = (input[4] & BIT4_MASK) >> SHIFT_TWO_BIT;
        row |= (input[5] & BIT1_MASK) >> SHIFT_SIX_BIT;

        column = (input[4] & 0x07) << SHIFT_ONE_BIT;
        column |= (input[5] & BIT0_MASK) >> SHIFT_SEVEN_BIT;

        output[3] |= (byte) (S[6][row * 16 + column] << SHIFT_FOUR_BIT);

        // S8
        row = (input[5] & BIT2_MASK) >> SHIFT_FOUR_BIT;
        row |= input[5] & BIT7_MASK;

        column = (input[5] & 0x1E) >> SHIFT_ONE_BIT;

        output[3] |= (byte) (S[7][row * 16 + column]);

        return output;
    }

    /**
     * 生成16轮子密钥
     *
     * @param iniKey 初始密钥
     * @return 16轮子密钥
     */
    private byte[][] getSubKey(byte[] iniKey) {
        byte[][] subKey = new byte[16][48];
        byte[] masterKey = permute(iniKey, PC1);
        for (int round = 1; round <= 16; round++) {
            shiftKey(masterKey, round);
            subKey[round - POSITION_OFFSET] = permute(masterKey, PC2);
        }
        return subKey;
    }

    /**
     * 对64bit长的消息块进行加解密
     *
     * @param block  64bit长消息块块
     * @return 加解密后的消息块
     */
    private byte[] processBlock(byte[] block, String mode) {
        // initial permutation
        byte[] processedBlock = permute(block, IP);

        byte[] left = new byte[4];
        byte[] right = new byte[4];
        byte[] leftNext, rightNext;

        for (int i = 0; i < 4; i++) {
            left[i] = processedBlock[i];
            right[i] = processedBlock[i + 4];
        }

        int start, step;
        if (mode.equals(ENCRYPT)) {
            start = 0;
            step = 1;
        } else {
            start = 15;
            step = -1;
        }

        for (int i = start; i >= 0 && i < 16; i += step) {
            leftNext = right;
            byte[] expandRight = permute(right, EP);
            expandRight = XOR(expandRight, subkey[i]);
            right = sBox(expandRight);
            right = permute(right, P);
            rightNext = XOR(left, right);
            left = leftNext;
            right = rightNext;
        }
        // 合并左右部分，注意需要交换左右部分
        for (int i = 0; i < 4; i++) {
            processedBlock[i] = right[i];
            processedBlock[i + 4] = left[i];
        }

        return permute(processedBlock, IIP);
    }

    private byte[] encryptBlock(byte[] block) {
        return processBlock(block, ENCRYPT);
    }

    private byte[] decryptBlock(byte[] block) {
        return processBlock(block, DECRYPT);
    }

    public byte[] encrypt(byte[] input) {
        int partLen = 8;
        int len = input.length;
        int number = (int) Math.ceil((double) len / partLen);
        int mod = len % partLen;

        byte[] output;
        byte[] tempInput;
        if (mod == 0) {
            tempInput = new byte[number * partLen + 8];
            System.arraycopy(input, 0, tempInput, 0, len);
            for (int i = 0; i < partLen; i++) {
                tempInput[len + i] = (byte) partLen;
            }
            output = new byte[number * partLen + partLen];
        } else {
            tempInput = new byte[number * partLen];
            System.arraycopy(input, 0, tempInput, 0, len);
            for (int i = 0; i < partLen - mod; i++) {
                tempInput[len + i] = (byte) (partLen - mod);
            }
            output = new byte[number * partLen];
        }

        for (int i = 0; i < (output.length / partLen); i++) {
            byte[] temp = new byte[partLen];
            System.arraycopy(tempInput, i * partLen, temp, 0, partLen);
            System.arraycopy(encryptBlock(temp), 0, output, i * partLen, partLen);
        }

        return output;
    }

    public byte[] decrypt(byte[] input) {
        int len = input.length;
        int partLen = 8;
        int number = len / partLen;


        byte[] tempOutput = new byte[len];
        byte[] output;

        for (int i = 0; i < number; i++) {
            byte[] temp = new byte[partLen];
            System.arraycopy(input, i * partLen, temp, 0, partLen);
            System.arraycopy(decryptBlock(temp), 0, tempOutput, i * partLen, partLen);
        }
        if (tempOutput[len - 1] == (byte)partLen) {
            output = new byte[len - partLen];
            System.arraycopy(tempOutput, 0, output, 0, len - partLen);
        } else {
            int x = tempOutput[len - 1];
            output = new byte[len - x];
            System.arraycopy(tempOutput, 0, output, 0, len - x);
        }
        return output;
    }
}
