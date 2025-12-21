# 자바 스윙 쿠키런 (개선 버전)

## 실행 환경
- JDK 8+
- Lombok 사용 (IDE/빌드 설정 필요)
- 사운드 파일: `sound/` 폴더에 효과음 필요 (`magnet.wav`, `shield.wav`, `speedup.wav`, `max.wav`, `slow.wav`, `dameg.wav`, `gameover.wav`)
- BGM은 현재 코드에서 비활성화되어 있음

## 핵심 기능
- 고정 timestep 게임 루프(Timer) + 레이어 기반 충돌(`engine.Body`, `CollisionLayer`)
- 버프/디버프: 자석, 실드, 속도업, 거대화, 점수 2배, 슬로우 (획득 시 중앙 표시 + 효과음)
- 특수 발판: 이동/낙하/붕괴/점프패드/레일 (접촉 시 중앙 표시)
- 적/투사체: 추적형, 수직 이동형, 발사형 + 투사체 충돌
- HUD/일시정지: 점수/HP/스테이지/거리/속도, 버프 상태바, 컨트롤 힌트, 일시정지 오버레이
- 선택 화면: 캐릭터별 스탯(점프 14/16/18/20, 속도 8/7/6/5, HP 1000), 업그레이드/미션/업적/리더보드 요약 표시
- 세이브/설정: `save/slot1.dat` 직렬화 저장, 설정(`config/settings.json`), 게임/버프/스폰(`config/game.json`), 업적/미션(`config/achievements.json`, `config/missions.json`)
- 네트워크(표시용): UDP 상태 전송(시퀀스+보간), 파트너 마커 표시
- 디버그/치트: F1 오버레이, F2 무적, F3 로그 오버레이

## 빌드/실행
```
javac -encoding UTF-8 -cp "lib/lombok-1.18.8.jar" -d bin $(find src -name "*.java")
java -cp "bin:lib/lombok-1.18.8.jar" main.Main   # Windows는 ; 사용
```

## 2인 네트워크 실행 예시(로컬)
```
# 인스턴스 A
java -Dnet.localPort=6000 -Dnet.remotePort=6001 -Dnet.remoteHost=127.0.0.1 -cp "bin:lib/lombok-1.18.8.jar" main.Main
# 인스턴스 B
java -Dnet.localPort=6001 -Dnet.remotePort=6000 -Dnet.remoteHost=127.0.0.1 -cp "bin:lib/lombok-1.18.8.jar" main.Main
```

## 조작
- 점프: Space 또는 W (설정 키맵 따름)
- 슬라이드: Down 또는 S
- 일시정지: Esc (설정 키맵)
- 디버그: F1(오버레이), F2(무적), F3(로그)

## 폴더 구조
- `src/` 소스 코드
- `img/` 이미지 리소스
- `sound/` 효과음 리소스
- `config/` 게임 설정 및 데이터(JSON)
- `save/` 세이브 파일

## 데이터/설정
- `config/settings.json`: 키맵, 볼륨 등 설정
- `config/game.json`: 스폰 확률/버프 지속/HP 소모 등 밸런스 파라미터
- `config/achievements.json`, `config/missions.json`: 업적/미션 데이터

## 네트워크/설정 예시
```
# 로컬 2인 실행(포트만 교차)
java -Dnet.localPort=6000 -Dnet.remotePort=6001 -Dnet.remoteHost=127.0.0.1 -cp "bin:lib/lombok-1.18.8.jar" main.Main
java -Dnet.localPort=6001 -Dnet.remotePort=6000 -Dnet.remoteHost=127.0.0.1 -cp "bin:lib/lombok-1.18.8.jar" main.Main
```
```
// config/settings.json 예시
{
  "jumpKey": "SPACE",
  "slideKey": "DOWN",
  "pauseKey": "ESCAPE",
  "bgmVolume": 30,
  "sfxVolume": 70
}
```
```
// config/game.json 예시
{
  "spawnBuff": 0.015,
  "spawnPlatform": 0.01,
  "spawnEnemy": 0.01,
  "healthDrainPerSec": 2.0,
  "buffDuration": {
    "MAGNET": 7000,
    "SHIELD": 7000,
    "SPEED": 6000,
    "GIANT": 6000,
    "DOUBLE_SCORE": 8000,
    "SLOW": 6000
  }
}
```

## 저장/리더보드
- 현재는 `save/slot1.dat`에 저장됨(슬롯 전환 UI/로직은 미구현)
- 게임 종료 시 리더보드 갱신 및 저장

## 네트워크
- 로컬 2인 표시용(상태 동기화 수준)
- 전송 데이터: 위치/체력/점수/이미지 인덱스/타임스탬프

## 문제 해결
- `unmappable character` 오류가 나면 `javac -encoding UTF-8 ...`로 재빌드하세요.

## 개발 로그/보고서(요약)
- 고정 timestep 게임 루프 도입 및 업데이트/렌더 분리
- 레이어 기반 충돌(AABB)과 상태 머신 일부 적용
- 버프/디버프, 특수 발판, 적/투사체 콘텐츠 확장
- HUD/오버레이 개선(버프 타이머, 디버그/로그)
- 세이브/설정/업적/미션 JSON 로드 및 리더보드 저장
- UDP 표시용 동기화(시퀀스+보간)
- 렌더/충돌 루프 최적화(Body/Font/Color 캐시)

## 참고
- 초기 블로그 가이드는 원본 README 하단 링크 참고 (기본 동작/페이드/더블버퍼링 등)
