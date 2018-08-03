package ope.mope;

import ope.OpeTest;
import ope.Cipher;
import ope.fast.FastOpeCipher;

/**
 * @author Ayman Madkour
 */
public class MopeTest extends OpeTest {

	@Override
	protected Cipher createCipher() {
		return new MopeCipher(new FastOpeCipher(), 2);
	}
}
