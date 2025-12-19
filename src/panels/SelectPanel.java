package panels;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import ingame.CharacterStats;
import ingame.CookieImg;
import progress.SaveData;
import progress.SaveManager;
import main.Main;
import config.Settings;

public class SelectPanel extends JPanel {

	private Main mainRef;
	
	// 선택할 캐릭터 이미지 아이콘
	private ImageIcon ch01 = new ImageIcon("img/select/selectCh1.png");
	private ImageIcon ch02 = new ImageIcon("img/select/selectCh2.png");
	private ImageIcon ch03 = new ImageIcon("img/select/selectCh3.png");
	private ImageIcon ch04 = new ImageIcon("img/select/selectCh4.png");

	// 선택된 캐릭터 이미지 아이콘
	private ImageIcon ch011 = new ImageIcon("img/select/selectedCh1.png");
	private ImageIcon ch022 = new ImageIcon("img/select/selectedCh2.png");
	private ImageIcon ch033 = new ImageIcon("img/select/selectedCh3.png");
	private ImageIcon ch044 = new ImageIcon("img/select/selectedCh4.png");

	// 시작 버튼 이미지아이콘
	private ImageIcon start = new ImageIcon("img/select/GameStartBtn.png");
	
	// 이미지를 선택할 버튼
	private JButton ch1;
	private JButton ch2;
	private JButton ch3;
	private JButton ch4;

	// 시작 버튼
	private JButton StartBtn;
	
	// 게임에서 사용할 쿠키 이미지들을 담을 오브젝트
	private CookieImg ci;
	private CharacterStats characterStats;
	private JLabel statsLabel;
private JLabel profileLabel;
private JLabel missionLabel;
private JLabel achievementLabel;
private JLabel leaderboardLabel;
	private SaveData saveData;
	private JTextArea infoArea;
	private Settings settings;

	// 쿠키 이미지를 메인에서 gamePanel로 보내기 위한 게터
	public CookieImg getCi() {
		return ci;
	}

	public CharacterStats getCharacterStats() {
		return characterStats;
	}

	// 캐릭터별 기본 능력치 세팅 (점프는 캐릭터 순서대로 점차 상승)
	private CharacterStats statsForId(String id) {
		int speed = 6; // 기본 속도
		int health = 1000; // 기본 체력
		int jump = 14; // 기본 점프

		if ("ch1".equals(id)) { // 가장 낮은 점프 = 가장 빠른 속도
			jump = 14;
			speed = 8;
		} else if ("ch2".equals(id)) {
			jump = 16;
			speed = 7;
		} else if ("ch3".equals(id)) {
			jump = 18;
			speed = 6;
		} else if ("ch4".equals(id)) {
			jump = 20;
			speed = 5;
		}

		return new CharacterStats(id, speed, jump, health);
	}

	private void updateStatsLabel(String name, CharacterStats stats) {
		if (statsLabel == null) {
			return;
		}
		statsLabel.setText(String.format("%s | Speed %d / Jump %d / HP %d", name, stats.getBaseSpeed(),
				stats.getBaseJump(), stats.getBaseHealth()));
		showStatPopup(name, stats);
	}

	private void updateProfileLabel() {
		if (profileLabel == null || saveData == null) {
			return;
		}
		profileLabel.setText(String.format("Coins: %d | Speed Lv.%d Jump Lv.%d HP Lv.%d", saveData.getProfile().getCoins(),
				saveData.getProfile().getSpeedLevel(), saveData.getProfile().getJumpLevel(),
				saveData.getProfile().getHealthLevel()));
	}

