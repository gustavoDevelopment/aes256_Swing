package aes256.novopayment.prototype.component.impl;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import aes256.novopayment.prototype.component.IEncrypt;
import aes256.novopayment.prototype.constants.Constants;

public class Encyrpt implements IEncrypt{
	
	@Override
	public String doOnEncrypt(String strCipherText, String key) {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),Constants.ALGORITHM);
			Cipher cipher = Cipher.getInstance(Constants.ENCRYPTION_SCHEME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(Constants.INITIALIZATION_VECTOR));

			byte[] byteDataToEncrypt = strCipherText.getBytes(Constants.CHARSET_NAME);
			byte[] byteCipherText = cipher.doFinal(byteDataToEncrypt);
			strCipherText = Base64.getEncoder().encodeToString(byteCipherText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strCipherText;
	}

}
