package ope.fast;

import ope.OpeTest;
import ope.Cipher;

/**
 * @author Ayman Madkour
 */
public class FastOpeTest extends OpeTest {
	@Override
	protected Cipher createCipher() {
		return new FastOpeCipher();
	}
}
