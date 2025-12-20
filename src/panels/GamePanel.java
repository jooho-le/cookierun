package panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import engine.Body;
import engine.CollisionLayer;
import engine.PhysicsUtil;
import engine.PlayerState;
import engine.GameState;
import ingame.Back;
import ingame.BuffItem;
import ingame.BuffType;
import ingame.Cookie;
import ingame.CharacterStats;
import ingame.CookieImg;
import ingame.Enemy;
import ingame.EnemyType;
import ingame.Field;
import ingame.Jelly;
import ingame.MapObjectImg;
import ingame.PlatformType;
import ingame.Projectile;
import ingame.SpecialPlatform;
import ingame.Tacle;
import main.Main;
import config.Settings;
import config.GameConfig;
import util.Util;
import util.DebugLog;
import util.SoundManager;
import net.NetConfig;
import net.UdpSync;

public class GamePanel extends JPanel {

	// 쿠키 이미지 아이콘들
	private ImageIcon cookieIc; // 기본모션
	private ImageIcon jumpIc; // 점프모션
	private ImageIcon doubleJumpIc; // 더블점프모션
	private ImageIcon fallIc; // 낙하모션(더블 점프 후)
	private ImageIcon slideIc; // 슬라이드 모션
	private ImageIcon hitIc; // 부딛히는 모션

	// 배경 이미지
	private ImageIcon backIc; // 제일 뒷 배경
	private ImageIcon secondBackIc; // 2번째 배경

	private ImageIcon backIc2;
	private ImageIcon secondBackIc2;

	private ImageIcon backIc3;
	private ImageIcon secondBackIc3;

	private ImageIcon backIc4;
	private ImageIcon secondBackIc4;

	// 젤리 이미지 아이콘들
	private ImageIcon jelly1Ic;
	private ImageIcon jelly2Ic;
	private ImageIcon jelly3Ic;
	private ImageIcon jellyHPIc;

	private ImageIcon jellyEffectIc;

	// 발판 이미지 아이콘들
	private ImageIcon field1Ic; // 발판
	private ImageIcon field2Ic; // 공중발판

	// 장애물 이미지 아이콘들
	private ImageIcon tacle10Ic; // 1칸 장애물
	private ImageIcon tacle20Ic; // 2칸 장애물
	private ImageIcon tacle30Ic; // 3칸 장애물
	private ImageIcon tacle40Ic; // 4칸 장애물

	// 체력 게이지
	private ImageIcon lifeBar;

	private ImageIcon redBg; // 피격시 붉은 화면

	private ImageIcon jumpButtonIconUp;
	private ImageIcon jumpButtonIconDown;

	private ImageIcon slideIconUp;
	private ImageIcon slideIconDown;

	Image jumpBtn;
	Image slideBtn;

	// 리스트 생성
	private List<Jelly> jellyList; // 젤리 리스트

	private List<Field> fieldList; // 발판 리스트

	private List<Tacle> tacleList; // 장애물 리스트

	private List<SpecialPlatform> specialPlatforms; // 특수 발판

	private List<Enemy> enemyList; // 적 리스트

	private List<Projectile> projectileList; // 투사체 리스트

	private List<BuffItem> buffItemList; // 버프/디버프 아이템 리스트

	private List<Integer> mapLengthList;

	private int mapLength = 0;

	private int runPage = 0; // 한 화면 이동할때마다 체력을 깎기 위한 변수

	private int runStage = 1; // 스테이지를 확인하는 변수이다. (미구현)

	private int resultScore = 0; // 결과점수를 수집하는 변수

	private int gameSpeed = 5; // 게임 속도
	private int baseGameSpeed = 5; // 기본 속도
	private int speedBonus = 3; // 속도 부스트 가중치
	private int slowPenalty = 2; // 슬로우 패널티
	private int magnetRadius = 200; // 자석 범위
	private int jumpPowerBase = 8; // 점프 기본 계수
	private int maxHealth = 1000; // 최대 체력
	private double fallVelocity = 0;
	private double jumpVelocity = 0;
	private double healthTickAcc = 0; // 시간 경과 체력 감소 누적
	private boolean debugOverlay = false;
	private boolean godMode = false;
	private long debugFrameCount = 0;
	private long debugLastFpsTime = 0;
	private int debugFps = 0;
	private boolean logOverlay = false;
	private String lastPickupText = "";
	private long lastPickupTime = 0;
	private static final long PICKUP_DISPLAY_MS = 1000;
	// 배경음 제거
	// private SoundManager.BgmPlayer bgmPlayer = new SoundManager.BgmPlayer();

	private int nowField = 2000; // 발판의 높이를 저장.

	private JButton escButton; // esc 버튼 (테스트 중)

	private boolean fadeOn = false;

	private boolean escKeyOn = false; // 일시정지를 위한 esc키 확인

	private boolean downKeyOn = false; // 다운키 눌렀는지 여부

	private boolean redScreen = false; // 피격시 반짝 붉은 화면 여부

	// 버프/디버프 상태
	private EnumMap<BuffType, Long> buffEndTime = new EnumMap<>(BuffType.class);
	private EnumMap<BuffType, Long> buffDuration = new EnumMap<>(BuffType.class);
	private boolean shieldActive = false;
	private boolean speedActive = false;
	private boolean doubleScoreActive = false;
	private boolean magnetActive = false;
	private boolean giantActive = false;
	private boolean slowActive = false;

	// 네트워크 동기화
	private boolean netEnabled = true; // 2인 네트워크 기본 활성화
	private NetConfig netConfig = new NetConfig();
	private UdpSync udpSync;
	private net.GameState remoteState;
	private long lastRemoteTs = 0;
	private int sendSeq = 0;
	private int remoteSeq = -1;
	private double remoteLerpX = 0;
	private double remoteLerpY = 0;
	private double remoteLastDrawX = 0;
	private double remoteLastDrawY = 0;

	private final double fixedStep = 0.016;
	private double accumulator = 0;
	private GameConfig gameConfig = GameConfig.load();

	private GameState gameState = GameState.LOADING;
	private java.util.Stack<String> menuStack = new java.util.Stack<>();

	private Timer gameTimer; // 단일 게임 루프 타이머
	private long lastTick = 0;
	private PlayerState playerState = PlayerState.IDLE;

	int face; // 쿠키의 정면
	int foot; // 쿠키의 발

	// 진행/통계
	private int totalJelly = 0;
	private int totalJump = 0;
	private int totalDistance = 0;

	private progress.SaveData saveData;
	private Settings settings;

	// 이미지 파일로 된 맵을 가져온다.
	private int[] sizeArr; // 이미지의 넓이와 높이를 가져오는 1차원 배열
	private int[][] colorArr; // 이미지의 x y 좌표의 픽셀 색값을 저장하는 2차원배열

	private Image buffImage; // 더블버퍼 이미지
	private Graphics buffg; // 더블버퍼 g

	private AlphaComposite alphaComposite; // 투명도 관련 오브젝트

	Cookie c1; // 쿠키 오브젝트

	Back b11; // 배경1-1 오브젝트
	Back b12; // 배경1-2 오브젝트

	Back b21; // 배경2-1 오브젝트
	Back b22; // 배경2-2 오브젝트

	Color backFade; // 배경이 바뀔 때 페이드 아웃 페이드 인 하기 위한 컬러변수

	// 맵 오브젝트의 이미지들
	MapObjectImg mo1;
	MapObjectImg mo2;
	MapObjectImg mo3;
	MapObjectImg mo4;

	// 외부
	JFrame superFrame;
	CardLayout cl;
	Main main;

