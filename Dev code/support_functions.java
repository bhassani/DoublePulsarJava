//original source from: https://github.com/jc65536/File-Encryptor/blob/master/src/Encryptor.java
//Author: jc65536
public class Encryptor {
	private String originalString;
	private String encryptionKey;
	private String encryptedString = "";
	public Encryptor(String inputString, String key) {
		originalString = inputString;
		encryptionKey = key;
		for (int a = 0; a < originalString.length(); ++a) {
			char charInFile = originalString.charAt(a);
			char charInKey = encryptionKey.charAt(a % encryptionKey.length());
			try {
				switch (originalString.substring(a, a + 2)) {
				case "\t":
					charInFile = '\t';
					break;
				case "\n":
					charInFile = '\n';
					break;
				}
			} catch (StringIndexOutOfBoundsException e) {
			}
			encryptedString += Integer.toHexString((int) (charInFile ^ charInKey)) + " ";
		}
	}

	public String getEncryptedString() {
		return encryptedString;
	}
}

/*
https://javarevisited.blogspot.com/2020/04/7-examples-to-read-file-into-byte-array-in-java.html#axzz76kuulJgU
*/
//readFile into buffer
public static byte[] readFile(String file) throws IOException {   
  File f = new File(file);
  byte[] buffer = new byte[(int)f.length()]; 
  FileInputStream is = new FileInputStream(file);  
  is.read(buffer);   
  is.close();   
  return buffer;
}

/* implemention 2:

import java.io
https://docs.oracle.com/javase/9/docs/api/java/io/FileInputStream.html
*/

/* implementation 3: 
https://attacomsian.com/blog/java-read-write-binary-files */

public static void readBinaryFile() 
{
	//allow user to select which DLL but for testing we won't allow input at this time
	try {
	    // create a reader
	    FileInputStream fis = new FileInputStream(new File("payload.dll"));

	    // read one byte at a time
	    int ch;
	    while ((ch = fis.read()) != -1) {
		System.out.print((char) ch);
	    }

	    // close the reader
	    fis.close();

	} catch (IOException ex) {
	    ex.printStackTrace();
	}
}

