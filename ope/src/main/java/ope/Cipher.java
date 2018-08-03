package ope;

/**
 * General interface for OPE ciphers.
 * Provides methods for generating keys and decoding keys from binary format.
 * 
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public interface Cipher {
	public Key generateKey() throws OpeException;
	public Key decodeKey(byte[] bytes) throws OpeException;
}