	// 게임패널 생성자 (상위 프레임과 카드레이아웃, 그리고 Main인스턴스를 받는다)
	public GamePanel(JFrame superFrame, CardLayout cl, Object o, Settings settings) {

		this.superFrame = superFrame;
		this.cl = cl;
		this.main = (Main) o;
		this.settings = settings;

		// 일시정지 버튼
		escButton = new JButton("back");
		escButton.setBounds(350, 200, 100, 30);
		escButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				remove(escButton);
				escKeyOn = false;
			}
		});

	}

	// 게임을 세팅한다
	public void gameSet(CookieImg ci, CharacterStats stats, progress.SaveData saveData) {

		setFocusable(true);
		this.saveData = saveData;

		initCookieImg(ci); // 쿠키이미지를 세팅

		initObject(stats, saveData); // 게임 내 지형지물 인스턴스 생성

		initListener(); // 키리스너 추가

		startGameLoop(); // 단일 루프 시작
	}

	// 게임을 시작한다
	public void gameStart() {

	}

	// 화면을 그린다
	@Override
	protected void paintComponent(Graphics g) {

		// 더블버퍼는 그림을 미리그려놓고 화면에 출력한다.

		// 더블버퍼 관련
		if (buffg == null) {
			buffImage = createImage(this.getWidth(), this.getHeight());
			if (buffImage == null) {
				System.out.println("더블 버퍼링용 오프 스크린 생성 실패");
			} else {
				buffg = buffImage.getGraphics();
			}
		}

		// 투명도 관련
		Graphics2D g2 = (Graphics2D) buffg;

		super.paintComponent(buffg); // 이전 화면을 지운다.

		// 배경이미지를 그린다
		buffg.drawImage(b11.getImage(), b11.getX(), 0, b11.getWidth(), b11.getHeight() * 5 / 4, null);
		buffg.drawImage(b12.getImage(), b12.getX(), 0, b12.getWidth(), b12.getHeight() * 5 / 4, null);
		buffg.drawImage(b21.getImage(), b21.getX(), 0, b21.getWidth(), b21.getHeight() * 5 / 4, null);
		buffg.drawImage(b22.getImage(), b22.getX(), 0, b22.getWidth(), b22.getHeight() * 5 / 4, null);

		// 스테이지 넘어갈시 페이드아웃 인 효과
		if (fadeOn) {
			buffg.setColor(backFade); // 투명하게 하는방법 1
			buffg.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

		// 발판을 그린다
		for (int i = 0; i < fieldList.size(); i++) {

			Field tempFoot = fieldList.get(i);

			// 사양을 덜 잡아먹게 하기위한 조치
			if (tempFoot.getX() > -90 && tempFoot.getX() < 810) { // x값이 -90~810인 객체들만 그린다.

				buffg.drawImage(tempFoot.getImage(), tempFoot.getX(), tempFoot.getY(), tempFoot.getWidth(),
						tempFoot.getHeight(), null);
			}

		}

		// 젤리를 그린다
		for (int i = 0; i < jellyList.size(); i++) {

			Jelly tempJelly = jellyList.get(i);

			if (tempJelly.getX() > -90 && tempJelly.getX() < 810) {

				alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) tempJelly.getAlpha() / 255);
				g2.setComposite(alphaComposite); // 투명하게 하는방법 2

				buffg.drawImage(tempJelly.getImage(), tempJelly.getX(), tempJelly.getY(), tempJelly.getWidth(),
						tempJelly.getHeight(), null);

				// alpha값을 되돌린다
				alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
				g2.setComposite(alphaComposite);
			}
		}

		// 장애물을 그린다
		for (int i = 0; i < tacleList.size(); i++) {

			Tacle tempTacle = tacleList.get(i);

			if (tempTacle.getX() > -90 && tempTacle.getX() < 810) {

				buffg.drawImage(tempTacle.getImage(), tempTacle.getX(), tempTacle.getY(), tempTacle.getWidth(),
						tempTacle.getHeight(), null);
			}
		}

		// 특수 발판을 그린다
		for (int i = 0; i < specialPlatforms.size(); i++) {
			SpecialPlatform sp = specialPlatforms.get(i);
			buffg.setColor(sp.getColor());
			buffg.fillRoundRect(sp.getX(), sp.getY(), sp.getWidth(), sp.getHeight(), 12, 12);
			buffg.setColor(new Color(255, 255, 255, 200));
			buffg.drawRoundRect(sp.getX(), sp.getY(), sp.getWidth(), sp.getHeight(), 12, 12);
			buffg.setFont(new Font("Arial", Font.BOLD, 12));
			buffg.drawString(shortPlatformLabel(sp.getType()), sp.getX() + 6, sp.getY() + sp.getHeight() - 6);
		}

		// 적을 그린다
		for (int i = 0; i < enemyList.size(); i++) {
			Enemy enemy = enemyList.get(i);
			Color c = Color.RED;
			if (enemy.getType() == EnemyType.VERTICAL) {
				c = new Color(255, 80, 80);
			} else if (enemy.getType() == EnemyType.SHOOTER) {
				c = new Color(255, 200, 80);
			}
			buffg.setColor(c);
			buffg.fillOval(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
			buffg.setColor(new Color(0, 0, 0, 160));
			buffg.drawOval(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
		}

		// 투사체를 그린다
		buffg.setColor(new Color(255, 255, 255, 220));
		for (int i = 0; i < projectileList.size(); i++) {
			Projectile p = projectileList.get(i);
			buffg.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());
		}

		// 버프/디버프 아이템을 그린다 (심볼 아이콘)
		for (int i = 0; i < buffItemList.size(); i++) {
			BuffItem item = buffItemList.get(i);
			Color itemColor = getBuffColor(item.getType());
			buffg.setColor(itemColor);
			buffg.fillOval(item.getX(), item.getY(), item.getWidth(), item.getHeight());
			buffg.setColor(new Color(255, 255, 255, 180));
			buffg.setFont(new Font("Arial", Font.BOLD, 12));
			buffg.drawString(shortBuffLabel(item.getType()), item.getX() + 6, item.getY() + item.getHeight() / 2 + 4);
		}

		if (c1.isInvincible()) { // 무적상태일 경우
			// 쿠키의 alpha값을 받아온다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) c1.getAlpha() / 255);
			g2.setComposite(alphaComposite);

			// 쿠키를 그린다
			buffg.drawImage(c1.getImage(), c1.getX() - 110, c1.getY() - 170,
					cookieIc.getImage().getWidth(null) * 8 / 10, cookieIc.getImage().getHeight(null) * 8 / 10, null);

			// alpha값을 되돌린다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);

		} else { // 무적상태가 아닐 경우

			// 쿠키를 그린다
			buffg.drawImage(c1.getImage(), c1.getX() - 110, c1.getY() - 170,
					cookieIc.getImage().getWidth(null) * 8 / 10, cookieIc.getImage().getHeight(null) * 8 / 10, null);
		}

		// 피격시 붉은 화면
		if (redScreen) {

			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 125 / 255);
			g2.setComposite(alphaComposite);

			buffg.drawImage(redBg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);
		}

		// 원격 플레이어 상태가 있으면 간단히 표시
		if (remoteState != null && Util.getTime() - lastRemoteTs < 2000) {
			double alpha = Math.min(1.0, (double) (Util.getTime() - lastRemoteTs) / 200.0);
			remoteLastDrawX = remoteLastDrawX + (remoteLerpX - remoteLastDrawX) * alpha;
			remoteLastDrawY = remoteLastDrawY + (remoteLerpY - remoteLastDrawY) * alpha;
			buffg.setColor(new Color(120, 200, 255, 160));
			buffg.fillOval((int) remoteLastDrawX - 20, (int) remoteLastDrawY - 40, 80, 80);
			buffg.setColor(Color.WHITE);
			buffg.drawString("PARTNER", (int) remoteLastDrawX - 10, (int) remoteLastDrawY - 50);
		}

		// 상단 HUD 배경을 그린다 (유리 느낌 그라디언트)
		GradientPaint hudPaint = new GradientPaint(0, 0, new Color(15, 17, 26, 200), 0, 90,
				new Color(25, 35, 55, 140));
		g2.setPaint(hudPaint);
		g2.fill(new RoundRectangle2D.Double(6, 6, this.getWidth() - 12, 78, 12, 12));
		g2.setPaint(null);
		g2.setColor(new Color(255, 255, 255, 60));
		g2.setStroke(new BasicStroke(2f));
		g2.draw(new RoundRectangle2D.Double(6, 6, this.getWidth() - 12, 78, 12, 12));

		// 점수를 그린다
		Util.drawFancyString(g2, Integer.toString(resultScore), 600, 58, 30, Color.WHITE);

		// 체력게이지를 그린다
		buffg.drawImage(lifeBar.getImage(), 20, 30, null);
		buffg.setColor(Color.BLACK);
		buffg.fillRect(84 + (int) (470 * ((double) c1.getHealth() / maxHealth)), 65,
				1 + 470 - (int) (470 * ((double) c1.getHealth() / maxHealth)), 21);

		// 체력 수치를 텍스트로 표시한다
		buffg.setFont(new Font("Arial", Font.BOLD, 14));
		buffg.setColor(Color.WHITE);
		buffg.drawString(String.format("HP: %d / %d", c1.getHealth(), maxHealth), 24, 25);

		// 진행 정보(스테이지/거리/속도)를 표시한다
		buffg.setFont(new Font("Arial", Font.BOLD, 14));
		buffg.drawString(String.format("STAGE: %s", getStageLabel()), 540, 25);
		buffg.drawString(String.format("DIST: %dm", getDistanceMeters()), 540, 45);
		buffg.drawString(String.format("SPEED: %d", gameSpeed), 540, 65);

		// 버튼을 그린다
		buffg.drawImage(jumpBtn, 0, 360, 132, 100, null);
		buffg.drawImage(slideBtn, 650, 360, 132, 100, null);

		// 하단 컨트롤 힌트를 그린다
		g2.setColor(new Color(0, 0, 0, 130));
		g2.fill(new RoundRectangle2D.Double(150, this.getHeight() - 48, 500, 36, 14, 14));
		g2.setColor(new Color(255, 255, 255, 200));
		g2.setFont(new Font("Arial", Font.PLAIN, 13));
		g2.drawString("Space: Jump / Double Jump    Down: Slide    Esc: Pause", 165, this.getHeight() - 25);

		// 버프/디버프 상태를 그린다
		drawBuffStatus(g2);

		if (debugOverlay) {
			g2.setColor(new Color(0, 0, 0, 160));
			g2.fillRect(10, 100, 220, 90);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			g2.drawString(String.format("FPS: %d", debugFps), 20, 120);
			g2.drawString(String.format("Pos: (%d, %d)", c1.getX(), c1.getY()), 20, 135);
			g2.drawString(String.format("Speed: %d", gameSpeed), 20, 150);
			g2.drawString(String.format("HP: %d/%d", c1.getHealth(), maxHealth), 20, 165);
			g2.drawString(String.format("GodMode: %s", godMode ? "ON" : "OFF"), 20, 180);
		}

		if (logOverlay) {
			g2.setColor(new Color(0, 0, 0, 180));
			g2.fillRect(this.getWidth() - 320, 20, 300, 200);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
			int y = 36;
			java.util.List<String> logs = DebugLog.snapshot();
			for (int i = logs.size() - 1; i >= 0 && y < 210; i--) {
				String line = logs.get(i);
				g2.drawString(line.length() > 45 ? line.substring(0, 45) : line, this.getWidth() - 310, y);
				y += 14;
			}
		}

		if (lastPickupText != null && !lastPickupText.isEmpty() && Util.getTime() - lastPickupTime < PICKUP_DISPLAY_MS) {
			g2.setFont(new Font("Arial", Font.BOLD, 36));
			g2.setColor(Color.BLACK);
			int strWidth = g2.getFontMetrics().stringWidth(lastPickupText);
			g2.drawString(lastPickupText, this.getWidth() / 2 - strWidth / 2, 140);
		}

		if (escKeyOn) { // esc키를 누를경우 화면을 흐리게 만든다

			// alpha값을 반투명하게 만든다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 100 / 255);
			g2.setComposite(alphaComposite);

			buffg.setColor(Color.BLACK);

			buffg.fillRect(0, 0, 850, 550);

			// alpha값을 되돌린다
			alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 255 / 255);
			g2.setComposite(alphaComposite);

			// 일시정지 안내 텍스트
			buffg.setColor(Color.WHITE);
			buffg.setFont(new Font("Arial", Font.BOLD, 32));
			buffg.drawString("PAUSED", this.getWidth() / 2 - 80, this.getHeight() / 2 - 10);
			buffg.setFont(new Font("Arial", Font.PLAIN, 15));
			buffg.drawString("Esc: Resume    Start/End buttons: Mouse    Space: Jump    Down: Slide",
					this.getWidth() / 2 - 235, this.getHeight() / 2 + 18);
		}

		if (gameState == GameState.PAUSED) {
			buffg.setColor(new Color(0, 0, 0, 160));
			buffg.fillRect(0, 0, this.getWidth(), this.getHeight());
			buffg.setColor(Color.WHITE);
			buffg.setFont(new Font("Arial", Font.BOLD, 32));
			buffg.drawString("PAUSED", this.getWidth() / 2 - 80, this.getHeight() / 2 - 10);
			buffg.setFont(new Font("Arial", Font.PLAIN, 15));
			buffg.drawString("Press Esc to Resume", this.getWidth() / 2 - 90, this.getHeight() / 2 + 18);
		}

		// 버퍼이미지를 화면에 출력한다
		g.drawImage(buffImage, 0, 0, this);

		// FPS 계산
		debugFrameCount++;
		long now = Util.getTime();
		if (debugLastFpsTime == 0) {
			debugLastFpsTime = now;
		}
		if (now - debugLastFpsTime >= 1000) {
			debugFps = (int) debugFrameCount;
			debugFrameCount = 0;
			debugLastFpsTime = now;
		}
	}

	// 맵 오브젝트 이미지들을 저장
	private void makeMo() {

		mo1 = new MapObjectImg(new ImageIcon("img/Objectimg/map1img/bg1.png"),
				new ImageIcon("img/Objectimg/map1img/bg2.png"), new ImageIcon("img/Objectimg/map1img/jelly1.png"),
				new ImageIcon("img/Objectimg/map1img/jelly2.png"), new ImageIcon("img/Objectimg/map1img/jelly3.png"),
				new ImageIcon("img/Objectimg/map1img/life.png"), new ImageIcon("img/Objectimg/map1img/effectTest.png"),
				new ImageIcon("img/Objectimg/map1img/fieldIc1.png"),
				new ImageIcon("img/Objectimg/map1img/fieldIc2.png"), new ImageIcon("img/Objectimg/map1img/tacle1.gif"),
				new ImageIcon("img/Objectimg/map1img/tacle2.png"), new ImageIcon("img/Objectimg/map1img/tacle3.png"),
				new ImageIcon("img/Objectimg/map1img/tacle3.png"));

		mo2 = new MapObjectImg(new ImageIcon("img/Objectimg/map2img/back1.png"),
				new ImageIcon("img/Objectimg/map2img/back2.png"), new ImageIcon("img/Objectimg/map1img/jelly1.png"),
				new ImageIcon("img/Objectimg/map1img/jelly2.png"), new ImageIcon("img/Objectimg/map1img/jelly3.png"),
				new ImageIcon("img/Objectimg/map1img/life.png"), new ImageIcon("img/Objectimg/map1img/effectTest.png"),
				new ImageIcon("img/Objectimg/map2img/field1.png"), new ImageIcon("img/Objectimg/map2img/field2.png"),
				new ImageIcon("img/Objectimg/map2img/tacle1.png"), new ImageIcon("img/Objectimg/map2img/tacle2.png"),
				new ImageIcon("img/Objectimg/map2img/tacle3.png"), new ImageIcon("img/Objectimg/map2img/tacle3.png"));

		mo3 = new MapObjectImg(new ImageIcon("img/Objectimg/map3img/bg.png"),
				new ImageIcon("img/Objectimg/map3img/bg2.png"), new ImageIcon("img/Objectimg/map1img/jelly1.png"),
				new ImageIcon("img/Objectimg/map1img/jelly2.png"), new ImageIcon("img/Objectimg/map1img/jelly3.png"),
				new ImageIcon("img/Objectimg/map1img/life.png"), new ImageIcon("img/Objectimg/map1img/effectTest.png"),
				new ImageIcon("img/Objectimg/map3img/field.png"), new ImageIcon("img/Objectimg/map3img/field2.png"),
				new ImageIcon("img/Objectimg/map3img/tacle1.png"), new ImageIcon("img/Objectimg/map3img/tacle2.png"),
				new ImageIcon("img/Objectimg/map3img/tacle3.png"), new ImageIcon("img/Objectimg/map3img/tacle3.png"));

		mo4 = new MapObjectImg(new ImageIcon("img/Objectimg/map4img/bback.png"),
				new ImageIcon("img/Objectimg/map4img/bback2.png"), new ImageIcon("img/Objectimg/map1img/jelly1.png"),
				new ImageIcon("img/Objectimg/map1img/jelly2.png"), new ImageIcon("img/Objectimg/map1img/jelly3.png"),
				new ImageIcon("img/Objectimg/map1img/life.png"), new ImageIcon("img/Objectimg/map1img/effectTest.png"),
				new ImageIcon("img/Objectimg/map4img/ffootTest.png"),
				new ImageIcon("img/Objectimg/map4img/ffootTest2.png"),
				new ImageIcon("img/Objectimg/map4img/tacle1.png"), new ImageIcon("img/Objectimg/map4img/tacle2.png"),
				new ImageIcon("img/Objectimg/map4img/tacle2.png"), new ImageIcon("img/Objectimg/map4img/tacle2.png"));

	}

	// 메인에서 받은 쿠키 이미지 아이콘들을 인스턴스화
	private void initCookieImg(CookieImg ci) {
		// 쿠키 이미지 아이콘들
		cookieIc = ci.getCookieIc(); // 기본모션
		jumpIc = ci.getJumpIc(); // 점프모션
		doubleJumpIc = ci.getDoubleJumpIc(); // 더블점프모션
		fallIc = ci.getFallIc(); // 낙하모션(더블 점프 후)
		slideIc = ci.getSlideIc(); // 슬라이드 모션
		hitIc = ci.getHitIc(); // 부딛히는 모션
	}

	// 젤리 발판 장애물 등을 인스턴스화
	private void initImageIcon(MapObjectImg mo) {

		// 젤리 이미지 아이콘들
		jelly1Ic = mo.getJelly1Ic();
		jelly2Ic = mo.getJelly2Ic();
		jelly3Ic = mo.getJelly3Ic();
		jellyHPIc = mo.getJellyHPIc();

		jellyEffectIc = mo.getJellyEffectIc();

		// 발판 이미지 아이콘들
		field1Ic = mo.getField1Ic(); // 발판
		field2Ic = mo.getField2Ic(); // 공중발판

		// 장애물 이미지 아이콘들
		tacle10Ic = mo.getTacle10Ic(); // 1칸 장애물
		tacle20Ic = mo.getTacle20Ic(); // 2칸 장애물
		tacle30Ic = mo.getTacle30Ic(); // 3칸 장애물
		tacle40Ic = mo.getTacle40Ic(); // 4칸 장애물
	}

	// 맵의 구조를 그림판 이미지를 받아서 세팅
	private void initMap(int num, int mapLength) {

		String tempMap = null;
		int tempMapLength = 0;

		if (num == 1) {
			tempMap = "img/map/map1.png";
		} else if (num == 2) {
			tempMap = "img/map/map2.png";
		} else if (num == 3) {
			tempMap = "img/map/map3.png";
		} else if (num == 4) {
			tempMap = "img/map/map4.png";
		}

		// 맵 정보 불러오기
		try {
			sizeArr = Util.getSize(tempMap); // 맵 사이즈를 배열에 저장
			colorArr = Util.getPic(tempMap); // 맵 픽셀값을 배열에 저장
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		tempMapLength = sizeArr[0];
		int maxX = sizeArr[0]; // 맵의 넓이
		int maxY = sizeArr[1]; // 맵의 높이

		for (int i = 0; i < maxX; i += 1) { // 젤리는 1칸을 차지하기 때문에 1,1사이즈로 반복문을 돌린다.
			for (int j = 0; j < maxY; j += 1) {
				if (colorArr[i][j] == 16776960) { // 색값이 16776960일 경우 기본젤리 생성
					// 좌표에 40을 곱하고, 넓이와 높이는 30으로 한다.
					jellyList.add(new Jelly(jelly1Ic.getImage(), i * 40 + mapLength * 40, j * 40, 30, 30, 255, 1234));

				} else if (colorArr[i][j] == 13158400) { // 색값이 13158400일 경우 노란젤리 생성
					// 좌표에 40을 곱하고, 넓이와 높이는 30으로 한다.
					jellyList.add(new Jelly(jelly2Ic.getImage(), i * 40 + mapLength * 40, j * 40, 30, 30, 255, 2345));

				} else if (colorArr[i][j] == 9868800) { // 색값이 9868800일 경우 노란젤리 생성
					// 좌표에 40을 곱하고, 넓이와 높이는 30으로 한다.
					jellyList.add(new Jelly(jelly3Ic.getImage(), i * 40 + mapLength * 40, j * 40, 30, 30, 255, 3456));

				} else if (colorArr[i][j] == 16737280) { // 색값이 16737280일 경우 피 물약 생성
					// 좌표에 40을 곱하고, 넓이와 높이는 30으로 한다.
					jellyList.add(new Jelly(jellyHPIc.getImage(), i * 40 + mapLength * 40, j * 40, 30, 30, 255, 4567));
				}
			}
		}

		for (int i = 0; i < maxX; i += 2) { // 발판은 4칸을 차지하는 공간이기 때문에 2,2사이즈로 반복문을 돌린다.
			for (int j = 0; j < maxY; j += 2) {
				if (colorArr[i][j] == 0) { // 색값이 0 일경우 (검은색)
					// 좌표에 40을 곱하고, 넓이와 높이는 80으로 한다.
					fieldList.add(new Field(field1Ic.getImage(), i * 40 + mapLength * 40, j * 40, 80, 80));

				} else if (colorArr[i][j] == 6579300) { // 색값이 6579300 일경우 (회색)
					// 좌표에 40을 곱하고, 넓이와 높이는 80으로 한다.
					fieldList.add(new Field(field2Ic.getImage(), i * 40 + mapLength * 40, j * 40, 80, 80));
				}
			}
		}

		for (int i = 0; i < maxX; i += 2) { // 장애물은 4칸 이상을 차지한다. 추후 수정
			for (int j = 0; j < maxY; j += 2) {
				if (colorArr[i][j] == 16711680) { // 색값이 16711680일 경우 (빨간색) 1칸
					// 좌표에 40을 곱하고, 넓이와 높이는 80으로 한다.
					tacleList.add(new Tacle(tacle10Ic.getImage(), i * 40 + mapLength * 40, j * 40, 80, 80, 0));

				} else if (colorArr[i][j] == 16711830) { // 색값이 16711830일 경우 (분홍) 2칸
					// 좌표에 40을 곱하고, 넓이와 높이는 160으로 한다.
					tacleList.add(new Tacle(tacle20Ic.getImage(), i * 40 + mapLength * 40, j * 40, 80, 160, 0));

				} else if (colorArr[i][j] == 16711935) { // 색값이 16711830일 경우 (핫핑크) 3칸
					// 좌표에 40을 곱하고, 넓이와 높이는 240으로 한다.
					tacleList.add(new Tacle(tacle30Ic.getImage(), i * 40 + mapLength * 40, j * 40, 80, 240, 0));
				}
			}
		}

		this.mapLength = this.mapLength + tempMapLength;
	}

	// makeMo, initImageIcon, imitMap 메서드를 이용해서 객체 생성
	private void initObject(CharacterStats stats, progress.SaveData saveData) {

		// 생명게이지 이미지아이콘
		lifeBar = new ImageIcon("img/Objectimg/lifebar/lifeBar1.png");

		// 피격 붉은 이미지
		redBg = new ImageIcon("img/Objectimg/lifebar/redBg.png");

		// 점프버튼
		jumpButtonIconUp = new ImageIcon("img/Objectimg/lifebar/jumpno.png");
		jumpButtonIconDown = new ImageIcon("img/Objectimg/lifebar/jumpdim.png");

		// 슬라이드 버튼
		slideIconUp = new ImageIcon("img/Objectimg/lifebar/slideno.png");
		slideIconDown = new ImageIcon("img/Objectimg/lifebar/slidedim.png");

		jumpBtn = jumpButtonIconUp.getImage();
		slideBtn = slideIconUp.getImage();

		jellyList = new ArrayList<>(); // 젤리 리스트

		fieldList = new ArrayList<>(); // 발판 리스트

		tacleList = new ArrayList<>(); // 장애물 리스트

		specialPlatforms = new ArrayList<>(); // 특수 발판 리스트
		enemyList = new ArrayList<>(); // 적 리스트
		projectileList = new ArrayList<>(); // 투사체 리스트

		buffItemList = new ArrayList<>(); // 버프/디버프 아이템 리스트
		for (BuffType type : BuffType.values()) {
			buffEndTime.put(type, 0L);
			buffDuration.put(type, 1L);
		}
		shieldActive = false;
		speedActive = false;
		doubleScoreActive = false;
		magnetActive = false;
		giantActive = false;
		slowActive = false;

		mapLengthList = new ArrayList<>(); // 다음 맵의 시작지점을 확인하기위한 배열

		// 맵 인스턴스들을 생성

		makeMo();

		initImageIcon(mo1);
		initMap(1, mapLength);
		mapLengthList.add(mapLength);

		initImageIcon(mo2);
		initMap(2, mapLength);
		mapLengthList.add(mapLength);

		initImageIcon(mo3);
		initMap(3, mapLength);
		mapLengthList.add(mapLength);

		initImageIcon(mo4);
		initMap(4, mapLength);

		// 배경이미지 아이콘
		backIc = mo1.getBackIc();
		secondBackIc = mo1.getSecondBackIc();

		backIc2 = mo2.getBackIc();
		secondBackIc2 = mo2.getSecondBackIc();

		backIc3 = mo3.getBackIc();
		secondBackIc3 = mo3.getSecondBackIc();

		backIc4 = mo4.getBackIc();
		secondBackIc4 = mo4.getSecondBackIc();

		// 쿠키 인스턴스 생성 / 기본 자료는 클래스안에 내장 되어 있기 때문에 이미지만 넣었다.
		c1 = new Cookie(cookieIc.getImage());

		// 능력치 적용 (캐릭터 기본 + 프로필 업그레이드)
		int profileSpeedUp = saveData.getProfile().getSpeedLevel() - 1;
		int profileJumpUp = saveData.getProfile().getJumpLevel() - 1;
		int profileHealthUp = saveData.getProfile().getHealthLevel() - 1;

		baseGameSpeed = stats.getBaseSpeed() + profileSpeedUp;
		gameSpeed = baseGameSpeed;
		// 점프 계수는 캐릭터 기본 + 업그레이드 가중치
		jumpPowerBase = stats.getBaseJump() + profileJumpUp * 2;
		maxHealth = stats.getBaseHealth() + profileHealthUp * 100;
		if (maxHealth < 600) {
			maxHealth = 600;
		}
		c1.setHealth(maxHealth);

		// 쿠키의 정면 위치 / 쿠키의 x값과 높이를 더한 값
		face = c1.getX() + c1.getWidth();

		// 쿠키의 발밑 위치 / 쿠키의 y값과 높이를 더한 값
		foot = c1.getY() + c1.getHeight();

		// 배경1-1 인스턴스 생성
		b11 = new Back(backIc.getImage(), 0, 0, backIc.getImage().getWidth(null), backIc.getImage().getHeight(null));

		// 배경1-2 인스턴스 생성
		b12 = new Back(backIc.getImage(), backIc.getImage().getWidth(null), 0, // y 값 (조정 필요)
				backIc.getImage().getWidth(null), backIc.getImage().getHeight(null));

		// 배경2-1 인스턴스 생성
		b21 = new Back(secondBackIc.getImage(), 0, 0, secondBackIc.getImage().getWidth(null),
				secondBackIc.getImage().getHeight(null));

		// 배경2-2 인스턴스 생성
		b22 = new Back(secondBackIc.getImage(), secondBackIc.getImage().getWidth(null), 0, // y 값 (조정 필요)
				secondBackIc.getImage().getWidth(null), secondBackIc.getImage().getHeight(null));

		backFade = new Color(0, 0, 0, 0);

	}

	// 리스너 추가 메서드
	private void initListener() {
		addKeyListener(new KeyAdapter() { // 키 리스너 추가

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == settings.getKeyPause()) {
					if (gameState == GameState.PAUSED) {
						gameState = GameState.PLAYING;
						if (!menuStack.isEmpty()) {
							menuStack.pop();
						}
					} else {
						gameState = GameState.PAUSED;
						menuStack.push("Paused");
					}
				}

				if (gameState == GameState.PLAYING) {
					if (e.getKeyCode() == settings.getKeyJump() || e.getKeyCode() == KeyEvent.VK_W
							|| e.getKeyCode() == KeyEvent.VK_SPACE) {// 점프 키를 누르고 더블점프가 2가 아닐때
						jumpBtn = jumpButtonIconDown.getImage();
						if (c1.getCountJump() < 2) {
							jump(); // 점프 메서드 가동
						}
					}
					if (e.getKeyCode() == settings.getKeySlide() || e.getKeyCode() == KeyEvent.VK_S
							|| e.getKeyCode() == KeyEvent.VK_DOWN) { // 다운키를 눌렀을 때
						slideBtn = slideIconDown.getImage();
						downKeyOn = true; // downKeyOn 변수를 true로

						if (c1.getImage() != slideIc.getImage() // 쿠키이미지가 슬라이드 이미지가 아니고
								&& !c1.isJump() // 점프 중이 아니며
								&& !c1.isFall()) { // 낙하 중도 아닐 때

							c1.setImage(slideIc.getImage()); // 이미지를 슬라이드이미지로 변경

						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S
						|| e.getKeyCode() == settings.getKeySlide()) { // 다운키를 뗐을 때
					slideBtn = slideIconUp.getImage();
					downKeyOn = false; // downKeyOn 변수를 false로

					if (c1.getImage() != cookieIc.getImage() // 쿠키이미지가 기본이미지가 아니고
							&& !c1.isJump() // 점프 중이 아니며
							&& !c1.isFall()) { // 낙하 중도 아닐 때

						c1.setImage(cookieIc.getImage()); // 이미지를 기본이미지로 변경
					}
				}

				if (e.getKeyCode() == settings.getKeyJump() || e.getKeyCode() == KeyEvent.VK_W
						|| e.getKeyCode() == KeyEvent.VK_SPACE) {
					jumpBtn = jumpButtonIconUp.getImage();
				}

				if (e.getKeyCode() == KeyEvent.VK_F1) { // 디버그 오버레이 토글
					debugOverlay = !debugOverlay;
				}
				if (e.getKeyCode() == KeyEvent.VK_F2) { // 무적 토글
					godMode = !godMode;
					c1.setInvincible(godMode);
				}
				if (e.getKeyCode() == KeyEvent.VK_F3) { // 로그 오버레이 토글
					logOverlay = !logOverlay;
				}
			}
		});
	}

	// 리페인트 전용 쓰레드 추가 메서드
	private void startGameLoop() {
		lastTick = Util.getTime();
		if (gameTimer != null) {
			gameTimer.stop();
		}
		gameTimer = new Timer(16, e -> {
			long now = Util.getTime();
			double dt = (now - lastTick) / 1000.0; // seconds
			lastTick = now;
			if (gameState == GameState.PLAYING) {
				accumulator += dt;
				while (accumulator >= fixedStep) {
					updateGame(fixedStep);
					sendNetState();
					accumulator -= fixedStep;
				}
			}
			repaint();
		});
		gameTimer.start();

		if (netEnabled) {
			startNetReceive();
			System.out.println("UDP sync enabled: local " + netConfig.localPort + " -> remote " + netConfig.remoteHost + ":"
					+ netConfig.remotePort);
		}

		gameState = GameState.PLAYING;
		int bgmVol = Math.max(3, settings.getBgmVolume() / 4); // 기존의 1/4로 줄임, 최소 3%
		// bgmPlayer.playLoop("sound/background.wav", bgmVol);
	}

	// 단일 루프에서 호출되는 업데이트
	private void updateGame(double dt) {

		refreshBuffFlags();
		gameSpeed = calcGameSpeed();

		Body playerBody = new Body(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight(), CollisionLayer.PLAYER,
				CollisionLayer.ENEMY | CollisionLayer.PROJECTILE | CollisionLayer.ITEM | CollisionLayer.PLATFORM);

		if (Math.random() < gameConfig.spawnBuff) {
			spawnBuffItem();
		}
		if (Math.random() < gameConfig.spawnPlatform) {
			spawnSpecialPlatform();
		}
		if (Math.random() < gameConfig.spawnEnemy) {
			spawnEnemy();
		}

		if (runPage > 800) { // 800픽셀 이동 마다 체력이 10씩 감소한다
			c1.setHealth(c1.getHealth() - 10);
			runPage = 0;
		}

		runPage += gameSpeed; // 화면이 이동하면 runPage에 이동한 만큼 저장된다.
		totalDistance += gameSpeed;
		updateProgressDistance();

		// 시간 경과에 따른 체력 감소
		healthTickAcc += dt;
		if (healthTickAcc >= 1.0) {
			int ticks = (int) (healthTickAcc);
			if (!godMode) {
				c1.setHealth(c1.getHealth() - (int) (gameConfig.healthDrainPerSec * ticks));
			}
			healthTickAcc -= ticks;
		}

		foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔
		if (foot > 1999 || c1.getHealth() < 1) {
			main.getEndPanel().setResultScore(resultScore);
			main.getEndPanel().updateLeaderboard(saveData.getLeaderboard());
			cl.show(superFrame.getContentPane(), "end");
			main.setGamePanel(new GamePanel(superFrame, cl, main, settings));
			superFrame.requestFocus();
			updateLeaderboardAndSave();
			gameState = GameState.RESULT;
			// if (bgmPlayer != null) bgmPlayer.stop();
			SoundManager.playEffect("sound/gameover.wav", settings.getSfxVolume());
			return;
		}

		// 배경 이미지 변경 (페이드 스레드는 기존 로직 유지)
		if (fadeOn == false && mapLengthList.size() >= 3) {
			if (mapLength > mapLengthList.get(2) * 40 + 800 && b11.getImage() != backIc4.getImage()) {
				fadeOn = true;
				new Thread(() -> {
					backFadeOut();
					b11 = new Back(backIc4.getImage(), 0, 0, backIc4.getImage().getWidth(null),
							backIc4.getImage().getHeight(null));
					b12 = new Back(backIc4.getImage(), backIc4.getImage().getWidth(null), 0,
							backIc4.getImage().getWidth(null), backIc4.getImage().getHeight(null));
					b21 = new Back(secondBackIc4.getImage(), 0, 0, secondBackIc4.getImage().getWidth(null),
							secondBackIc4.getImage().getHeight(null));
					b22 = new Back(secondBackIc4.getImage(), secondBackIc4.getImage().getWidth(null), 0,
							secondBackIc4.getImage().getWidth(null), secondBackIc4.getImage().getHeight(null));
					backFadeIn();
					fadeOn = false;
				}).start();

			} else if (mapLength > mapLengthList.get(1) * 40 + 800 && mapLength < mapLengthList.get(2) * 40 + 800
					&& b11.getImage() != backIc3.getImage()) {
				fadeOn = true;
				new Thread(() -> {
					backFadeOut();
					b11 = new Back(backIc3.getImage(), 0, 0, backIc3.getImage().getWidth(null),
							backIc3.getImage().getHeight(null));
					b12 = new Back(backIc3.getImage(), backIc3.getImage().getWidth(null), 0,
							backIc3.getImage().getWidth(null), backIc3.getImage().getHeight(null));
					b21 = new Back(secondBackIc3.getImage(), 0, 0, secondBackIc3.getImage().getWidth(null),
							secondBackIc3.getImage().getHeight(null));
					b22 = new Back(secondBackIc3.getImage(), secondBackIc3.getImage().getWidth(null), 0,
							secondBackIc3.getImage().getWidth(null), secondBackIc3.getImage().getHeight(null));
					backFadeIn();
					fadeOn = false;
				}).start();

			} else if (mapLength > mapLengthList.get(0) * 40 + 800 && mapLength < mapLengthList.get(1) * 40 + 800
					&& b11.getImage() != backIc2.getImage()) {
				fadeOn = true;
				new Thread(() -> {
					backFadeOut();
					b11 = new Back(backIc2.getImage(), 0, 0, backIc2.getImage().getWidth(null),
							backIc2.getImage().getHeight(null));
					b12 = new Back(backIc2.getImage(), backIc2.getImage().getWidth(null), 0,
							backIc2.getImage().getWidth(null), backIc2.getImage().getHeight(null));
					b21 = new Back(secondBackIc2.getImage(), 0, 0, secondBackIc2.getImage().getWidth(null),
							secondBackIc2.getImage().getHeight(null));
					b22 = new Back(secondBackIc2.getImage(), secondBackIc2.getImage().getWidth(null), 0,
							secondBackIc2.getImage().getWidth(null), secondBackIc2.getImage().getHeight(null));
					backFadeIn();
					fadeOn = false;
				}).start();
			}
		}

		mapLength += gameSpeed;

		// 배경 이동
		if (b11.getX() < -(b11.getWidth() - 1)) {
			b11.setX(b11.getWidth());
		}
		if (b12.getX() < -(b12.getWidth() - 1)) {
			b12.setX(b12.getWidth());
		}
		if (b21.getX() < -(b21.getWidth() - 1)) {
			b21.setX(b21.getWidth());
		}
		if (b22.getX() < -(b22.getWidth() - 1)) {
			b22.setX(b22.getWidth());
		}
		b11.setX(b11.getX() - gameSpeed / 3);
		b12.setX(b12.getX() - gameSpeed / 3);
		b21.setX(b21.getX() - gameSpeed * 2 / 3);
		b22.setX(b22.getX() - gameSpeed * 2 / 3);

		// 발판 이동
		for (int i = 0; i < fieldList.size(); i++) {
			Field tempField = fieldList.get(i);
			if (tempField.getX() < -90) {
				fieldList.remove(tempField);
			} else {
				tempField.setX(tempField.getX() - gameSpeed);
			}
		}

		// 젤리 이동/획득
		for (int i = 0; i < jellyList.size(); i++) {
			Jelly tempJelly = jellyList.get(i);
			if (tempJelly.getX() < -90) {
				fieldList.remove(tempJelly);
			} else {
				tempJelly.setX(tempJelly.getX() - gameSpeed);
				if (tempJelly.getImage() == jellyEffectIc.getImage() && tempJelly.getAlpha() > 4) {
					tempJelly.setAlpha(tempJelly.getAlpha() - 5);
				}
				if (magnetActive) {
					int centerX = tempJelly.getX() + tempJelly.getWidth() / 2;
					int centerY = tempJelly.getY() + tempJelly.getHeight() / 2;
					int cookieCenterX = c1.getX() + c1.getWidth() / 2;
					int cookieCenterY = c1.getY() + c1.getHeight() / 2;
					double distance = Math.sqrt(Math.pow(centerX - cookieCenterX, 2) + Math.pow(centerY - cookieCenterY, 2));
					if (distance < magnetRadius && distance > 0) {
						int pull = 4;
						if (centerX > cookieCenterX) {
							tempJelly.setX(tempJelly.getX() - pull);
						} else {
							tempJelly.setX(tempJelly.getX() + pull);
						}
						if (centerY > cookieCenterY) {
							tempJelly.setY(tempJelly.getY() - pull);
						} else {
							tempJelly.setY(tempJelly.getY() + pull);
						}
					}
				}

				foot = c1.getY() + c1.getHeight();
				if (c1.getImage() != slideIc.getImage() && tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
						&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
						&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY()
						&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
						&& tempJelly.getImage() != jellyEffectIc.getImage()) {

					if (tempJelly.getImage() == jellyHPIc.getImage()) {
						if ((c1.getHealth() + 100) > maxHealth) {
							c1.setHealth(maxHealth);
						} else {
							c1.setHealth(c1.getHealth() + 100);
						}
					}
					tempJelly.setImage(jellyEffectIc.getImage());
					int plusScore = tempJelly.getScore();
					if (doubleScoreActive) {
						plusScore = plusScore * 2;
					}
					resultScore = resultScore + plusScore;
					totalJelly++;
					updateProgressJelly();

				} else if (c1.getImage() == slideIc.getImage()
						&& tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
						&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
						&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY() + c1.getHeight() * 1 / 3
						&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
						&& tempJelly.getImage() != jellyEffectIc.getImage()) {

					if (tempJelly.getImage() == jellyHPIc.getImage()) {
						if ((c1.getHealth() + 100) > maxHealth) {
							c1.setHealth(maxHealth);
						} else {
							c1.setHealth(c1.getHealth() + 100);
						}
					}
					tempJelly.setImage(jellyEffectIc.getImage());
					int plusScore = tempJelly.getScore();
					if (doubleScoreActive) {
						plusScore = plusScore * 2;
					}
					resultScore = resultScore + plusScore;
					totalJelly++;
					updateProgressJelly();
				}
			}
		}

		// 버프/디버프 아이템 이동 및 획득
		for (int i = 0; i < buffItemList.size(); i++) {
			BuffItem item = buffItemList.get(i);
			if (item.getX() < -60) {
				buffItemList.remove(item);
				continue;
			}
			item.setX(item.getX() - gameSpeed);

			face = c1.getX() + c1.getWidth();
			foot = c1.getY() + c1.getHeight();
					playerBody.set(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight());
					Body itemBody = new Body(item.getX(), item.getY(), item.getWidth(), item.getHeight(), CollisionLayer.ITEM,
							CollisionLayer.PLAYER);
					if (playerBody.intersects(itemBody)) {
						applyBuffFromItem(item.getType());
						DebugLog.add("Buff picked: " + item.getType());
						showPickupMessage(buffLabel(item.getType()));
						buffItemList.remove(item);
					}
				}

		// 장애물 이동 및 충돌
		for (int i = 0; i < tacleList.size(); i++) {
			Tacle tempTacle = tacleList.get(i);
			if (tempTacle.getX() < -90) {
				fieldList.remove(tempTacle);
			} else {
				tempTacle.setX(tempTacle.getX() - gameSpeed);
				face = c1.getX() + c1.getWidth();
				foot = c1.getY() + c1.getHeight();

				playerBody.set(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight());
				Body tacleBody = new Body(tempTacle.getX(), tempTacle.getY(), tempTacle.getWidth(), tempTacle.getHeight(),
						CollisionLayer.ENEMY, CollisionLayer.PLAYER);

				if (!c1.isInvincible() && playerBody.intersects(tacleBody)) {
					if (giantActive) {
						tacleList.remove(tempTacle);
						resultScore += 200;
					} else if (shieldActive) {
						shieldActive = false;
						buffEndTime.put(BuffType.SHIELD, 0L);
					} else {
						hit();
						DebugLog.add("Hit by obstacle");
					}
				}
			}
		}

		// 특수 발판 이동/충돌
		for (int i = 0; i < specialPlatforms.size(); i++) {
			SpecialPlatform sp = specialPlatforms.get(i);
			if (sp.getX() < -120) {
				specialPlatforms.remove(sp);
				continue;
			}
			sp.setX(sp.getX() - gameSpeed + sp.getVx());
			sp.setY(sp.getY() + sp.getVy());
			if (sp.getType() == PlatformType.MOVING) {
				if (sp.getY() < 120 || sp.getY() > 320) {
					sp.setVy(-sp.getVy());
				}
			}
			if (sp.getType() == PlatformType.COLLAPSING && sp.isTriggered()) {
				if (Util.getTime() - sp.getCollapseStart() > 600) {
					specialPlatforms.remove(sp);
					continue;
				}
			}
			if (sp.getType() == PlatformType.FALLING && sp.isTriggered()) {
				sp.setVy(sp.getVy() + 1);
			}
		}

		// 적 이동/충돌
		for (int i = 0; i < enemyList.size(); i++) {
			Enemy enemy = enemyList.get(i);
			if (enemy.getX() < -120) {
				enemyList.remove(enemy);
				continue;
			}
			if (enemy.getType() == EnemyType.CHASER) {
				if (enemy.getY() < c1.getY()) {
					enemy.setY(enemy.getY() + 1);
				} else if (enemy.getY() > c1.getY()) {
					enemy.setY(enemy.getY() - 1);
				}
			} else if (enemy.getType() == EnemyType.VERTICAL) {
				if (enemy.getY() < 120 || enemy.getY() > 340) {
					enemy.setVy(-enemy.getVy());
				}
				enemy.setY(enemy.getY() + enemy.getVy());
			} else if (enemy.getType() == EnemyType.SHOOTER) {
				if (Util.getTime() - enemy.getLastFire() > 1500) {
					projectileList.add(new Projectile(enemy.getX(), enemy.getY() + enemy.getHeight() / 2, 12, 12, -8, 0));
					enemy.setLastFire(Util.getTime());
				}
			}
			enemy.setX(enemy.getX() - gameSpeed - 1);

			playerBody.set(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight());
			Body enemyBody = new Body(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), CollisionLayer.ENEMY,
					CollisionLayer.PLAYER);

			if (!c1.isInvincible() && playerBody.intersects(enemyBody)) {
				if (giantActive) {
					enemyList.remove(enemy);
					resultScore += 200;
				} else if (shieldActive) {
					shieldActive = false;
					buffEndTime.put(BuffType.SHIELD, 0L);
					enemyList.remove(enemy);
				} else {
					hit();
					DebugLog.add("Hit by enemy");
				}
			}
		}

		// 투사체 이동/충돌
		for (int i = 0; i < projectileList.size(); i++) {
			Projectile p = projectileList.get(i);
			p.setX(p.getX() + p.getVx());
			p.setY(p.getY() + p.getVy());
			if (p.getX() < -40 || p.getX() > 900) {
				projectileList.remove(p);
				continue;
			}
			playerBody.set(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight());
			Body projBody = new Body(p.getX(), p.getY(), p.getWidth(), p.getHeight(), CollisionLayer.PROJECTILE,
					CollisionLayer.PLAYER);
			if (!c1.isInvincible() && playerBody.intersects(projBody)) {
				if (shieldActive) {
					shieldActive = false;
					buffEndTime.put(BuffType.SHIELD, 0L);
				} else {
					hit();
				}
				projectileList.remove(p);
			}
		}

		// 쿠키가 밟을 발판 높이 계산
		int tempField;
		int tempNowField;
		if (c1.isInvincible()) {
			tempNowField = 400;
		} else {
			tempNowField = 2000;
		}
		for (int i = 0; i < fieldList.size(); i++) {
			int tempX = fieldList.get(i).getX();
			if (tempX > c1.getX() - 60 && tempX <= face) {
				tempField = fieldList.get(i).getY();
				foot = c1.getY() + c1.getHeight();
				if (tempField < tempNowField && tempField >= foot) {
					tempNowField = tempField;
				}
			}
		}
		for (int i = 0; i < specialPlatforms.size(); i++) {
			SpecialPlatform sp = specialPlatforms.get(i);
			int tempX = sp.getX();
			if (tempX > c1.getX() - 60 && tempX <= face) {
				tempField = sp.getY();
				foot = c1.getY() + c1.getHeight();
				if (tempField < tempNowField && tempField >= foot) {
					tempNowField = tempField;
					if (!sp.isTriggered()) {
						if (sp.getType() == PlatformType.FALLING || sp.getType() == PlatformType.COLLAPSING) {
							sp.setTriggered(true);
							sp.setCollapseStart(Util.getTime());
							showPickupMessage(platformLabel(sp.getType()));
						}
					}
					if (sp.getType() == PlatformType.JUMPPAD) {
						c1.setY(c1.getY() - 15);
						c1.setJump(true);
						c1.setCountJump(1);
						showPickupMessage(platformLabel(sp.getType()));
					} else if (sp.getType() == PlatformType.RAIL) {
						downKeyOn = true;
						c1.setImage(slideIc.getImage());
						showPickupMessage(platformLabel(sp.getType()));
					}
				}
			}
		}
		nowField = tempNowField;

		applyGravityAndLanding();
	}

	private void applyGravityAndLanding() {
		foot = c1.getY() + c1.getHeight();
		if (c1.isJump()) { // 상승 중
			if (jumpVelocity > 0) {
				c1.setY(c1.getY() - (int) Math.round(jumpVelocity));
				jumpVelocity -= 1.2;
			} else {
				c1.setJump(false);
				playerState = PlayerState.FALL;
			}
		}

		foot = c1.getY() + c1.getHeight();
		if (!escKeyOn && foot < nowField && !c1.isJump()) {
			c1.setFall(true);
			fallVelocity += 0.8;
			if (fallVelocity > 20) {
				fallVelocity = 20;
			}
			int fallY = (int) Math.round(fallVelocity);
			if (foot + fallY >= nowField) {
				fallY = nowField - foot;
				c1.setFall(false);
				fallVelocity = 0;
				if (!downKeyOn) {
					c1.setImage(cookieIc.getImage());
				} else {
					c1.setImage(slideIc.getImage());
				}
				c1.setCountJump(0);
			}
			c1.setY(c1.getY() + fallY);
			playerState = PlayerState.FALL;
		} else if (foot >= nowField) {
			c1.setFall(false);
			fallVelocity = 0;
			if (!c1.isJump()) {
				playerState = PlayerState.RUN;
			}
		}
	}

	private void sendNetState() {
		if (!netEnabled || udpSync == null) {
			return;
		}
		net.GameState state = new net.GameState();
		state.seq = sendSeq++;
		state.x = c1.getX();
		state.y = c1.getY();
		state.health = c1.getHealth();
		state.score = resultScore;
		state.imageIndex = 0;
		state.timestamp = Util.getTime();
		udpSync.send(state);
	}

	private void startNetReceive() {
		if (udpSync != null) {
			udpSync.stop();
		}
		udpSync = new UdpSync(netConfig);
		udpSync.start(gs -> {
			if (gs.seq <= remoteSeq) {
				return;
			}
			remoteSeq = gs.seq;
			remoteState = gs;
			lastRemoteTs = gs.timestamp;
			remoteLerpX = gs.x;
			remoteLerpY = gs.y;
		});
	}

	private void showPickupMessage(String msg) {
		lastPickupText = msg;
		lastPickupTime = Util.getTime();
	}

	private String buffLabel(BuffType type) {
		switch (type) {
		case MAGNET:
			return "자석!";
		case SHIELD:
			return "실드!";
		case SPEED:
			return "속도 업!";
		case GIANT:
			return "거대화!";
		case DOUBLE_SCORE:
			return "점수 x2!";
		case SLOW:
			return "슬로우!";
		case REVERSE_GRAVITY:
			return "역중력!";
		default:
			return "버프!";
		}
	}

	private String platformLabel(PlatformType type) {
		switch (type) {
		case MOVING:
			return "이동 발판!";
		case FALLING:
			return "낙하 발판!";
		case COLLAPSING:
			return "붕괴 발판!";
		case JUMPPAD:
			return "점프 패드!";
		case RAIL:
			return "레일!";
		default:
			return "발판!";
		}
	}

	// 화면을 움직이고 젤리를 먹거나, 장애물에 부딛히는 등의 이벤트를 발생시키는 메서드
	private void mapMove() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					refreshBuffFlags(); // 버프 만료 체크
					gameSpeed = calcGameSpeed(); // 속도 재계산

					// 일정 확률로 버프/디버프 아이템 스폰
					if (Math.random() < 0.0025) {
						spawnBuffItem();
					}

					// 특수 발판/적 스폰
					if (Math.random() < 0.0018) {
						spawnSpecialPlatform();
					}
					if (Math.random() < 0.0015) {
						spawnEnemy();
					}

					if (runPage > 800) { // 800픽셀 이동 마다 체력이 10씩 감소한다 (추후 맵길이에 맟추어 감소량 조절)
						c1.setHealth(c1.getHealth() - 10);
						runPage = 0;
					}

					runPage += gameSpeed; // 화면이 이동하면 runPage에 이동한 만큼 저장된다.
					totalDistance += gameSpeed;
					updateProgressDistance();

					foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔
					if (foot > 1999 || c1.getHealth() < 1) {
						main.getEndPanel().setResultScore(resultScore);
						cl.show(superFrame.getContentPane(), "end");
						main.setGamePanel(new GamePanel(superFrame, cl, main, settings));
						superFrame.requestFocus();
						updateLeaderboardAndSave();
						escKeyOn = true;
					}

					// 배경 이미지 변경
					if (fadeOn == false) { // 페이드아웃인 상태가 아닐때
						if (mapLength > mapLengthList.get(2) * 40 + 800 && b11.getImage() != backIc4.getImage()) {
							fadeOn = true;

							new Thread(new Runnable() {

								@Override
								public void run() {

									backFadeOut();

									b11 = new Back(backIc4.getImage(), 0, 0, backIc4.getImage().getWidth(null),
											backIc4.getImage().getHeight(null));

									b12 = new Back(backIc4.getImage(), backIc4.getImage().getWidth(null), 0,
											backIc4.getImage().getWidth(null), backIc4.getImage().getHeight(null));

									b21 = new Back(secondBackIc4.getImage(), 0, 0,
											secondBackIc4.getImage().getWidth(null),
											secondBackIc4.getImage().getHeight(null));

									b22 = new Back(secondBackIc4.getImage(), secondBackIc4.getImage().getWidth(null), 0,
											secondBackIc4.getImage().getWidth(null),
											secondBackIc4.getImage().getHeight(null));

									backFadeIn();
									fadeOn = false;
								}
							}).start();

						} else if (mapLength > mapLengthList.get(1) * 40 + 800
								&& mapLength < mapLengthList.get(2) * 40 + 800
								&& b11.getImage() != backIc3.getImage()) {
							fadeOn = true;

							new Thread(new Runnable() {

								@Override
								public void run() {

									backFadeOut();

									b11 = new Back(backIc3.getImage(), 0, 0, backIc3.getImage().getWidth(null),
											backIc3.getImage().getHeight(null));

									b12 = new Back(backIc3.getImage(), backIc3.getImage().getWidth(null), 0,
											backIc3.getImage().getWidth(null), backIc3.getImage().getHeight(null));

									b21 = new Back(secondBackIc3.getImage(), 0, 0,
											secondBackIc3.getImage().getWidth(null),
											secondBackIc3.getImage().getHeight(null));

									b22 = new Back(secondBackIc3.getImage(), secondBackIc3.getImage().getWidth(null), 0,
											secondBackIc3.getImage().getWidth(null),
											secondBackIc3.getImage().getHeight(null));

									backFadeIn();
									fadeOn = false;
								}
							}).start();

						} else if (mapLength > mapLengthList.get(0) * 40 + 800
								&& mapLength < mapLengthList.get(1) * 40 + 800
								&& b11.getImage() != backIc2.getImage()) {
							fadeOn = true;

							new Thread(new Runnable() {

								@Override
								public void run() {

									backFadeOut();

									b11 = new Back(backIc2.getImage(), 0, 0, backIc2.getImage().getWidth(null),
											backIc2.getImage().getHeight(null));

									b12 = new Back(backIc2.getImage(), backIc2.getImage().getWidth(null), 0,
											backIc2.getImage().getWidth(null), backIc2.getImage().getHeight(null));

									b21 = new Back(secondBackIc2.getImage(), 0, 0,
											secondBackIc2.getImage().getWidth(null),
											secondBackIc2.getImage().getHeight(null));

									b22 = new Back(secondBackIc2.getImage(), secondBackIc2.getImage().getWidth(null), 0,
											secondBackIc2.getImage().getWidth(null),
											secondBackIc2.getImage().getHeight(null));

									backFadeIn();
									fadeOn = false;
								}
							}).start();
						}
					}

					// 배경이미지 변경을 위한 맵이동 길이 측정
					mapLength += gameSpeed;

					if (b11.getX() < -(b11.getWidth() - 1)) { // 배경1-1 이 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-2뒤에 붙음
						b11.setX(b11.getWidth());
					}
					if (b12.getX() < -(b12.getWidth() - 1)) { // 배경1-2 가 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-1뒤에 붙음
						b12.setX(b12.getWidth());
					}

					if (b21.getX() < -(b21.getWidth() - 1)) { // 배경1-1 이 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-2뒤에 붙음
						b21.setX(b21.getWidth());
					}
					if (b22.getX() < -(b22.getWidth() - 1)) { // 배경1-2 가 -(배경넓이)보다 작으면, 즉 화면밖으로 모두나가면 배경 1-1뒤에 붙음
						b22.setX(b22.getWidth());
					}

					// 배경의 x좌표를 -1 해준다 (왼쪽으로 흐르는 효과)
					b11.setX(b11.getX() - gameSpeed / 3);
					b12.setX(b12.getX() - gameSpeed / 3);

					b21.setX(b21.getX() - gameSpeed * 2 / 3);
					b22.setX(b22.getX() - gameSpeed * 2 / 3);

					// 발판위치를 -3 씩 해준다. (왼쪽으로 흐르는 효과)
					for (int i = 0; i < fieldList.size(); i++) {

						Field tempField = fieldList.get(i); // 임시 변수에 리스트 안에 있는 개별 발판을 불러오자

						if (tempField.getX() < -90) { // 발판의 x좌표가 -90 미만이면 해당 발판을 제거한다.(최적화)

							fieldList.remove(tempField);

						} else {

							tempField.setX(tempField.getX() - gameSpeed); // 위 조건에 해당이 안되면 x좌표를 줄이자

						}
					}

					// 젤리위치를 -4 씩 해준다.
					for (int i = 0; i < jellyList.size(); i++) {

						Jelly tempJelly = jellyList.get(i); // 임시 변수에 리스트 안에 있는 개별 젤리를 불러오자

						if (tempJelly.getX() < -90) { // 젤리의 x 좌표가 -90 미만이면 해당 젤리를 제거한다.(최적화)

							fieldList.remove(tempJelly);

						} else {

							tempJelly.setX(tempJelly.getX() - gameSpeed); // 위 조건에 해당이 안되면 x좌표를 줄이자
							if (tempJelly.getImage() == jellyEffectIc.getImage() && tempJelly.getAlpha() > 4) {
								tempJelly.setAlpha(tempJelly.getAlpha() - 5);
							}

							// 자석 버프가 켜져 있으면 젤리를 끌어당긴다
							if (magnetActive) {
								int centerX = tempJelly.getX() + tempJelly.getWidth() / 2;
								int centerY = tempJelly.getY() + tempJelly.getHeight() / 2;
								int cookieCenterX = c1.getX() + c1.getWidth() / 2;
								int cookieCenterY = c1.getY() + c1.getHeight() / 2;
								double distance = Math
										.sqrt(Math.pow(centerX - cookieCenterX, 2) + Math.pow(centerY - cookieCenterY, 2));
								if (distance < magnetRadius && distance > 0) {
									int pull = 4;
									if (centerX > cookieCenterX) {
										tempJelly.setX(tempJelly.getX() - pull);
									} else {
										tempJelly.setX(tempJelly.getX() + pull);
									}
									if (centerY > cookieCenterY) {
										tempJelly.setY(tempJelly.getY() - pull);
									} else {
										tempJelly.setY(tempJelly.getY() + pull);
									}
								}
							}

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if ( // 캐릭터의 범위 안에 젤리가 있으면 아이템을 먹는다.
							c1.getImage() != slideIc.getImage()
									&& tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
									&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
									&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY()
									&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
									&& tempJelly.getImage() != jellyEffectIc.getImage()) {

								if (tempJelly.getImage() == jellyHPIc.getImage()) {
									if ((c1.getHealth() + 100) > 1000) {
										c1.setHealth(maxHealth);
									} else {
										c1.setHealth(c1.getHealth() + 100);
									}
								}
								tempJelly.setImage(jellyEffectIc.getImage()); // 젤리의 이미지를 이펙트로 바꾼다
								int plusScore = tempJelly.getScore();
								if (doubleScoreActive) {
									plusScore = plusScore * 2;
								}
								resultScore = resultScore + plusScore; // 총점수에 젤리 점수를 더한다
								totalJelly++;
								updateProgressJelly();

							} else if ( // 슬라이딩 하는 캐릭터의 범위 안에 젤리가 있으면 아이템을 먹는다.
							c1.getImage() == slideIc.getImage()
									&& tempJelly.getX() + tempJelly.getWidth() * 20 / 100 >= c1.getX()
									&& tempJelly.getX() + tempJelly.getWidth() * 80 / 100 <= face
									&& tempJelly.getY() + tempJelly.getWidth() * 20 / 100 >= c1.getY()
											+ c1.getHeight() * 1 / 3
									&& tempJelly.getY() + tempJelly.getWidth() * 80 / 100 <= foot
									&& tempJelly.getImage() != jellyEffectIc.getImage()) {

								if (tempJelly.getImage() == jellyHPIc.getImage()) {
									if ((c1.getHealth() + 100) > 1000) {
										c1.setHealth(maxHealth);
									} else {
										c1.setHealth(c1.getHealth() + 100);
									}
								}
								tempJelly.setImage(jellyEffectIc.getImage()); // 젤리의 이미지를 이펙트로 바꾼다
								int plusScore = tempJelly.getScore();
								if (doubleScoreActive) {
									plusScore = plusScore * 2;
								}
								resultScore = resultScore + plusScore; // 총점수에 젤리 점수를 더한다
								totalJelly++;
								updateProgressJelly();

							}
						}
					}

					// 특수 발판 이동 및 처리
					for (int i = 0; i < specialPlatforms.size(); i++) {
						SpecialPlatform sp = specialPlatforms.get(i);
						if (sp.getX() < -120) {
							specialPlatforms.remove(sp);
							continue;
						}
						// 이동
						sp.setX(sp.getX() - gameSpeed + sp.getVx());
						sp.setY(sp.getY() + sp.getVy());

						// 간단한 상하 왕복
						if (sp.getType() == PlatformType.MOVING) {
							if (sp.getY() < 120 || sp.getY() > 320) {
								sp.setVy(-sp.getVy());
							}
						}

						// 붕괴: 밟히면 일정 시간 후 삭제
						if (sp.getType() == PlatformType.COLLAPSING && sp.isTriggered()) {
							if (Util.getTime() - sp.getCollapseStart() > 600) {
								specialPlatforms.remove(sp);
								continue;
							}
						}

						// 낙하: 밟히면 아래로 떨어짐
						if (sp.getType() == PlatformType.FALLING && sp.isTriggered()) {
							sp.setVy(sp.getVy() + 1);
						}
					}

					// 버프/디버프 아이템 이동 및 획득 처리
					for (int i = 0; i < buffItemList.size(); i++) {
						BuffItem item = buffItemList.get(i);
						if (item.getX() < -60) {
							buffItemList.remove(item);
							continue;
						}
						item.setX(item.getX() - gameSpeed);

						face = c1.getX() + c1.getWidth();
						foot = c1.getY() + c1.getHeight();
						if (item.getX() + item.getWidth() >= c1.getX() && item.getX() <= face
								&& item.getY() + item.getHeight() >= c1.getY()
								&& item.getY() <= foot) {
							applyBuffFromItem(item.getType());
							buffItemList.remove(item);
						}
					}

					// 적 이동 및 충돌
					for (int i = 0; i < enemyList.size(); i++) {
						Enemy enemy = enemyList.get(i);
						if (enemy.getX() < -120) {
							enemyList.remove(enemy);
							continue;
						}

						if (enemy.getType() == EnemyType.CHASER) {
							if (enemy.getY() < c1.getY()) {
								enemy.setY(enemy.getY() + 1);
							} else if (enemy.getY() > c1.getY()) {
								enemy.setY(enemy.getY() - 1);
							}
						} else if (enemy.getType() == EnemyType.VERTICAL) {
							if (enemy.getY() < 120 || enemy.getY() > 340) {
								enemy.setVy(-enemy.getVy());
							}
							enemy.setY(enemy.getY() + enemy.getVy());
						} else if (enemy.getType() == EnemyType.SHOOTER) {
							if (Util.getTime() - enemy.getLastFire() > 1500) {
								projectileList
										.add(new Projectile(enemy.getX(), enemy.getY() + enemy.getHeight() / 2, 12, 12, -8, 0));
								enemy.setLastFire(Util.getTime());
							}
						}

						enemy.setX(enemy.getX() - gameSpeed - 1);

						// 충돌 체크
						if (!c1.isInvincible() && enemy.getX() < face && enemy.getX() + enemy.getWidth() > c1.getX()
								&& enemy.getY() < foot && enemy.getY() + enemy.getHeight() > c1.getY()) {
							if (giantActive) {
								enemyList.remove(enemy);
								resultScore += 200;
							} else if (shieldActive) {
								shieldActive = false;
								buffEndTime.put(BuffType.SHIELD, 0L);
								enemyList.remove(enemy);
							} else {
								hit();
							}
						}
					}

					// 투사체 이동 및 충돌
					for (int i = 0; i < projectileList.size(); i++) {
						Projectile p = projectileList.get(i);
						p.setX(p.getX() + p.getVx());
						p.setY(p.getY() + p.getVy());
						if (p.getX() < -40 || p.getX() > 900) {
							projectileList.remove(p);
							continue;
						}
						if (!c1.isInvincible() && p.getX() < face && p.getX() + p.getWidth() > c1.getX()
								&& p.getY() < foot && p.getY() + p.getHeight() > c1.getY()) {
							if (shieldActive) {
								shieldActive = false;
								buffEndTime.put(BuffType.SHIELD, 0L);
							} else {
								hit();
							}
							projectileList.remove(p);
						}
					}

					// 장애물위치를 - 4 씩 해준다.
					for (int i = 0; i < tacleList.size(); i++) {

						Tacle tempTacle = tacleList.get(i); // 임시 변수에 리스트 안에 있는 개별 장애물을 불러오자

						if (tempTacle.getX() < -90) {

							fieldList.remove(tempTacle); // 장애물의 x 좌표가 -90 미만이면 해당 젤리를 제거한다.(최적화)

						} else {

							tempTacle.setX(tempTacle.getX() - gameSpeed); // 위 조건에 해당이 안되면 x좌표를 줄이자

							face = c1.getX() + c1.getWidth(); // 캐릭터 정면 위치 재스캔
							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if ( // 무적상태가 아니고 슬라이드 중이 아니며 캐릭터의 범위 안에 장애물이 있으면 부딛힌다
							!c1.isInvincible() && c1.getImage() != slideIc.getImage()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 >= c1.getX()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 <= face
									&& tempTacle.getY() + tempTacle.getHeight() / 2 >= c1.getY()
									&& tempTacle.getY() + tempTacle.getHeight() / 2 <= foot) {

								if (giantActive) {
									tacleList.remove(tempTacle); // 거대화 상태이면 장애물을 파괴
									resultScore += 200;
								} else if (shieldActive) {
									shieldActive = false;
									buffEndTime.put(BuffType.SHIELD, 0L);
								} else {
									hit(); // 피격 + 무적 쓰레드 메서드
								}

							} else if ( // 슬라이딩 아닐시 공중장애물
							!c1.isInvincible() && c1.getImage() != slideIc.getImage()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 >= c1.getX()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 <= face
									&& tempTacle.getY() <= c1.getY()
									&& tempTacle.getY() + tempTacle.getHeight() * 95 / 100 > c1.getY()) {

								if (giantActive) {
									tacleList.remove(tempTacle);
									resultScore += 200;
								} else if (shieldActive) {
									shieldActive = false;
									buffEndTime.put(BuffType.SHIELD, 0L);
								} else {
									hit(); // 피격 + 무적 쓰레드 메서드
								}

							} else if ( // 무적상태가 아니고 슬라이드 중이며 캐릭터의 범위 안에 장애물이 있으면 부딛힌다
							!c1.isInvincible() && c1.getImage() == slideIc.getImage()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 >= c1.getX()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 <= face
									&& tempTacle.getY() + tempTacle.getHeight() / 2 >= c1.getY()
											+ c1.getHeight() * 2 / 3
									&& tempTacle.getY() + tempTacle.getHeight() / 2 <= foot) {

								if (giantActive) {
									tacleList.remove(tempTacle);
									resultScore += 200;
								} else if (shieldActive) {
									shieldActive = false;
									buffEndTime.put(BuffType.SHIELD, 0L);
								} else {
									hit(); // 피격 + 무적 쓰레드 메서드
								}

							} else if ( // 슬라이딩시 공중장애물
							!c1.isInvincible() && c1.getImage() == slideIc.getImage()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 >= c1.getX()
									&& tempTacle.getX() + tempTacle.getWidth() / 2 <= face
									&& tempTacle.getY() < c1.getY() && tempTacle.getY()
											+ tempTacle.getHeight() * 95 / 100 > c1.getY() + c1.getHeight() * 2 / 3) {

								if (giantActive) {
									tacleList.remove(tempTacle);
									resultScore += 200;
								} else if (shieldActive) {
									shieldActive = false;
									buffEndTime.put(BuffType.SHIELD, 0L);
								} else {
									hit(); // 피격 + 무적 쓰레드 메서드
								}
							}
						}
					}

					// 쿠키가 밟을 발판을 계산하는 코드
					int tempField; // 발판위치를 계속 스캔하는 지역변수
					int tempNowField; // 캐릭터와 발판의 높이에 따라 저장되는 지역변수, 결과를 nowField에 저장한다

					// 쿠키가 무적상태라면 낙사 하지 않기 때문에 400으로 세팅 / 무적이 아니라면 2000(낙사지점);
					if (c1.isInvincible()) {
						tempNowField = 400;
					} else {
						tempNowField = 2000;
					}

					for (int i = 0; i < fieldList.size(); i++) { // 발판의 개수만큼 반복

						int tempX = fieldList.get(i).getX(); // 발판의 x값

						if (tempX > c1.getX() - 60 && tempX <= face) { // 발판이 캐릭 범위 안이라면

							tempField = fieldList.get(i).getY(); // 발판의 y값을 tempField에 저장한다

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							// 발판위치가 tempNowField보다 높고, 발바닥 보다 아래 있다면
							// 즉, 캐릭터 발 아래에 제일 높이 있는 발판이라면 tempNowField에 저장한다.
							if (tempField < tempNowField && tempField >= foot) {

								tempNowField = tempField;

							}
						}
					}

					// 특수 발판도 밟을 수 있도록 스캔
					for (int i = 0; i < specialPlatforms.size(); i++) {
						SpecialPlatform sp = specialPlatforms.get(i);
						int tempX = sp.getX();
						if (tempX > c1.getX() - 60 && tempX <= face) {
							tempField = sp.getY();
							foot = c1.getY() + c1.getHeight();
							if (tempField < tempNowField && tempField >= foot) {
								tempNowField = tempField;
								// 특수 발판 처리: 낙하/붕괴 트리거, 점프패드/레일 효과
								if (!sp.isTriggered()) {
									if (sp.getType() == PlatformType.FALLING || sp.getType() == PlatformType.COLLAPSING) {
										sp.setTriggered(true);
										sp.setCollapseStart(Util.getTime());
									}
								}
								if (sp.getType() == PlatformType.JUMPPAD) {
									// 즉시 점프 상승
									c1.setY(c1.getY() - 15);
									c1.setJump(true);
									c1.setCountJump(1);
								} else if (sp.getType() == PlatformType.RAIL) {
									downKeyOn = true;
									c1.setImage(slideIc.getImage());
								}
							}
						}
					}

					nowField = tempNowField; // 결과를 nowField에 업데이트 한다.

					if (escKeyOn) { // esc키를 누르면 게임이 멈춘다
						while (escKeyOn) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	// 부딛혔을 때 일어나는 상태를 담당하는 메서드
	private void hit() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				c1.setInvincible(true); // 쿠키를 무적상태로 전환

				System.out.println("피격무적시작");

				SoundManager.playEffect("sound/dameg.wav", settings.getSfxVolume());

				redScreen = true; // 피격 붉은 이펙트 시작

				c1.setHealth(c1.getHealth() - 100); // 쿠키의 체력을 100 깎는다

				c1.setImage(hitIc.getImage()); // 쿠키를 부딛힌 모션으로 변경

				c1.setAlpha(80); // 쿠키의 투명도를 80으로 변경

				try { // 0.5초 대기
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				redScreen = false; // 피격 붉은 이펙트 종료

				try { // 0.5초 대기
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (c1.getImage() == hitIc.getImage()) { // 0.5초 동안 이미지가 바뀌지 않았다면 기본이미지로 변경

					c1.setImage(cookieIc.getImage());

				}

				for (int j = 0; j < 11; j++) { // 2.5초간 캐릭터가 깜빡인다. (피격후 무적 상태를 인식)

					if (c1.getAlpha() == 80) { // 이미지의 알파값이 80이면 160으로

						c1.setAlpha(160);

					} else { // 아니면 80으로

						c1.setAlpha(80);

					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				c1.setAlpha(255); // 쿠키의 투명도를 정상으로 변경

				c1.setInvincible(false);
				System.out.println("피격무적종료");
			}
		}).start();
	}

	// 낙하 메서드
	private void fall() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

					// 발바닥이 발판보다 위에 있으면 작동
					if (!escKeyOn // 일시중지가 발동 안됐을 때
							&& foot < nowField // 공중에 있으며
							&& !c1.isJump() // 점프 중이 아니며
							&& !c1.isFall()) { // 떨어지는 중이 아닐 때

						c1.setFall(true); // 떨어지는 중으로 전환
						System.out.println("낙하");

						if (c1.getCountJump() == 2) { // 더블점프가 끝났을 경우 낙하 이미지로 변경
							c1.setImage(fallIc.getImage());
						}

						long t1 = Util.getTime(); // 현재시간을 가져온다
						long t2;
						int set = 1; // 처음 낙하량 (0~10) 까지 테스트해보자

						while (foot < nowField) { // 발이 발판에 닿기 전까지 반복

							t2 = Util.getTime() - t1; // 지금 시간에서 t1을 뺀다

							int fallY = set + (int) ((t2) / 40); // 낙하량을 늘린다.

							foot = c1.getY() + c1.getHeight(); // 캐릭터 발 위치 재스캔

							if (foot + fallY >= nowField) { // 발바닥+낙하량 위치가 발판보다 낮다면 낙하량을 조정한다.
								fallY = nowField - foot;
							}

							c1.setY(c1.getY() + fallY); // Y좌표에 낙하량을 더한다

							if (c1.isJump()) { // 떨어지다가 점프를 하면 낙하중지
								break;
							}

							if (escKeyOn) {
								long tempT1 = Util.getTime();
								long tempT2 = 0;
								while (escKeyOn) {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								tempT2 = Util.getTime() - tempT1;
								t1 = t1 + tempT2;
							}

							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
						c1.setFall(false);

						if (downKeyOn // 다운키를 누른상태고
								&& !c1.isJump() // 점프 상태가 아니고
								&& !c1.isFall() // 낙하 상태가 아니고
								&& c1.getImage() != slideIc.getImage()) { // 쿠키 이미지가 슬라이드 이미지가 아닐 경우

							c1.setImage(slideIc.getImage()); // 쿠키 이미지를 슬라이드로 변경

						} else if (!downKeyOn // 다운키를 누른상태가 아니고
								&& !c1.isJump() // 점프 상태가 아니고
								&& !c1.isFall() // 낙하 상태가 아니고
								&& c1.getImage() != cookieIc.getImage()) { // 쿠키 이미지가 기본 이미지가 아닐 경우

							c1.setImage(cookieIc.getImage());
						}

						if (!c1.isJump()) { // 발이 땅에 닿고 점프 중이 아닐 때 더블점프 카운트를 0으로 변경
							c1.setCountJump(0);
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// 점프 메서드
	private void jump() {
		c1.setCountJump(c1.getCountJump() + 1); // 점프 횟수 증가

		if (c1.getCountJump() == 1) {
			c1.setImage(jumpIc.getImage());
			playerState = PlayerState.JUMP;
		} else if (c1.getCountJump() == 2) {
			c1.setImage(doubleJumpIc.getImage());
			playerState = PlayerState.DOUBLE_JUMP;
		}

		c1.setJump(true);
		jumpVelocity = jumpPowerBase;

		totalJump++;
		updateProgressJump();
	}

	private void backFadeOut() {
		for (int i = 0; i < 256; i += 2) {
			backFade = new Color(0, 0, 0, i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void backFadeIn() {
		for (int i = 255; i >= 0; i -= 2) {
			backFade = new Color(0, 0, 0, i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// HUD에 표시할 스테이지 라벨을 계산한다
	private String getStageLabel() {
		if (mapLengthList == null || mapLengthList.size() < 3) {
			return "1";
		}
		int threshold1 = mapLengthList.get(0) * 40 + 800;
		int threshold2 = mapLengthList.get(1) * 40 + 800;
		int threshold3 = mapLengthList.get(2) * 40 + 800;

		if (mapLength > threshold3) {
			return "4";
		} else if (mapLength > threshold2) {
			return "3";
		} else if (mapLength > threshold1) {
			return "2";
		}
		return "1";
	}

	// 진행 거리를 단순 변환해 반환한다
	private int getDistanceMeters() {
		return mapLength / 10;
	}

	// 버프 상태를 HUD에 표시한다
	private void drawBuffStatus(Graphics2D g2) {
		int startX = this.getWidth() - 190;
		int startY = 16;
		int barWidth = 160;
		int barHeight = 16;
		int idx = 0;

		for (BuffType type : BuffType.values()) {
			if (isBuffActive(type)) {
				long remaining = Math.max(0, buffEndTime.get(type) - Util.getTime());
				long duration = Math.max(1, buffDuration.get(type));
				float ratio = Math.min(1f, Math.max(0f, remaining / (float) duration));
				int y = startY + idx * (barHeight + 6);

				g2.setColor(new Color(0, 0, 0, 140));
				g2.fill(new RoundRectangle2D.Double(startX, y, barWidth, barHeight, 10, 10));

				g2.setColor(getBuffColor(type));
				g2.fill(new RoundRectangle2D.Double(startX, y, (int) (barWidth * ratio), barHeight, 10, 10));

				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 12));
				g2.drawString(shortBuffLabel(type) + " " + (remaining / 1000) + "s", startX + 8, y + 12);

				idx++;
			}
		}
	}

	// 버프 아이템 색상
	private Color getBuffColor(BuffType type) {
		switch (type) {
		case MAGNET:
			return new Color(0, 170, 255, 200);
		case SHIELD:
			return new Color(0, 220, 150, 200);
		case SPEED:
			return new Color(255, 140, 0, 200);
		case GIANT:
			return new Color(255, 80, 0, 200);
		case DOUBLE_SCORE:
			return new Color(255, 215, 0, 200);
		case SLOW:
			return new Color(160, 120, 255, 200);
		case REVERSE_GRAVITY:
			return new Color(120, 120, 255, 200);
		default:
			return new Color(255, 255, 255, 200);
		}
	}

	// 버프 레이블
	private String shortBuffLabel(BuffType type) {
		switch (type) {
		case MAGNET:
			return "MAG";
		case SHIELD:
			return "SHD";
		case SPEED:
			return "SPD";
		case GIANT:
			return "BIG";
		case DOUBLE_SCORE:
			return "x2";
		case SLOW:
			return "SLOW";
		case REVERSE_GRAVITY:
			return "REV";
		default:
			return "BUF";
		}
	}

	// 특수 발판 레이블
	private String shortPlatformLabel(PlatformType type) {
		switch (type) {
		case MOVING:
			return "MV";
		case FALLING:
			return "FL";
		case COLLAPSING:
			return "CL";
		case JUMPPAD:
			return "JP";
		case RAIL:
			return "RL";
		default:
			return "PF";
		}
	}

	private boolean isBuffActive(BuffType type) {
		Long end = buffEndTime.get(type);
		return end != null && end > Util.getTime();
	}

	private void activateBuff(BuffType type, long durationMs) {
		long now = Util.getTime();
		buffEndTime.put(type, now + durationMs);
		buffDuration.put(type, durationMs);

		switch (type) {
		case MAGNET:
			magnetActive = true;
			break;
		case SHIELD:
			shieldActive = true;
			break;
		case SPEED:
			speedActive = true;
			break;
		case GIANT:
			giantActive = true;
			break;
		case DOUBLE_SCORE:
			doubleScoreActive = true;
			break;
		case SLOW:
			slowActive = true;
			break;
		default:
			break;
		}
	}

	private void refreshBuffFlags() {
		long now = Util.getTime();
		magnetActive = buffEndTime.get(BuffType.MAGNET) > now;
		shieldActive = buffEndTime.get(BuffType.SHIELD) > now;
		speedActive = buffEndTime.get(BuffType.SPEED) > now;
		giantActive = buffEndTime.get(BuffType.GIANT) > now;
		doubleScoreActive = buffEndTime.get(BuffType.DOUBLE_SCORE) > now;
		slowActive = buffEndTime.get(BuffType.SLOW) > now;
	}

	private int calcGameSpeed() {
		int speed = baseGameSpeed;
		if (speedActive) {
			speed += speedBonus;
		}
		if (slowActive) {
			speed -= slowPenalty;
		}
		if (speed < 2) {
			speed = 2;
		}
		return speed;
	}

	private void spawnBuffItem() {
		BuffType[] types = { BuffType.MAGNET, BuffType.SHIELD, BuffType.SPEED, BuffType.GIANT, BuffType.DOUBLE_SCORE,
				BuffType.SLOW };
		int idx = (int) (Math.random() * types.length);
		BuffType type = types[idx];
		int x = 900;
		int y = 120 + (int) (Math.random() * 250);
		int size = 28;
		buffItemList.add(new BuffItem(type, x, y, size, size));
	}

	private void applyBuffFromItem(BuffType type) {
		long dur = gameConfig.buffDuration.getOrDefault(type, 7000L);
		activateBuff(type, dur);
		switch (type) {
		case MAGNET:
			SoundManager.playEffect("sound/magnet.wav", settings.getSfxVolume());
			break;
		case SHIELD:
			SoundManager.playEffect("sound/shield.wav", settings.getSfxVolume());
			break;
		case SPEED:
			SoundManager.playEffect("sound/speedup.wav", settings.getSfxVolume());
			break;
		case GIANT:
			SoundManager.playEffect("sound/max.wav", settings.getSfxVolume());
			break;
		case DOUBLE_SCORE:
			SoundManager.playEffect("sound/speedup.wav", settings.getSfxVolume());
			break;
		case SLOW:
			SoundManager.playEffect("sound/slow.wav", settings.getSfxVolume());
			break;
		default:
			break;
		}
	}

	// 업적/미션 진행 업데이트
	private void updateProgressJelly() {
		if (saveData == null) {
			return;
		}
		saveData.getAchievements().forEach(a -> {
			if (a.getId().equals("a2")) {
				a.addProgress(1);
			}
		});
		saveData.getMissions().forEach(m -> {
			if (m.getId().equals("m2")) {
				m.addProgress(1);
			}
		});
	}

	private void updateProgressJump() {
		if (saveData == null) {
			return;
		}
		saveData.getAchievements().forEach(a -> {
			if (a.getId().equals("a1")) {
				a.addProgress(1);
			}
		});
	}

	private void updateProgressDistance() {
		if (saveData == null) {
			return;
		}
		saveData.getMissions().forEach(m -> {
			if (m.getId().equals("m1")) {
				m.addProgress(gameSpeed);
			}
		});
	}

	private void updateLeaderboardAndSave() {
		if (saveData == null) {
			return;
		}
		saveData.getLeaderboard().add(
				new progress.LeaderboardEntry(saveData.getProfile().getName(), resultScore, java.time.LocalDateTime.now()));
		saveData.getLeaderboard().sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
		if (saveData.getLeaderboard().size() > 10) {
			saveData.getLeaderboard().subList(10, saveData.getLeaderboard().size()).clear();
		}
		int gainedCoins = resultScore / 10;
		saveData.getProfile().addCoins(gainedCoins);
		progress.SaveManager.save("slot1", saveData);
	}

	// 특수 발판 스폰 (데모용)
	private void spawnSpecialPlatform() {
		int x = 900;
		int y = 200 + (int) (Math.random() * 200);
		int w = 80;
		int h = 20;
		double r = Math.random();
		PlatformType type;
		if (r < 0.2) {
			type = PlatformType.MOVING;
		} else if (r < 0.4) {
			type = PlatformType.FALLING;
		} else if (r < 0.6) {
			type = PlatformType.COLLAPSING;
		} else if (r < 0.8) {
			type = PlatformType.JUMPPAD;
		} else {
			type = PlatformType.RAIL;
		}
		int vx = 0;
		int vy = 0;
		Color c = new Color(200, 200, 200, 220);
		if (type == PlatformType.MOVING) {
			vy = (Math.random() < 0.5) ? 1 : -1;
			c = new Color(90, 200, 255, 230);
		} else if (type == PlatformType.FALLING) {
			c = new Color(255, 150, 90, 230);
		} else if (type == PlatformType.COLLAPSING) {
			c = new Color(255, 90, 90, 230);
		} else if (type == PlatformType.JUMPPAD) {
			c = new Color(120, 255, 140, 230);
		} else if (type == PlatformType.RAIL) {
			c = new Color(190, 120, 255, 230);
		}
		specialPlatforms.add(new SpecialPlatform(x, y, w, h, type, vx, vy, c));
	}

	// 적 스폰 (데모용)
	private void spawnEnemy() {
		int x = 900;
		int y = 180 + (int) (Math.random() * 200);
		double r = Math.random();
		EnemyType type;
		if (r < 0.33) {
			type = EnemyType.CHASER;
		} else if (r < 0.66) {
			type = EnemyType.VERTICAL;
		} else {
			type = EnemyType.SHOOTER;
		}
		int vx = 0;
		int vy = (type == EnemyType.VERTICAL) ? 2 : 0;
		enemyList.add(new Enemy(x, y, 36, 36, vx, vy, type));
	}

}
