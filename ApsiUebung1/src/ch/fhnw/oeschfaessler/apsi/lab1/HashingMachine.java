package ch.fhnw.oeschfaessler.apsi.lab1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class HashingMachine {
	
	private final static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0 };

	public static int createHash(byte[] input) {
		input = preprocess(input);
		return create(input);
	}

	private static byte[] preprocess(byte[] input) {
		
		byte[] length = ByteBuffer.allocate(8).putLong(input.length).array();
		int r = 8 - (input.length % 8); 
		byte[] out = new byte[input.length + r + 8];

		// structure the input
		for (int i = 0; i < input.length; i++) out[i] = input[i];
		if (r > 0)                             out[input.length] = -128; 
		for (int i = 1; i < r; i++)            out[input.length + i] = 0;
		for (int i = 0; i < 8; i++)            out[out.length - 8 + i] = length[i];

		return out;
		
	}
	
	private static int create(byte[] input) {
		
		BlockCipher engine = new DESEngine();
		BufferedBlockCipher cipher = new PaddedBlockCipher(engine);
		
		byte[] desOut;
		byte[] tempState = new byte[8];
		byte[] hash = iv.clone();

		for (int i = 0; i < input.length; i += 8) {
			try {
				desOut = new byte[16];
				cipher.init(true, new KeyParameter(hash));
				cipher.processBytes(input, i, 8, desOut, 0);
				cipher.doFinal(desOut, 0);
				for (int j = 0; j < hash.length; j++)
					tempState[j] = (byte) ((desOut[j] ^ desOut[j + 8]) ^ hash[j]);
			} catch (CryptoException ce) { System.err.println(ce); }

			// swap
			byte[] tmp = tempState;
			tempState = hash;
			hash = tmp;
		}

		ByteBuffer buffer = ByteBuffer.wrap(hash);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		long result = buffer.getLong();
		return (int) (result >>> 32) ^ Integer.reverse((int) result);
	}
}