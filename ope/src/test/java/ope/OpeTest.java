package ope;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ayman Madkour
 */
public abstract class OpeTest {
	
	protected short[] plaintexts;
	protected byte[][] ciphertexts;
	
	protected Cipher cipher;
	protected Key key;
	
	protected abstract Cipher createCipher();
	
	@Before
	public void setUp() {
		// Initialize cipher
		cipher = createCipher();
		key = cipher.generateKey();
		
		// Initialize plaintexts
		this.plaintexts = new short[] {
				-30000,
				-20000,
				-10000,
				-5000,
				-2000,
				-1500,
				-1000,
				-500,
				-100,
				-10,
				-5,
				-1,
				0,
				1,
				5,
				10,
				100,
				500,
				1000,
				1500,
				2000,
				5000,
				10000,
				20000,
				30000
		};
		
		// Encrypt data
		ciphertexts = new byte[plaintexts.length][];
		for (int i = 0; i < plaintexts.length; i++) {
			ciphertexts[i] = key.encryptShort(plaintexts[i]);
		}

		// Print data set
		System.out.println("Data set:");
		System.out.println();
		for (int i = 0; i < plaintexts.length; i++) {
			System.out.println(String.format("%6d => %s", plaintexts[i], toString(ciphertexts[i])));
		}
	}
	
	@Test
	public void testRangeQuery() {
		// Prepare range query
		short minPlaintext = -100;
		short maxPlaintext = 1000;

		System.out.println();
		System.out.println("================================================================================");
		System.out.println("Searching for range:");
		System.out.println();
		System.out.println("Min Plaintext = " + minPlaintext);
		System.out.println("Max Plaintext = " + maxPlaintext);
		
		// Encrypt range
		byte[] minCiphertext = key.encryptShort(minPlaintext);
		byte[] maxCiphertext = key.encryptShort(maxPlaintext);
		
		System.out.println("Min Ciphertext = " + toString(minCiphertext));
		System.out.println("Max Ciphertext = " + toString(maxCiphertext));
		
		// Search in ciphertexts
		List<byte[]> results = new ArrayList<>();
		for (int i = 0; i < ciphertexts.length; i++) {
			if (inRange(ciphertexts[i], minCiphertext, maxCiphertext)) {
				results.add(ciphertexts[i]);
			}
		}
		
		// Print encrypted results
		System.out.println();
		System.out.println("================================================================================");
		System.out.println("Ciphertext Results:");
		System.out.println();
		for (byte[] result : results) {
			System.out.println(toString(result));
		}
		
		// Validate result set size
		Assert.assertEquals(11, results.size());
		
		// Print plaintext results
		System.out.println();
		System.out.println("================================================================================");
		System.out.println("Plaintext Results:");
		System.out.println();
		for (byte[] result : results) {
			System.out.println(String.format("%6d", key.decryptShort(result)));
		}
		
		// Validate results
		for (byte[] result : results) {
			short value = key.decryptShort(result);
			Assert.assertTrue(value >= minPlaintext && value <= maxPlaintext);
		}
	}

	protected static String toString(byte[] bytes) {
		StringBuilder s = new StringBuilder();
		for (byte b : bytes) { s.append(String.format("%02x", Byte.toUnsignedInt(b))); }
		return s.toString();
	}
	
	protected static int compare(byte[] c1, byte[] c2) {
		for (int i = 0; i < c1.length; i++) {
			int b1 = Byte.toUnsignedInt(c1[i]);
			int b2 = Byte.toUnsignedInt(c2[i]);
			if (b1 < b2) { return -1; }
			else if (b1 > b2) { return 1; }
		}
		return 0;
	}
	
	protected static boolean inRange(byte[] value, byte[] min, byte[] max) {
		return compare(value, min) >= 0 && compare(value, max) <= 0;
	}
}
