package aes256.novopayment.prototype;

import javax.swing.*;

import aes256.novopayment.prototype.component.IDecrypt;
import aes256.novopayment.prototype.component.IEncrypt;
import aes256.novopayment.prototype.component.impl.Decyrpt;
import aes256.novopayment.prototype.component.impl.Encyrpt;
import aes256.novopayment.prototype.frame.component.AESEncryptionFrame;

import java.io.IOException;

public class App {
	public static void main(String[] args) {

		try {
			// Establecer el Look and Feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// Alternativamente, puedes usar Nimbus:
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> {
			IDecrypt iDecrypt = new Decyrpt();
			IEncrypt iEncrypt = new Encyrpt();
			AESEncryptionFrame aesEncryptionFrame = null;
			try {
				aesEncryptionFrame = new AESEncryptionFrame(iDecrypt, iEncrypt);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			try {
				aesEncryptionFrame.doOnStart();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
