
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
