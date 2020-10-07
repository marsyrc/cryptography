package edu.seu.algs;


public class AES {
    /**
     * Mask constants
     */
    private static final int HIGH_FOUR_BITS_MASK = 0xF0;
    private static final int LOW_FOUR_BITS_MASK = 0x0F;

    /**
     * shift constants
     */
    private static final int SHIFT_FOUR_BITS = 4;

    private static final int BITS_OF_BYTE = 8;

    /**
     * Mode constants
     */
    private static final String ENCRYPT = "e";
    private static final String DECRYPT = "d";

    /**
     * S-Box
     */
    private static final int[][] S = {
            {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
            {0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
            {0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
            {0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
            {0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
            {0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
            {0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
            {0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
            {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
            {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
            {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
            {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
            {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
            {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
            {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
            {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16}
    };

    /**
     * Inverse S-Box
     */
    private static final int[][] IS = {
            {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
            {0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
            {0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
            {0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
            {0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
            {0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
            {0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
            {0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
            {0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
            {0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
            {0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
            {0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
            {0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
            {0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
            {0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
            {0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
    };

    /**
     * Round constant
     * first element is unused
     */
    private static final byte[] RCON = {
            (byte) 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0x1b, 0x36
    };

    /**
     * matrix used in mix columns
     */
    private static final byte[][] MIX_COLUMN_MATRIX = {
            {0x02, 0x03, 0x01, 0x01},
            {0x01, 0x02, 0x03, 0x01},
            {0x01, 0x01, 0x02, 0x03},
            {0x03, 0x01, 0x01, 0x02}
    };

    /**
     * matrix used in inverse mix columns
     */
    private static final byte[][] INVERSE_MIX_COLUMN_MATRIX = {
            {0x0E, 0x0B, 0x0D, 0x09},
            {0x09, 0x0E, 0x0B, 0x0D},
            {0x0D, 0x09, 0x0E, 0x0B},
            {0x0B, 0x0D, 0x09, 0x0E}
    };

    private byte[][][] subkey;

    public AES(byte[] iniKey) {
        subkey = keyExpansions(iniKey);
    }

    /**
     * GF(2^8)域上的乘法
     *
     * @param b1
     * @param b2
     * @return
     */
    private static byte gfMul(byte b1, byte b2) {
        if ((b1 == 0) || (b2 == 0)) {
            return 0;
        }
        int i1 = b1 & 0xFF, i2 = b2 & 0xFF;
        if (i1 < i2) {
            int temp = i1;
            i1 = i2;
            i2 = temp;
        }
        int result = (i2 & 0x01) == 0 ? 0 : i1;
        int shiftBits = 1, shiftResult = i1;
        while ((shiftBits < BITS_OF_BYTE) && ((i2 >> shiftBits) > 0)) {
            if ((shiftResult & 0x80) > 0) {
                shiftResult = ((shiftResult << 1) & 0xFF) ^ 0x1B;
            } else {
                shiftResult = (shiftResult << 1) & 0xFF;
            }
            if (((i2 >> shiftBits) & 0x01) > 0) {
                result ^= shiftResult;
            }
            shiftBits++;
        }
        return (byte) result;
    }

    /**
     * substitute operation, performed by S-Box or inverse S-Box in AES
     *
     * @param input
     * @param box
     * @return
     */
    private static byte[][] substitute(byte[][] input, int[][] box) {
        byte[][] output = new byte[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                output[i][j] = subByte(input[i][j], box);
            }
        }
        return output;
    }

    /**
     * 单字节替换
     *
     * @param input
     * @param box
     * @return
     */
    private static byte subByte(byte input, int[][] box) {
        int row = (input & HIGH_FOUR_BITS_MASK) >> SHIFT_FOUR_BITS;
        int column = input & LOW_FOUR_BITS_MASK;
        return (byte) box[row][column];
    }

    /**
     * shift rows step
     *
     * @param input
     * @param forward 1 if forward
     *                -1 if inverse
     */
    private static void shiftRows(byte[][] input, int forward) {
        for (int i = 1; i < input.length; i++) {
            input[i] = cycleShift(input[i], i * forward);
        }
    }

    /**
     * 循环移位，以byte为单位操作
     *
     * @param input
     * @param shiftBytes
     * @return
     */
    private static byte[] cycleShift(byte[] input, int shiftBytes) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[(i + shiftBytes + input.length) % input.length];
        }
        return output;
    }

    /**
     * mix column step
     *
     * @param input
     * @param matrix MIX_COLUMN_MATRIX if performs mix columns
     *               INVERSE_MIX_COLUMN_MATRIX if performs inverse mix columns
     */
    private static byte[][] mixColumns(byte[][] input, byte[][] matrix) {
        byte[][] output = new byte[input.length][input[0].length];
        for (int row = 0; row < output.length; row++) {
            for (int column = 0; column < output[0].length; column++) {
                for (int j = 0; j < input[0].length; j++) {
                    output[row][column] ^= gfMul(matrix[row][j], input[j][column]);
                }
            }
        }
        return output;
    }

    /**
     * AddRoundKey step
     *
     * @param state
     * @param subkey
     * @return
     */
    private static byte[][] addRoundKey(byte[][] state, byte[][] subkey) {
        byte[][] output = new byte[state.length][state[0].length];
        for (int row = 0; row < state.length; row++) {
            for (int column = 0; column < state[0].length; column++) {
                output[row][column] = (byte) (state[row][column] ^ subkey[row][column]);
            }
        }
        return output;
    }

    /**
     * key expansion step
     *
     * @param iniKey 16 bytes initial key
     * @return 11 round sub key
     */
    private byte[][][] keyExpansions(byte[] iniKey) {
        byte[][][] subkey = new byte[11][4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                subkey[0][j][i] = iniKey[4 * i + j];
            }
        }
        for (int round = 1; round < 11; round++) {
            byte[] chosenWord = new byte[4];
            for (int i = 0; i < 4; i++) {
                chosenWord[i] = subkey[round - 1][i][3];
            }
            chosenWord = gFunction(chosenWord, round);
            for (int i = 0; i < 4; i++) {
                subkey[round][i][0] = (byte) (subkey[round - 1][i][0] ^ chosenWord[i]);
            }

            for (int wordIndex = 1; wordIndex < 4; wordIndex++) {
                for (int byteIndex = 0; byteIndex < 4; byteIndex++) {
                    subkey[round][byteIndex][wordIndex] =
                            (byte) (subkey[round - 1][byteIndex][wordIndex] ^ subkey[round][byteIndex][wordIndex - 1]);
                }
            }
        }
        return subkey;
    }

    private static byte[] gFunction(byte[] chosenWord, int round) {
        // cycle left shift
        byte[] output = cycleShift(chosenWord, 1);
        // S-Box
        for (int i = 0; i < 4; i++) {
            output[i] = subByte(output[i], S);
        }
        // xor with round constant
        output[0] = (byte) (output[0] ^ RCON[round]);
        return output;
    }

    /**
     * 对16 bytes的消息块进行aes加密
     *
     * @param block  16 bytes消息块
     * @return
     */
    private byte[] encryptBlock(byte[] block) {
        byte[] output = new byte[block.length];
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = block[4 * i + j];
            }
        }
        int roundCount = 0;
        // initial add round key
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = (byte) (state[i][j] ^ subkey[roundCount][i][j]);
            }
        }
        roundCount++;
        for (; roundCount < 10; roundCount++) {
            // substitute bytes
            state = substitute(state, S);
            // shift rows
            shiftRows(state, 1);
            // mix columns
            state = mixColumns(state, MIX_COLUMN_MATRIX);
            //add round keys
            state = addRoundKey(state, subkey[roundCount]);
        }
        // final transfer
        state = substitute(state, S);
        shiftRows(state, 1);
        state = addRoundKey(state, subkey[roundCount]);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output[4 * i + j] = state[j][i];
            }
        }
        return output;
    }

    private byte[] decryptBlock(byte[] block) {
        byte[] output = new byte[block.length];
        byte[][] state = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = block[4 * i + j];
            }
        }
        int roundCount = 10;
        // initial add round key
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = (byte) (state[i][j] ^ subkey[roundCount][i][j]);
            }
        }
        roundCount--;
        for (; roundCount > 0; roundCount--) {
            // inverse shift rows
            shiftRows(state, -1);
            // inverse substitute bytes
            state = substitute(state, IS);
            // add round keys
            state = addRoundKey(state, subkey[roundCount]);
            // inverse mix column
            state = mixColumns(state, INVERSE_MIX_COLUMN_MATRIX);
        }
        // final transfer
        shiftRows(state, -1);
        state = substitute(state, IS);
        state = addRoundKey(state, subkey[roundCount]);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output[4 * i + j] = state[j][i];
            }
        }
        return output;
    }

    public byte[] encrypt(byte[] input) {
        int partLen = 16;
        int len = input.length;
        int number = (int) Math.ceil((double) len / partLen);
        int mod = len % partLen;

        byte[] output;
        byte[] tempInput;
        if (mod == 0) {
            tempInput = new byte[number * partLen + 16];
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
        int partLen = 16;
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
