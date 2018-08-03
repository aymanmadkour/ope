package ope.fast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import ope.Key;
import ope.OpeException;

/**
 * Key implementation of Hwang et al's Fast Order-Preserving Encryption scheme.
 * 
 * Reference:
 * 
 * Hwang, Y. H., Kim, S., & Seo, J. W. (2015, October).
 * Fast order-preserving encryption from uniform distribution sampling.
 * In Proceedings of the 2015 ACM Workshop on Cloud Computing Security Workshop (pp. 41-52). ACM.
 * https://dl.acm.org/citation.cfm?id=2808431
 *  
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public class FastOpeKey implements Key {

	private final long n;
	private final double alpha;
	private final double beta;
	private final double e;
	private final long k;
	
	private final byte[] kBytes;
	
	private final int plaintextBytesPerBlock;
	private final int ciphertextBytesPerBlock;
	
	private final int ciphertextBitsPerByte;
	private final long ciphertextBitMask;
	
	private final long[] fmin;
	private final long[] fmax;
	
	private final int[] bitMasks = new int[] {
			0x00,
			0x80,
			0x40,
			0x20,
			0x10,
			0x08,
			0x04,
			0x02,
			0x01
		};
	
	FastOpeKey(long n, double alpha, double e, long k) {
		this.n = n;
		this.alpha = alpha;
		this.beta = 1.0 - alpha;
		this.e = e;
		this.k = k;

		ByteBuffer kBuffer = ByteBuffer.allocate(Long.BYTES);
		kBuffer.putLong(k);
		this.kBytes = kBuffer.array();
		kBuffer = null;
		
		this.fmin = new long[9];
		this.fmax = new long[9];
		
		BigDecimal bigAlpha = new BigDecimal(this.alpha);
		BigDecimal bigBeta = new BigDecimal(this.beta);
		BigDecimal bigN = new BigDecimal(this.n);
		BigDecimal bigE = new BigDecimal(this.e);
		
		for (int i = 0; i < 9; i++) {
			BigDecimal factor = bigN.multiply(bigE.pow(i));
			fmin[i] = bigAlpha.multiply(factor).setScale(0, BigDecimal.ROUND_FLOOR).longValue();
			fmax[i] = bigBeta.multiply(factor).setScale(0, BigDecimal.ROUND_CEILING).longValue();
		}
		
		int cipherBits = 0;
		for (long b = n; b > 0; b >>= 1) { cipherBits++; }
		this.ciphertextBitsPerByte = cipherBits;
		
		long ciphertextBitMask = 1;
		for (int i = 1; i < ciphertextBitsPerByte; i++) {
			ciphertextBitMask <<= 1;
			ciphertextBitMask |= 1;
		}
		this.ciphertextBitMask = ciphertextBitMask;
		
		int plaintextBytesPerBlock = 0;
		
		for (int i = 1; i <= 8; i++) {
			if ((cipherBits * i) % 8 == 0) {
				plaintextBytesPerBlock = i;
				break;
			}
		}
		this.plaintextBytesPerBlock = plaintextBytesPerBlock;
		this.ciphertextBytesPerBlock = plaintextBytesPerBlock
				* cipherBits / 8;
	}

	public byte[] encodeKey() throws OpeException {
		ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES
				+ 2 * Double.BYTES);
		
		buffer.putLong(n);
		buffer.putDouble(alpha);
		buffer.putDouble(e);
		buffer.putLong(k);
		
		return buffer.array();
	}

	public byte[] encrypt(byte[] plaintext) throws OpeException {
		// Calculate ciphertext size
		// Add one extra byte for padding info
		int blockCount = (plaintext.length
				+ plaintextBytesPerBlock - 1)
				/ plaintextBytesPerBlock;
		
		int ciphertextSize = blockCount
				* ciphertextBytesPerBlock
				+ 1;
		
		int padding = blockCount
				* plaintextBytesPerBlock
				- plaintext.length;
		
		// Allocate buffer
		ByteBuffer plaintextBuffer = ByteBuffer.wrap(plaintext);
		ByteBuffer ciphertextBuffer = ByteBuffer.allocate(ciphertextSize);
		
		// Encrypt blocks
		for (int block = 0; block < blockCount; block++) {
			BigInteger blockCipher = BigInteger.valueOf(0);
			
			for (int i = 0; i < plaintextBytesPerBlock; i++) {
				long cipher = 0;
				if (plaintextBuffer.position() < plaintext.length) {
					int b = Byte.toUnsignedInt(plaintextBuffer.get());
					cipher = f(0, 0);
					for (int j = 1; j <= 8; j++) { cipher += ((b & bitMasks[j]) == 0 ? -1 : 1) * f(j, b); }
				}
				blockCipher = blockCipher.shiftLeft(ciphertextBitsPerByte).or(BigInteger.valueOf(cipher));
			}
			
			byte[] blockCipherBytes = blockCipher.toByteArray();
			
			if (blockCipherBytes.length < ciphertextBytesPerBlock) {
				int diff = ciphertextBytesPerBlock - blockCipherBytes.length;
				for (int i = 0; i < diff; i++) { ciphertextBuffer.put((byte) 0); }

				ciphertextBuffer.put(blockCipherBytes);
				
			} else {
				ciphertextBuffer.put(blockCipherBytes, blockCipherBytes.length - ciphertextBytesPerBlock, ciphertextBytesPerBlock);
			}
			
		}
		
		// Add padding info
		ciphertextBuffer.put((byte) padding);
		
		// Done
		return ciphertextBuffer.array();
	}

	public byte[] decrypt(byte[] ciphertext) throws OpeException {
		// Calculate plaintext size
		int blockCount = (ciphertext.length - 1) / ciphertextBytesPerBlock;
		int plaintextSize = blockCount * plaintextBytesPerBlock - ciphertext[ciphertext.length - 1];
		
		// Allocate buffers
		ByteBuffer plaintextBuffer = ByteBuffer.allocate(plaintextSize);
		ByteBuffer ciphertextBuffer = ByteBuffer.wrap(ciphertext);
		
		// Decrypt blocks
		for (int block = 0; block < blockCount; block++) {
			byte[] blockBytes = new byte[ciphertextBytesPerBlock];
			ciphertextBuffer.get(blockBytes);
			BigInteger blockCipher = new BigInteger(blockBytes);

			int plaintextOffset = block * plaintextBytesPerBlock;
			
			for (int i = plaintextBytesPerBlock - 1; i >= 0; i--) {
				long cipher = blockCipher.and(BigInteger.valueOf(ciphertextBitMask)).longValue();
				blockCipher = blockCipher.shiftRight(ciphertextBitsPerByte);
				
				int b = 0;
				long a = f(0, 0);
				if (cipher >= a) { b |= bitMasks[1]; }
				
				for (int j = 1; j < 8; j++) {
					long aj = f(j, b);
					a += ((b & bitMasks[j]) == 0 ? -1 : 1) * aj;
					if (cipher >= a) { b |= bitMasks[j + 1]; }
				}
					
				if (plaintextOffset + i < plaintextSize) {
					plaintextBuffer.position(plaintextOffset + i);
					plaintextBuffer.put((byte) b);
				}
			}
		}
		
		// Done
		return plaintextBuffer.array();
	}

	private long f(int i, int x) {
		try {
			// Include only i most significant bits
			int shift = 8 - i;
			x >>= shift;
			x <<= shift;
			
			// Calculate hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(kBytes);
			md.update((byte) x);
			byte[] hash = md.digest();
			
			// Convert to big integer
			BigInteger bi = new BigInteger(hash);
			
			// Calculate function value
			return bi.mod(BigInteger.valueOf(fmax[i] - fmin[i])).add(BigInteger.valueOf(fmin[i])).longValue();
			
		} catch (Exception e) {
			return 0;
		}
	}
}
