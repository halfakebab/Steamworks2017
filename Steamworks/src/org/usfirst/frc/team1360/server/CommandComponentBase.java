package org.usfirst.frc.team1360.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;

import org.usfirst.frc.team1360.server.util.IOUtils;

public abstract class CommandComponentBase extends Thread implements Component {
	private InputStream i;
	private OutputStream o;

	public CommandComponentBase(InputStream i, OutputStream o) {
		this.i = i;
		this.o = o;
	}

	@Override
	public final void run() {
		try {
			byte[] header = new byte[6];
			while (true) {
				i.read(header);
				int id = IOUtils.UInt16Big(header, 0);
				int len = IOUtils.Int32Big(header, 2);
				byte[] data = new byte[len];
				i.read(data);
				new HandlerThread(id, data).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected synchronized final void sendCommand(int id, Object... data) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			for (Object o : data) {
				byte[] r = serialize(o);
				if (r == null)
					throw new InvalidObjectException(
							String.format("Cannot serialize object of type '%s'", o.getClass().getSimpleName()));
				os.write(r);
			}
			o.write(IOUtils.UInt16Big(id));
			o.write(IOUtils.Int32Big(os.size()));
			o.write(os.toByteArray());
		}
	}

	protected byte[] serialize(Object data) {
		if (data instanceof byte[])
			return (byte[]) data;
		if (data instanceof Integer)
			return IOUtils.Int32Big((int) data);
		return null;
	}

	protected abstract void onCommand(int id, byte[] data) throws Exception;

	private class HandlerThread extends Thread {
		int id;
		byte[] data;

		public HandlerThread(int id, byte[] data) {
			this.id = id;
			this.data = data;
		}

		@Override
		public void run() {
			try {
			onCommand(id, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
