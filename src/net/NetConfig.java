package net;

// 네트워크 설정 (UDP 피어)
public class NetConfig {
	public String remoteHost = "127.0.0.1";
	public int remotePort = 6001;
	public int localPort = 6000;

	public NetConfig() {
		String hostProp = System.getProperty("net.remoteHost");
		String rPortProp = System.getProperty("net.remotePort");
		String lPortProp = System.getProperty("net.localPort");

		if (hostProp != null && !hostProp.isEmpty()) {
			remoteHost = hostProp;
		}
		if (rPortProp != null) {
			try {
				remotePort = Integer.parseInt(rPortProp);
			} catch (NumberFormatException ignored) {
			}
		}
		if (lPortProp != null) {
			try {
				localPort = Integer.parseInt(lPortProp);
			} catch (NumberFormatException ignored) {
			}
		}
	}
}
