package ope.mope;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import ope.Key;
import ope.OpeException;

/**
 * Key implementation of Boldyreva et al's Modular Order-Preserving Encryption scheme.
 * 
 * It is important to note that MOPE is not a standalone encryption scheme, but rather
 * an enhancement that can be used with an existing encryption scheme.
 * 
 * Reference:
 * 
 * Boldyreva, A., Chenette, N., & O'Neill, A. (2011, August).
 * Order-preserving encryption revisited: Improved security analysis and alternative solutions.
 * In Annual Cryptology Conference (pp. 578-595). Springer, Berlin, Heidelberg.
 * https://link.springer.com/content/pdf/10.1007/978-3-642-01001-9_13.pdf
 * 
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public class MopeKey implements Key {
	
	private final Key key;
	
	private final int plaintextBytes;
	
	private final BigInteger offset;
	private final BigInteger max;
	
	public MopeKey(Key key, int plaintextBytes, BigInteger offset) {
		this.key = key;
		
		this.plaintextBytes = plaintextBytes;
		this.offset = offset;
		
		BigInteger max = BigInteger.valueOf(1);
		for (int i = 0; i < plaintextBytes; i++) { max = max.shiftLeft(8); }
		this.max = max;
	}
	
	public Key getKey() {
		return key;
	}
	
	public byte[] encodeKey() throws OpeException {
		// Encode key
		byte[] keyBytes = key.encodeKey();
		
		// Calculate required size and prepare buffer
		int size = Integer.BYTES + plaintextBytes + keyBytes.length;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		
		// Write plaintext size and offset
		buffer.putInt(plaintextBytes);
		byte[] offsetBytes = offset.toByteArray();
		if (offsetBytes.length >= plaintextBytes) {
			buffer.put(offsetBytes, offsetBytes.length - plaintextBytes, plaintextBytes);
			
		} else {
			for (int i = plaintextBytes - offsetBytes.length; i > 0; i--) { buffer.put((byte) 0); }
			buffer.put(offsetBytes);
		}
		
		// Write key
		buffer.put(keyBytes);
		
		// Done
		return buffer.array();
	}

	public byte[] encrypt(byte[] plaintext) throws OpeException {
		// Check plaintext size
		if (plaintext.length > plaintextBytes) {
			throw new OpeException("Plaintext cannot exceed " + plaintextBytes + " bytes in size.");
		}

		// Convert plaintext to integer
		byte[] temp = new byte[plaintext.length + 1];
		System.arraycopy(plaintext, 0, temp, 1, plaintext.length);
		BigInteger plain = new BigInteger(temp);
		
		// Offset
		BigInteger plain2 = plain.subtract(offset);
		if (plain2.compareTo(BigInteger.valueOf(0)) < 0) { plain2 = plain2.add(max); }
		plain2 = plain2.mod(max);
		
		// Get bytes
		byte[] plaintext2;
		temp = plain2.toByteArray();
		if (temp.length == plaintextBytes) { plaintext2 = temp; }
		else {
			plaintext2 = new byte[plaintextBytes];
			if (temp.length < plaintextBytes) {
				System.arraycopy(temp, 0, plaintext2, plaintextBytes - temp.length, temp.length);
				
			} else {
				System.arraycopy(temp, temp.length - plaintextBytes, plaintext2, 0, plaintextBytes);
			}
		}
		
		// Encrypt
		return key.encrypt(plaintext2);
	}

	public byte[] decrypt(byte[] ciphertext) throws OpeException {
		// Decrypt
		byte[] plaintext2 = key.decrypt(ciphertext);
		if (plaintext2.length > plaintextBytes) {
			throw new OpeException("Plaintext cannot exceed " + plaintextBytes + " bytes in size.");
		}
		
		// Convert to integer
		byte[] temp = new byte[plaintext2.length + 1];
		System.arraycopy(plaintext2, 0, temp, 1, plaintext2.length);
		BigInteger plain2 = new BigInteger(temp);
		
		// Remove offset
		BigInteger plain = plain2.add(offset).mod(max);
		
		// Get bytes
		byte[] plaintext;
		temp = plain.toByteArray();
		if (temp.length == plaintextBytes) { plaintext = temp; }
		else {
			plaintext = new byte[plaintextBytes];
			if (temp.length < plaintextBytes) {
				System.arraycopy(temp, 0, plaintext, plaintextBytes - temp.length, temp.length);
				
			} else {
				System.arraycopy(temp, temp.length - plaintextBytes, plaintext, 0, plaintextBytes);
			}
		}
		return plaintext;
	}
}
