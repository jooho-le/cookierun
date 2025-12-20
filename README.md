
# 자바 스윙 쿠키런 (개선 버전)

## 실행 환경
- JDK 8+
- Lombok 사용 (IDE/빌드 설정 필요)
- 사운드 파일: `sound/` 폴더에 효과음 필요 (`magnet.wav`, `shield.wav`, `speedup.wav`, `max.wav`, `slow.wav`, `dameg.wav`, `gameover.wav`, `background.wav`는 현재 미사용)

## 주요 변경/기능
- 고정 timestep 게임 루프(Timer) + 레이어 기반 충돌(`engine.Body`, `CollisionLayer`)
- 버프/디버프: 자석, 실드, 속도업, 거대화, 점수 2배, 슬로우 (획득 시 중앙 표시 + 효과음)
- 특수 발판: 이동/낙하/붕괴/점프패드/레일 (접촉 시 중앙 표시)
- 적/투사체: 추적형, 수직 이동형, 발사형 + 투사체 충돌
- HUD/일시정지: 점수/HP/스테이지/거리/속도, 버프 상태바, 컨트롤 힌트, 일시정지 오버레이
- 선택 화면: 캐릭터별 스탯(점프 14/16/18/20, 속도 8/7/6/5, HP 1000), 업그레이드, 미션/업적/리더보드 요약, 슬롯 전환
- 세이브/설정: `save/slotX.dat` 직렬화 저장, 설정(`config/settings.json`), 게임/버프/스폰(`config/game.json`), 업적/미션(`config/achievements.json`, `config/missions.json`)
- 네트워크(표시용): UDP 상태 전송(시퀀스+보간), 파트너 마커 표시
- 디버그/치트: F1 오버레이, F2 무적, F3 로그 오버레이

## 빌드/실행
```
javac -cp "lib/lombok-1.18.8.jar" -d bin $(find src -name "*.java")
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

## 참고
- 초기 블로그 가이드는 원본 README 하단 링크 참고 (기본 동작/페이드/더블버퍼링 등) 

