package ope;

import ope.util.Encoder;

/**
 * General interface for OPE key.
 * Provides methods for encrypting and decrypting data, as well as
 * encoding the key to binary format.
 * 
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public interface Key {

	public byte[] encodeKey();

	public byte[] encrypt(byte[] plaintext);

	public default byte[] encryptBoolean(boolean plaintext) {
		return encrypt(Encoder.encodeBoolean(plaintext));
	}

	public default byte[] encryptByte(byte plaintext) {
		return encrypt(Encoder.encodeByte(plaintext));
	}

	public default byte[] encryptShort(short plaintext) {
		return encrypt(Encoder.encodeShort(plaintext));
	}

	public default byte[] encryptInt(int plaintext) {
		return encrypt(Encoder.encodeInt(plaintext));
	}

	public default byte[] encryptLong(long plaintext) {
		return encrypt(Encoder.encodeLong(plaintext));
	}

	public default byte[] encryptFloat(float plaintext) {
		return encrypt(Encoder.encodeFloat(plaintext));
	}

	public default byte[] encryptDouble(double plaintext) {
		return encrypt(Encoder.encodeDouble(plaintext));
	}

	public default byte[] encryptChar(char plaintext) {
		return encrypt(Encoder.encodeChar(plaintext));
	}

	public default byte[] encryptString(String plaintext) {
		return encrypt(Encoder.encodeString(plaintext));
	}

	public byte[] decrypt(byte[] ciphertext);

	public default boolean decryptBoolean(byte[] ciphertext) {
		return Encoder.decodeBoolean(decrypt(ciphertext));
	}

	public default byte decryptByte(byte[] ciphertext) {
		return Encoder.decodeByte(decrypt(ciphertext));
	}

	public default short decryptShort(byte[] ciphertext) {
		return Encoder.decodeShort(decrypt(ciphertext));
	}

	public default int decryptInt(byte[] ciphertext) {
		return Encoder.decodeInt(decrypt(ciphertext));
	}

	public default long decryptLong(byte[] ciphertext) {
		return Encoder.decodeLong(decrypt(ciphertext));
	}

	public default float decryptFloat(byte[] ciphertext) {
		return Encoder.decodeFloat(decrypt(ciphertext));
	}

	public default double decryptDouble(byte[] ciphertext) {
		return Encoder.decodeDouble(decrypt(ciphertext));
	}

	public default char decryptChar(byte[] ciphertext) {
		return Encoder.decodeChar(decrypt(ciphertext));
	}

	public default String decryptString(byte[] ciphertext) {
		return Encoder.decodeString(decrypt(ciphertext));
	}
}
