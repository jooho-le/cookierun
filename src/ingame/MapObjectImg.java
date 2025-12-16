package ingame;

import javax.swing.ImageIcon;

// Lombok 제거: 수동 getter/setter 제공
public class MapObjectImg {

	// 배경 이미지
	private ImageIcon backIc; // 제일 뒷 배경
	private ImageIcon secondBackIc; // 2번째 배경

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

	public MapObjectImg() {
	}

	public MapObjectImg(ImageIcon backIc, ImageIcon secondBackIc, ImageIcon jelly1Ic, ImageIcon jelly2Ic,
			ImageIcon jelly3Ic, ImageIcon jellyHPIc, ImageIcon jellyEffectIc, ImageIcon field1Ic, ImageIcon field2Ic,
			ImageIcon tacle10Ic, ImageIcon tacle20Ic, ImageIcon tacle30Ic, ImageIcon tacle40Ic) {
		this.backIc = backIc;
		this.secondBackIc = secondBackIc;
		this.jelly1Ic = jelly1Ic;
		this.jelly2Ic = jelly2Ic;
		this.jelly3Ic = jelly3Ic;
		this.jellyHPIc = jellyHPIc;
		this.jellyEffectIc = jellyEffectIc;
		this.field1Ic = field1Ic;
		this.field2Ic = field2Ic;
		this.tacle10Ic = tacle10Ic;
		this.tacle20Ic = tacle20Ic;
		this.tacle30Ic = tacle30Ic;
		this.tacle40Ic = tacle40Ic;
	}

	public ImageIcon getBackIc() {
		return backIc;
	}

	public void setBackIc(ImageIcon backIc) {
		this.backIc = backIc;
	}

	public ImageIcon getSecondBackIc() {
		return secondBackIc;
	}

	public void setSecondBackIc(ImageIcon secondBackIc) {
		this.secondBackIc = secondBackIc;
	}

	public ImageIcon getJelly1Ic() {
		return jelly1Ic;
	}

	public void setJelly1Ic(ImageIcon jelly1Ic) {
		this.jelly1Ic = jelly1Ic;
	}

	public ImageIcon getJelly2Ic() {
		return jelly2Ic;
	}

	public void setJelly2Ic(ImageIcon jelly2Ic) {
		this.jelly2Ic = jelly2Ic;
	}

	public ImageIcon getJelly3Ic() {
		return jelly3Ic;
	}

	public void setJelly3Ic(ImageIcon jelly3Ic) {
		this.jelly3Ic = jelly3Ic;
	}

	public ImageIcon getJellyHPIc() {
		return jellyHPIc;
	}

	public void setJellyHPIc(ImageIcon jellyHPIc) {
		this.jellyHPIc = jellyHPIc;
	}

	public ImageIcon getJellyEffectIc() {
		return jellyEffectIc;
	}

	public void setJellyEffectIc(ImageIcon jellyEffectIc) {
		this.jellyEffectIc = jellyEffectIc;
	}

	public ImageIcon getField1Ic() {
		return field1Ic;
	}

	public void setField1Ic(ImageIcon field1Ic) {
		this.field1Ic = field1Ic;
	}

	public ImageIcon getField2Ic() {
		return field2Ic;
	}

	public void setField2Ic(ImageIcon field2Ic) {
		this.field2Ic = field2Ic;
	}

	public ImageIcon getTacle10Ic() {
		return tacle10Ic;
	}

	public void setTacle10Ic(ImageIcon tacle10Ic) {
		this.tacle10Ic = tacle10Ic;
	}

	public ImageIcon getTacle20Ic() {
		return tacle20Ic;
	}

	public void setTacle20Ic(ImageIcon tacle20Ic) {
		this.tacle20Ic = tacle20Ic;
	}

	public ImageIcon getTacle30Ic() {
		return tacle30Ic;
	}

	public void setTacle30Ic(ImageIcon tacle30Ic) {
		this.tacle30Ic = tacle30Ic;
	}

	public ImageIcon getTacle40Ic() {
		return tacle40Ic;
	}

	public void setTacle40Ic(ImageIcon tacle40Ic) {
		this.tacle40Ic = tacle40Ic;
	}
}
