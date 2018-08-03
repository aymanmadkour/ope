package ope.mope;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import ope.Cipher;
import ope.Key;
import ope.OpeException;

/**
 * Cipher implementation of Boldyreva et al's Modular Order-Preserving Encryption scheme.
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
public class MopeCipher implements Cipher {

	private static final int DEFAULT_PLAINTEXT_BYTES = 8;
	
	private final SecureRandom rnd = new SecureRandom();
	
	private final Cipher cipher;
	
	private int plaintextBytes;
	
	public MopeCipher(Cipher cipher) {
		this(cipher, DEFAULT_PLAINTEXT_BYTES);
	}
	
	public MopeCipher(Cipher cipher, int plaintextBytes) {
		this.cipher = cipher;
		
		this.plaintextBytes = plaintextBytes > 0 ? plaintextBytes : DEFAULT_PLAINTEXT_BYTES;
	}
	
	public Cipher getCipher() {
		return cipher;
	}
	
	public int getPlaintextBytes() {
		return plaintextBytes;
	}
	
	public void setPlaintextBytes(int plaintextBytes) {
		this.plaintextBytes = plaintextBytes;
	}
	
	public Key generateKey() throws OpeException {
		// Generate offset
		byte[] offsetBytes = new byte[plaintextBytes];
		rnd.nextBytes(offsetBytes);
		BigInteger offset = new BigInteger(offsetBytes);
		
		// Generate key
		Key key = cipher.generateKey();
		
		// Generate MOPE key
		return new MopeKey(key, plaintextBytes, offset);
	}

	public Key decodeKey(byte[] bytes) throws OpeException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		// Read byte count
		int plaintextBytes = buffer.getInt();
		
		// Read offset
		byte[] offsetBytes = new byte[plaintextBytes + 1];
		buffer.get(offsetBytes, 1, plaintextBytes);
		BigInteger offset = new BigInteger(offsetBytes);
		
		// Read key
		byte[] keyBytes = new byte[bytes.length - buffer.position()];
		buffer.get(keyBytes);
		Key key = cipher.decodeKey(keyBytes);
		
		// Done
		return new MopeKey(key, plaintextBytes, offset);
	}

}
