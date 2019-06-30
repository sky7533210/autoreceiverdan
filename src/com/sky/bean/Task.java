package com.sky.bean;

public class Task {
	private String taskid;
	private String labels;
	private int leftnum;
	private float babyprice;
	private float smallfee;
	private int trueid;
	private int cando;
	private String candoreason;
	
	public Task() {
		super();
	}
	
	public Task(String taskid, String labels, int leftnum, float babyprice, float smallfee, int trueid, int cando,
			String candoreason) {
		super();
		this.taskid = taskid;
		this.labels = labels;
		this.leftnum = leftnum;
		this.babyprice = babyprice;
		this.smallfee = smallfee;
		this.trueid = trueid;
		this.cando = cando;
		this.candoreason = candoreason;
	}

	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	public int getLeftnum() {
		return leftnum;
	}
	public void setLeftnum(int leftnum) {
		this.leftnum = leftnum;
	}
	public float getBabyprice() {
		return babyprice;
	}
	public void setBabyprice(float babyprice) {
		this.babyprice = babyprice;
	}
	public float getSmallfee() {
		return smallfee;
	}
	public void setSmallfee(float smallfee) {
		this.smallfee = smallfee;
	}
	public int getTrueid() {
		return trueid;
	}
	public void setTrueid(int trueid) {
		this.trueid = trueid;
	}
	public int getCando() {
		return cando;
	}
	public void setCando(int cando) {
		this.cando = cando;
	}
	public String getCandoreason() {
		return candoreason;
	}
	public void setCandoreason(String candoreason) {
		this.candoreason = candoreason;
	}

}
