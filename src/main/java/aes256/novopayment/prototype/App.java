package aes256.novopayment.prototype;

import javax.swing.SwingUtilities;

import aes256.novopayment.prototype.component.IDecrypt;
import aes256.novopayment.prototype.component.IEncrypt;
import aes256.novopayment.prototype.component.impl.Decyrpt;
import aes256.novopayment.prototype.component.impl.Encyrpt;
import aes256.novopayment.prototype.frame.component.AESEncryptionFrame;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			IDecrypt iDecrypt = new Decyrpt();
			IEncrypt iEncrypt = new Encyrpt();
			AESEncryptionFrame aesEncryptionFrame = new AESEncryptionFrame(iDecrypt, iEncrypt);
			try {
				aesEncryptionFrame.doOnStart();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
