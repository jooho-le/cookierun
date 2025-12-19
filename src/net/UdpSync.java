package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// UDP를 이용한 양방향 상태 동기화 (직렬화 기반, 데모용)
public class UdpSync {
	private final NetConfig config;
	private DatagramSocket socket;
	private ExecutorService exec = Executors.newCachedThreadPool();
	private volatile boolean running = false;

	public UdpSync(NetConfig config) {
		this.config = config;
	}

	public void start(Consumer<GameState> onReceive) {
		try {
			socket = new DatagramSocket(config.localPort);
			running = true;
			exec.submit(() -> {
				byte[] buf = new byte[4096];
				while (running) {
					try {
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						socket.receive(packet);
						ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
						ObjectInputStream ois = new ObjectInputStream(bis);
						Object obj = ois.readObject();
						if (obj instanceof GameState) {
							onReceive.accept((GameState) obj);
						}
					} catch (Exception e) {
						if (running) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(GameState state) {
		if (socket == null || !running) return;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(state);
			oos.flush();
			byte[] data = bos.toByteArray();
			DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(config.remoteHost),
					config.remotePort);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;
		if (socket != null) {
			socket.close();
		}
		exec.shutdownNow();
	}
}
