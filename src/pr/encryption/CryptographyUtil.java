package pr.encryption;

public class CryptographyUtil {
	private final int numberToEncrypt = 357; // dowolna liczba z zakresu 227-872
												// (¿eby po dodaniu do byte
												// wynik zawsze by³ 3 cyfrowy)

	public String encryptData(String dataToEncrypt) {
		byte[] d = dataToEncrypt.getBytes();
		int[] tb = new int[d.length];
		for (int i = 0; i < d.length; i++) {
			tb[i] = d[i] + numberToEncrypt;
		}
		StringBuilder outData = new StringBuilder("");
		for (int x : tb) {
			outData.append(x);
		}
		return outData.toString();
	}

	public String decryptData(String dataToDecrypt) {
		byte[] t = new byte[dataToDecrypt.length() / 3];
		int j = 0;
		for (int i = 0; i < dataToDecrypt.length(); i += 3) {
			String ss = dataToDecrypt.substring(i, i + 3);
			int dd = Integer.valueOf(ss) - numberToEncrypt;
			t[j] = (byte) dd;
			j++;
		}
		return new String(t);
	}
}
