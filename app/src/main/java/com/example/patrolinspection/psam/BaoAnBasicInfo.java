package com.example.patrolinspection.psam;

public class BaoAnBasicInfo {
    private String baoAnID;  //保安员证
    private String id;          //公民身份证号码
    private String baoAnName; //姓名
//  private String usedName;//曾用名
    private String sex;//性别
    private String nation;//民族
    private String barth;//出生时期
//  private String hjsx;//户籍省市
//  private String hjxz;//户籍详址
//  private String bloodType;//血型
    private String baoAnGrade;//保安员职业等级
    private String fzjg;//发证机关
    private String fzrq;//发证日期
    private String scfzjg;//首次发证机关
    private String scfarq;//首次发证日期
    /**
     * @return the baoAnID
     */
    public String getBaoAnID() {
        return baoAnID;
    }
    /**
     * @param baoAnID the baoAnID to set
     */
    public void setBaoAnID(String baoAnID) {
        this.baoAnID = baoAnID;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the baoAnName
     */
    public String getBaoAnName() {
        return baoAnName;
    }
    /**
     * @param baoAnName the baoAnName to set
     */
    public void setBaoAnName(String baoAnName) {
        this.baoAnName = baoAnName;
    }
    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }
    /**
     * @param sex the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }
    /**
     * @return the nation
     */
    public String getNation() {
        return nation;
    }
    /**
     * @param nation the nation to set
     */
    public void setNation(String nation) {
        this.nation = nation;
    }
    /**
     * @return the barth
     */
    public String getBarth() {
        return barth;
    }
    /**
     * @param barth the barth to set
     */
    public void setBarth(String barth) {
        this.barth = barth;
    }
    /**
     * @return the baoAnGrade
     */
    public String getBaoAnGrade() {
        return baoAnGrade;
    }
    /**
     * @param baoAnGrade the baoAnGrade to set
     */
    public void setBaoAnGrade(String baoAnGrade) {
        this.baoAnGrade = baoAnGrade;
    }
    /**
     * @return the fzjg
     */
    public String getFzjg() {
        return fzjg;
    }
    /**
     * @param fzjg the fzjg to set
     */
    public void setFzjg(String fzjg) {
        this.fzjg = fzjg;
    }
    /**
     * @return the fzrq
     */
    public String getFzrq() {
        return fzrq;
    }
    /**
     * @param fzrq the fzrq to set
     */
    public void setFzrq(String fzrq) {
        this.fzrq = fzrq;
    }
    /**
     * @return the scfzjg
     */
    public String getScfzjg() {
        return scfzjg;
    }
    /**
     * @param scfzjg the scfzjg to set
     */
    public void setScfzjg(String scfzjg) {
        this.scfzjg = scfzjg;
    }
    /**
     * @return the scfarq
     */
    public String getScfarq() {
        return scfarq;
    }
    /**
     * @param scfarq the scfarq to set
     */
    public void setScfarq(String scfarq) {
        this.scfarq = scfarq;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BaoAnBasicInfo [baoAnID=" + baoAnID + ", id=" + id
                + ", baoAnName=" + baoAnName + ", sex=" + sex + ", nation="
                + nation + ", barth=" + barth + ", baoAnGrade=" + baoAnGrade
                + ", fzjg=" + fzjg + ", fzrq=" + fzrq + ", scfzjg=" + scfzjg
                + ", scfarq=" + scfarq + "]";
    }

}
