package aes256.novopayment.prototype.component.impl;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import aes256.novopayment.prototype.component.IDecrypt;
import aes256.novopayment.prototype.constants.Constants;

public class Decyrpt implements IDecrypt {
	
	
	@Override
	public String doOnDecrypt(String strCipherText, String key) {
		byte[] byteDecryptedText;
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), Constants.ALGORITHM);
			Cipher aesCipherForDecryption = Cipher.getInstance(Constants.ENCRYPTION_SCHEME);
			aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(Constants.INITIALIZATION_VECTOR));
			byteDecryptedText = aesCipherForDecryption.doFinal(Base64.getDecoder().decode(strCipherText.getBytes()));
			return  new String(byteDecryptedText, Constants.CHARSET_NAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

}