	private void refreshInfoArea() {
		if (infoArea == null || saveData == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[Missions]\n");
		saveData.getMissions().forEach(m -> {
			sb.append(String.format("- %s: %d/%d %s\n", m.getDescription(), m.getProgress(), m.getTarget(),
					m.isCompleted() ? "(완료)" : ""));
		});
		sb.append("[Achievements]\n");
		saveData.getAchievements().forEach(a -> {
			sb.append(String.format("- %s: %d/%d %s\n", a.getTitle(), a.getProgress(), a.getTarget(),
					a.isAchieved() ? "(달성)" : ""));
		});
		sb.append("[Leaderboard]\n");
		saveData.getLeaderboard().stream().limit(5).forEach(entry -> {
			sb.append(String.format("- %s : %d\n", entry.getPlayerName(), entry.getScore()));
		});
		infoArea.setText(sb.toString());
	}

	private void switchSlot(String slotName) {
		SaveData loaded = SaveManager.load(slotName, saveData.getProfile().getName());
		this.saveData = loaded;
		mainRef.setSaveData(loaded);
		updateProfileLabel();
		refreshInfoArea();
	}

	private void showStatPopup(String name, CharacterStats stats) {
		String msg = String.format("%s\nSpeed: %d\nJump: %d\nHP: %d", name, stats.getBaseSpeed(), stats.getBaseJump(),
				stats.getBaseHealth());
		javax.swing.JOptionPane.showMessageDialog(this, msg, "선택한 캐릭터 능력치", javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	public SelectPanel(Object o, SaveData saveData, Settings settings) {
		this.saveData = saveData;
		this.mainRef = (Main) o;
		this.settings = settings;

		// 시작 버튼
		StartBtn = new JButton(start);
		StartBtn.setName("StartBtn");
		StartBtn.addMouseListener((MouseListener) o);
		StartBtn.setBounds(254, 334, 291, 81);
		add(StartBtn);
		StartBtn.setBorderPainted(false);
		StartBtn.setContentAreaFilled(false);
		StartBtn.setFocusPainted(false);

		// 캐릭터 ch1
		ch1 = new JButton(ch01);
		ch1.setName("ch1");
		ch1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ch1.setIcon(ch011);
				ch2.setIcon(ch02);
				ch3.setIcon(ch03);
				ch4.setIcon(ch04);
				ci = new CookieImg(new ImageIcon("img/cookieimg/cookie1/player_origin.gif"),
						new ImageIcon("img/cookieimg/cookie1/player_up.gif"),
						new ImageIcon("img/cookieimg/cookie1/player_doubleup.gif"),
						new ImageIcon("img/cookieimg/cookie1/player_jumpend.png"),
						new ImageIcon("img/cookieimg/cookie1/player_down.gif"),
						new ImageIcon("img/cookieimg/cookie1/player_attack.png"));
				characterStats = statsForId("ch1");
				updateStatsLabel("쿠키1", characterStats);
			}
		});
		ch1.setBounds(90, 102, 150, 200);
		add(ch1);
		ch1.setBorderPainted(false);
		ch1.setContentAreaFilled(false);
		ch1.setFocusPainted(false);

		// 캐릭터 ch2
		ch2 = new JButton(ch02);
		ch2.setName("ch2");
		ch2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ch1.setIcon(ch01);
				ch2.setIcon(ch022);
				ch3.setIcon(ch03);
				ch4.setIcon(ch04);
				ci = new CookieImg(new ImageIcon("img/cookieimg/cookie2/normal.gif"),
						new ImageIcon("img/cookieimg/cookie2/jump.gif"),
						new ImageIcon("img/cookieimg/cookie2/doublejump.gif"),
						new ImageIcon("img/cookieimg/cookie2/fall.png"),
						new ImageIcon("img/cookieimg/cookie2/slide.gif"),
						new ImageIcon("img/cookieimg/cookie2/hit.gif"));
				characterStats = statsForId("ch2");
				updateStatsLabel("쿠키2", characterStats);
			}
		});
		ch2.setBounds(238, 102, 150, 200);
		add(ch2);
		ch2.setBorderPainted(false);
		ch2.setContentAreaFilled(false);
		ch2.setFocusPainted(false);

		// 캐릭터 ch3
		ch3 = new JButton(ch03);
		ch3.setName("ch3");
		ch3.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ch1.setIcon(ch01);
				ch2.setIcon(ch02);
				ch3.setIcon(ch033);
				ch4.setIcon(ch04);
				ci = new CookieImg(new ImageIcon("img/cookieimg/cookie3/cookie.gif"),
						new ImageIcon("img/cookieimg/cookie3/jump.png"),
						new ImageIcon("img/cookieimg/cookie3/doublejump.gif"),
						new ImageIcon("img/cookieimg/cookie3/fall.png"),
						new ImageIcon("img/cookieimg/cookie3/slide.gif"),
						new ImageIcon("img/cookieimg/cookie3/hit.png"));
				characterStats = statsForId("ch3");
				updateStatsLabel("쿠키3", characterStats);
			}
		});
		ch3.setBounds(386, 102, 150, 200);
		add(ch3);
		ch3.setBorderPainted(false);
		ch3.setContentAreaFilled(false);
		ch3.setFocusPainted(false);

		// 캐릭터 ch4
		ch4 = new JButton(ch04);
		ch4.setName("ch4");
		ch4.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ch1.setIcon(ch01);
				ch2.setIcon(ch02);
				ch3.setIcon(ch03);
				ch4.setIcon(ch044);
				ci = new CookieImg(new ImageIcon("img/cookieimg/cookie4/kch.gif"),
						new ImageIcon("img/cookieimg/cookie4/kjump.gif"),
						new ImageIcon("img/cookieimg/cookie4/kjump.gif"),
						new ImageIcon("img/cookieimg/cookie4/kjump.gif"),
						new ImageIcon("img/cookieimg/cookie4/kslide.gif"),
						new ImageIcon("img/cookieimg/cookie4/kch.gif"));
				characterStats = statsForId("ch4");
				updateStatsLabel("쿠키4", characterStats);
			}
		});
		ch4.setBounds(534, 102, 150, 200);
		add(ch4);
		ch4.setBorderPainted(false);
		ch4.setContentAreaFilled(false);
		ch4.setFocusPainted(false);

		// 배경
		JLabel selectBg = new JLabel("");
		selectBg.setForeground(Color.ORANGE);
		selectBg.setHorizontalAlignment(SwingConstants.CENTER);
		selectBg.setIcon(new ImageIcon("img/select/selectBg.png"));
		selectBg.setBounds(0, 0, 784, 461);
		add(selectBg);

		// 캐릭터 선택 타이틀
		JLabel selectTxt = new JLabel("");
		selectTxt.setHorizontalAlignment(SwingConstants.CENTER);
		selectTxt.setIcon(new ImageIcon("img/select/selectTxt.png"));
		selectTxt.setBounds(174, 20, 397, 112);
		add(selectTxt);

		// 능력치 표시 라벨 (배경 위로 오도록 마지막에 추가)
		statsLabel = new JLabel("캐릭터를 선택하세요");
		statsLabel.setForeground(Color.WHITE);
		statsLabel.setBounds(150, 290, 500, 20);
		add(statsLabel);

		// 프로필 정보 라벨
		profileLabel = new JLabel("");
		profileLabel.setForeground(Color.WHITE);
		profileLabel.setBounds(150, 310, 500, 20);
		add(profileLabel);
		updateProfileLabel();

		// 정보 영역 (미션/업적/리더보드)
		infoArea = new JTextArea();
		infoArea.setEditable(false);
		infoArea.setOpaque(false);
		infoArea.setForeground(Color.WHITE);
		infoArea.setBounds(20, 340, 300, 120);
		add(infoArea);
		refreshInfoArea();

		// 업그레이드 버튼
		JButton upSpeed = new JButton("Speed Up");
		upSpeed.setBounds(620, 110, 120, 30);
		upSpeed.addActionListener(e -> {
			if (saveData.getProfile().upgradeSpeed()) {
				updateProfileLabel();
				refreshInfoArea();
				SaveManager.save("slot1", saveData);
			}
		});
		add(upSpeed);

		JButton upJump = new JButton("Jump Up");
		upJump.setBounds(620, 150, 120, 30);
		upJump.addActionListener(e -> {
			if (saveData.getProfile().upgradeJump()) {
				updateProfileLabel();
				refreshInfoArea();
				SaveManager.save("slot1", saveData);
			}
		});
		add(upJump);

		JButton upHp = new JButton("HP Up");
		upHp.setBounds(620, 190, 120, 30);
		upHp.addActionListener(e -> {
			if (saveData.getProfile().upgradeHealth()) {
				updateProfileLabel();
				refreshInfoArea();
				SaveManager.save("slot1", saveData);
			}
		});
		add(upHp);

		// 슬롯 전환 버튼
		JButton slot1 = new JButton("Slot1");
		slot1.setBounds(620, 230, 70, 28);
		slot1.addActionListener(e -> switchSlot("slot1"));
		add(slot1);

		JButton slot2 = new JButton("Slot2");
		slot2.setBounds(695, 230, 70, 28);
		slot2.addActionListener(e -> switchSlot("slot2"));
		add(slot2);

		JButton slot3 = new JButton("Slot3");
		slot3.setBounds(770, 230, 70, 28);
		slot3.addActionListener(e -> switchSlot("slot3"));
		add(slot3);

	}
}
