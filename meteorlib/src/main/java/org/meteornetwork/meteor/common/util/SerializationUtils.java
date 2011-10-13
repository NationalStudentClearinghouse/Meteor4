package org.meteornetwork.meteor.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils {

	private SerializationUtils() {
	}

	public static byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

		ObjectOutputStream objOutStream = new ObjectOutputStream(byteArrayOutStream);
		objOutStream = new ObjectOutputStream(byteArrayOutStream);
		objOutStream.writeObject(o);
		objOutStream.close();

		byte[] serialized = byteArrayOutStream.toByteArray();

		return serialized;
	}

	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteArrayInStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objInStream = new ObjectInputStream(byteArrayInStream);

		try {
			return objInStream.readObject();
		} finally {
			objInStream.close();
		}
	}
}
