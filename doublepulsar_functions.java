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




