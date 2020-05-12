package com.hfr.bean;

import java.sql.Timestamp;

public class Detail {
    private String ID;
    private String SOURCE_NAME;
    private String DETAIL_LINK;
    private String DETAIL_TITLE;
    private String DETAIL_CONTENT;
    private String PAGE_TIME;
    private Timestamp CREATE_TIME;
    private String LIST_TITLE;
    private String CREATE_BY;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSOURCE_NAME() {
        return SOURCE_NAME;
    }

    public void setSOURCE_NAME(String SOURCE_NAME) {
        this.SOURCE_NAME = SOURCE_NAME;
    }

    public String getDETAIL_LINK() {
        return DETAIL_LINK;
    }

    public void setDETAIL_LINK(String DETAIL_LINK) {
        this.DETAIL_LINK = DETAIL_LINK;
    }

    public String getDETAIL_TITLE() {
        return DETAIL_TITLE;
    }

    public void setDETAIL_TITLE(String DETAIL_TITLE) {
        this.DETAIL_TITLE = DETAIL_TITLE;
    }

    public String getDETAIL_CONTENT() {
        return DETAIL_CONTENT;
    }

    public void setDETAIL_CONTENT(String DETAIL_CONTENT) {
        this.DETAIL_CONTENT = DETAIL_CONTENT;
    }

    public String getPAGE_TIME() {
        return PAGE_TIME;
    }

    public void setPAGE_TIME(String PAGE_TIME) {
        this.PAGE_TIME = PAGE_TIME;
    }

    public Timestamp getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(Timestamp CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public String getLIST_TITLE() {
        return LIST_TITLE;
    }

    public void setLIST_TITLE(String LIST_TITLE) {
        this.LIST_TITLE = LIST_TITLE;
    }

    public String getCREATE_BY() {
        return CREATE_BY;
    }

    public void setCREATE_BY(String CREATE_BY) {
        this.CREATE_BY = CREATE_BY;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "ID='" + ID + '\'' +
                ", SOURCE_NAME='" + SOURCE_NAME + '\'' +
                ", DETAIL_LINK='" + DETAIL_LINK + '\'' +
                ", DETAIL_TITLE='" + DETAIL_TITLE + '\'' +
                ", DETAIL_CONTENT='" + DETAIL_CONTENT + '\'' +
                ", PAGE_TIME='" + PAGE_TIME + '\'' +
                ", CREATE_TIME=" + CREATE_TIME +
                ", LIST_TITLE='" + LIST_TITLE + '\'' +
                ", CREATE_BY='" + CREATE_BY + '\'' +
                '}';
    }
}
