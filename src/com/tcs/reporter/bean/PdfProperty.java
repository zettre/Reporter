package com.tcs.reporter.bean;

public class PdfProperty {
	private float leftMargin;
	private float rightMargin;
	private float topMargin;
	private float bottomMargin;
	private int fontSize;
	private int pageHeight;
	private int pageWidth;
	
	public PdfProperty() {
		super();
	}
	public PdfProperty(float leftMargin, float rightMargin, float topMargin, float bottomMargin, int fontSize,
			int pageHeight, int pageWidth) {
		super();
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
		this.fontSize = fontSize;
		this.pageHeight = pageHeight;
		this.pageWidth = pageWidth;
	}
	public float getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(float leftMargin) {
		this.leftMargin = leftMargin;
	}
	public float getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(float rightMargin) {
		this.rightMargin = rightMargin;
	}
	public float getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(float topMargin) {
		this.topMargin = topMargin;
	}
	public float getBottomMargin() {
		return bottomMargin;
	}
	public void setBottomMargin(float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public int getPageHeight() {
		return pageHeight;
	}
	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}
	public int getPageWidth() {
		return pageWidth;
	}
	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}
	
}
