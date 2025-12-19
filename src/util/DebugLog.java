package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class DebugLog {
	private static final Deque<String> logs = new ArrayDeque<>();
	private static final int MAX = 50;
	private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static synchronized void add(String msg) {
		String line = String.format("[%s] %s", LocalDateTime.now().format(fmt), msg);
		logs.addLast(line);
		if (logs.size() > MAX) {
			logs.removeFirst();
		}
		System.out.println(line);
	}

	public static synchronized List<String> snapshot() {
		return logs.stream().collect(Collectors.toList());
	}
}
