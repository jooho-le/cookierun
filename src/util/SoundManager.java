package util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {

	private static float toDb(int volumePercent) {
		float vol = Math.max(1, Math.min(100, volumePercent));
		return (float) (20.0 * Math.log10(vol / 100.0));
	}

	public static void playEffect(String path, int volumePercent) {
		new Thread(() -> {
			try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path))) {
				Clip clip = AudioSystem.getClip();
				clip.open(ais);
				if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gain.setValue(toDb(volumePercent));
				}
				clip.start();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				DebugLog.add("Sound error: " + e.getMessage());
			}
		}).start();
	}

	public static class BgmPlayer {
		private Clip clip;

		public void playLoop(String path, int volumePercent) {
			stop();
			try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path))) {
				clip = AudioSystem.getClip();
				clip.open(ais);
				if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gain.setValue(toDb(volumePercent));
				}
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} catch (Exception e) {
				DebugLog.add("BGM error: " + e.getMessage());
			}
		}

		public void stop() {
			if (clip != null) {
				clip.stop();
				clip.close();
				clip = null;
			}
		}
	}
}
