package ope.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ope.OpeException;

/**
 * This class is used for encoding Java primitive types and strings using
 * special OPE-friendly formats.
 * 
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public class Encoder {

	private Encoder() {}
	
	public static byte[] encodeBoolean(boolean value) throws OpeException {
		return new byte[] { (byte) (value ? 1 : 0) };
	}
	
	public static boolean decodeBoolean(byte[] value) throws OpeException {
		checkLength(value, 1);
		return (value[0] != 0);
	}
	
	public static byte[] encodeByte(byte value) throws OpeException {
		BigInteger v = BigInteger.valueOf(value).subtract(BigInteger.valueOf(Byte.MIN_VALUE));
		
		return new byte[] { v.byteValue() };
	}
	
	public static byte decodeByte(byte[] value) throws OpeException {
		checkLength(value, 1);
		return new BigInteger(value).add(BigInteger.valueOf(Byte.MIN_VALUE)).byteValue();
	}
	
	public static byte[] encodeShort(short value) throws OpeException {
		BigInteger v = BigInteger.valueOf(value).subtract(BigInteger.valueOf(Short.MIN_VALUE));
		
		return new byte[] {
				v.shiftRight(8).byteValue(),
				v.byteValue()
		};
	}
	
	public static short decodeShort(byte[] value) throws OpeException {
		checkLength(value, 2);
		return new BigInteger(value).add(BigInteger.valueOf(Short.MIN_VALUE)).shortValue();
	}
	
	public static byte[] encodeInt(int value) throws OpeException {
		BigInteger v = BigInteger.valueOf(value).subtract(BigInteger.valueOf(Integer.MIN_VALUE));
		
		return new byte[] {
				v.shiftRight(24).byteValue(),
				v.shiftRight(16).byteValue(),
				v.shiftRight(8).byteValue(),
				v.byteValue()
		};
	}
	
	public static int decodeInt(byte[] value) throws OpeException {
		checkLength(value, 4);
		return new BigInteger(value).add(BigInteger.valueOf(Integer.MIN_VALUE)).intValue();
	}
	
	public static byte[] encodeLong(long value) throws OpeException {
		BigInteger v = BigInteger.valueOf(value).subtract(BigInteger.valueOf(Long.MIN_VALUE));
		
		return new byte[] {
				v.shiftRight(56).byteValue(),
				v.shiftRight(48).byteValue(),
				v.shiftRight(40).byteValue(),
				v.shiftRight(32).byteValue(),
				v.shiftRight(24).byteValue(),
				v.shiftRight(16).byteValue(),
				v.shiftRight(8).byteValue(),
				v.byteValue()
		};
	}
	
	public static long decodeLong(byte[] value) throws OpeException {
		checkLength(value, 8);
		return new BigInteger(value).add(BigInteger.valueOf(Long.MIN_VALUE)).longValue();
	}
	
	public static byte[] encodeFloat(float value) throws OpeException {
		int floatInt = Float.floatToIntBits(value);
		if (floatInt <0) {
			floatInt = -floatInt - Integer.MAX_VALUE;
		}
		return encodeInt(floatInt);
	}
	
	public static float decodeFloat(byte[] value) throws OpeException {
		checkLength(value, 4);

		int floatInt = decodeInt(value);
		if (floatInt < 0) {
			floatInt = -floatInt + Integer.MAX_VALUE;
		}
		return Float.intBitsToFloat(floatInt);
	}
	
	public static byte[] encodeDouble(double value) throws OpeException {
		long doubleLong = Double.doubleToLongBits(value);
		if (doubleLong < 0) {
			doubleLong = -doubleLong - Long.MAX_VALUE;
		}
		return encodeLong(doubleLong);
	}
	
	public static double decodeDouble(byte[] value) throws OpeException {
		checkLength(value, 8);
		long doubleLong = decodeLong(value);
		if (doubleLong < 0) {
			doubleLong = -doubleLong + Long.MAX_VALUE;
		}
		return Double.longBitsToDouble(doubleLong);
	}
	
	public static byte[] encodeChar(char value) throws OpeException {
		return new byte[] {
				(byte) (value >> 8),
				(byte) (value & 0xff)
		};
	}
	
	public static char decodeChar(byte[] value) throws OpeException {
		checkLength(value, 2);
		return (char) ((value[0] << 8) | value[1]);
	}
	
	public static byte[] encodeString(String value) throws OpeException {
		if (value == null) { return null; }
		else { return value.getBytes(StandardCharsets.UTF_8); }
	}
	
	public static String decodeString(byte[] value) throws OpeException {
		if (value == null) { return null; }
		else { return new String(value, StandardCharsets.UTF_8); }
	}
	
	private static void checkNull(byte[] value) throws OpeException {
		if (value == null) {
			throw new OpeException("Input value is null.");
		}
	}
	
	private static void checkLength(byte[] value, int expectedLength) throws OpeException {
		checkNull(value);
		if (value.length != expectedLength) {
			throw new OpeException("Invalid byte array length. Expecting " + expectedLength + ", found " + value.length + ".");
		}
	}
}
