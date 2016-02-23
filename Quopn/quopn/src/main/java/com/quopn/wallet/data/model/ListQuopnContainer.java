package com.quopn.wallet.data.model;

public class ListQuopnContainer {

	private QuopnData quopnData1;

	private QuopnData quopnData2;

	private int QUOPN_STATE = 2;// default is 2

	// to indicate which side quopn it is in the grid
	private int flag = 0;
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public QuopnData getQuopnData1() {
		return quopnData1;
	}

	public void setQuopnData1(QuopnData quopnData1) {
		this.quopnData1 = quopnData1;
	}

	public QuopnData getQuopnData2() {
		return quopnData2;
	}

	public void setQuopnData2(QuopnData quopnData2) {
		this.quopnData2 = quopnData2;
	}

	public int getQUOPN_STATE() {
		return QUOPN_STATE;
	}

	public void setQUOPN_STATE(int qUOPN_STATE) {
		QUOPN_STATE = qUOPN_STATE;
	}
	
}
