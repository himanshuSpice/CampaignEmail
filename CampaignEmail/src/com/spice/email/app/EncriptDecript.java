package com.spice.email.app;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncriptDecript {

	private String pass;
	private static final String ALGORITHM = "AES";
	private static final byte[] keyValue = new byte[] { 'T', 'h', 'i', 's',
			'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

	public static String encrypt(String email, String department)
			throws Exception {

		String parameter = email + "," + department;
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encValue = c.doFinal(parameter.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encValue);
		return encryptedValue;
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGORITHM);
		return key;
	}
	public static String getMessage(String strUnicode) {
		String strReturnValue = "", strSubUnicode = "";
		try {
			int intLength = strUnicode.length();
			if (strUnicode.length() % 4 != 0) {
				return "";
			}

			while (intLength > 0) {
				strSubUnicode = strUnicode.substring(0, 4);
				strUnicode = strUnicode.substring(4);
				strReturnValue += (char) Integer.parseInt(strSubUnicode, 16)
						+ "";
				intLength -= 4;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strReturnValue;
	}
	public static String getUnicode(String strMessage) {
		int intMessageLength = strMessage.length(), intCounter = 0;
		String strReturnValue = "";
		while (intMessageLength > intCounter) {
			strReturnValue += getProperUnicode(Integer.toHexString((int) strMessage.charAt(intCounter++)) + "");
		}
		return strReturnValue;

	}

	public static String getProperUnicode(String strUnicode) {
		if (strUnicode != null && strUnicode.length() == 1) {
			return "000" + strUnicode;
		}

		if (strUnicode != null && strUnicode.length() == 2) {
			return "00" + strUnicode;
		}
		if (strUnicode != null && strUnicode.length() == 3) {
			return "0" + strUnicode;
		}
		return strUnicode;
	}
	public static void main(String args[])
	{
		String getUnicodeString=getUnicode("hkkhh");
		
		System.out.println("Unicode to language="+getMessage("0906092a0915093e002000200909092e0902091700200020092e09480902002000200938094d0935093e091709240020002009390948002000200021"));
	}
}
