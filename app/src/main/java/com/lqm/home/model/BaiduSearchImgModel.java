package com.lqm.home.model;

import java.io.Serializable;
import java.util.List;

/**
 * 百度图片搜索
 */

public class BaiduSearchImgModel implements Serializable{

    private String queryEnc;
    private String queryExt;
    private int listNum;
    private int displayNum;
    private String gsm;
    private String bdFmtDispNum;
    private String bdSearchTime;
    private int isNeedAsyncRequest;
    private String bdIsClustered;

    private List<DataBean> data;

    public String getQueryEnc() {
        return queryEnc;
    }

    public void setQueryEnc(String queryEnc) {
        this.queryEnc = queryEnc;
    }

    public String getQueryExt() {
        return queryExt;
    }

    public void setQueryExt(String queryExt) {
        this.queryExt = queryExt;
    }

    public int getListNum() {
        return listNum;
    }

    public void setListNum(int listNum) {
        this.listNum = listNum;
    }

    public int getDisplayNum() {
        return displayNum;
    }

    public void setDisplayNum(int displayNum) {
        this.displayNum = displayNum;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getBdFmtDispNum() {
        return bdFmtDispNum;
    }

    public void setBdFmtDispNum(String bdFmtDispNum) {
        this.bdFmtDispNum = bdFmtDispNum;
    }

    public String getBdSearchTime() {
        return bdSearchTime;
    }

    public void setBdSearchTime(String bdSearchTime) {
        this.bdSearchTime = bdSearchTime;
    }

    public int getIsNeedAsyncRequest() {
        return isNeedAsyncRequest;
    }

    public void setIsNeedAsyncRequest(int isNeedAsyncRequest) {
        this.isNeedAsyncRequest = isNeedAsyncRequest;
    }

    public String getBdIsClustered() {
        return bdIsClustered;
    }

    public void setBdIsClustered(String bdIsClustered) {
        this.bdIsClustered = bdIsClustered;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String thumbURL;
        private String middleURL;
        private String largeTnImageUrl;
        private int hasLarge;
        private String hoverURL;
        private int pageNum;
        private String objURL;
        private String fromURL;
        private String fromURLHost;
        private String currentIndex;
        private int width;
        private int height;
        private String type;
        private String filesize;
        private String bdSrcType;
        private String di;
        private String adid;
        private String is;
        private Object simid_info;
        private Object face_info;
        private Object xiangshi_info;
        private String adPicId;
        private String pi;
        private int bdSetImgNum;
        private String bdImgnewsDate;
        private String fromPageTitle;
        private String fromPageTitleEnc;
        private String bdSourceName;
        private String bdFromPageTitlePrefix;
        private String token;

        public String getThumbURL() {
            return thumbURL;
        }

        public void setThumbURL(String thumbURL) {
            this.thumbURL = thumbURL;
        }

        public String getMiddleURL() {
            return middleURL;
        }

        public void setMiddleURL(String middleURL) {
            this.middleURL = middleURL;
        }

        public String getLargeTnImageUrl() {
            return largeTnImageUrl;
        }

        public void setLargeTnImageUrl(String largeTnImageUrl) {
            this.largeTnImageUrl = largeTnImageUrl;
        }

        public int getHasLarge() {
            return hasLarge;
        }

        public void setHasLarge(int hasLarge) {
            this.hasLarge = hasLarge;
        }

        public String getHoverURL() {
            return hoverURL;
        }

        public void setHoverURL(String hoverURL) {
            this.hoverURL = hoverURL;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public String getObjURL() {
            return objURL;
        }

        public void setObjURL(String objURL) {
            this.objURL = objURL;
        }

        public String getFromURL() {
            return fromURL;
        }

        public void setFromURL(String fromURL) {
            this.fromURL = fromURL;
        }

        public String getFromURLHost() {
            return fromURLHost;
        }

        public void setFromURLHost(String fromURLHost) {
            this.fromURLHost = fromURLHost;
        }

        public String getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(String currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getBdSrcType() {
            return bdSrcType;
        }

        public void setBdSrcType(String bdSrcType) {
            this.bdSrcType = bdSrcType;
        }

        public String getDi() {
            return di;
        }

        public void setDi(String di) {
            this.di = di;
        }

        public String getAdid() {
            return adid;
        }

        public void setAdid(String adid) {
            this.adid = adid;
        }

        public String getIs() {
            return is;
        }

        public void setIs(String is) {
            this.is = is;
        }

        public Object getSimid_info() {
            return simid_info;
        }

        public void setSimid_info(Object simid_info) {
            this.simid_info = simid_info;
        }

        public Object getFace_info() {
            return face_info;
        }

        public void setFace_info(Object face_info) {
            this.face_info = face_info;
        }

        public Object getXiangshi_info() {
            return xiangshi_info;
        }

        public void setXiangshi_info(Object xiangshi_info) {
            this.xiangshi_info = xiangshi_info;
        }

        public String getAdPicId() {
            return adPicId;
        }

        public void setAdPicId(String adPicId) {
            this.adPicId = adPicId;
        }

        public String getPi() {
            return pi;
        }

        public void setPi(String pi) {
            this.pi = pi;
        }

        public int getBdSetImgNum() {
            return bdSetImgNum;
        }

        public void setBdSetImgNum(int bdSetImgNum) {
            this.bdSetImgNum = bdSetImgNum;
        }

        public String getBdImgnewsDate() {
            return bdImgnewsDate;
        }

        public void setBdImgnewsDate(String bdImgnewsDate) {
            this.bdImgnewsDate = bdImgnewsDate;
        }

        public String getFromPageTitle() {
            return fromPageTitle;
        }

        public void setFromPageTitle(String fromPageTitle) {
            this.fromPageTitle = fromPageTitle;
        }

        public String getFromPageTitleEnc() {
            return fromPageTitleEnc;
        }

        public void setFromPageTitleEnc(String fromPageTitleEnc) {
            this.fromPageTitleEnc = fromPageTitleEnc;
        }

        public String getBdSourceName() {
            return bdSourceName;
        }

        public void setBdSourceName(String bdSourceName) {
            this.bdSourceName = bdSourceName;
        }

        public String getBdFromPageTitlePrefix() {
            return bdFromPageTitlePrefix;
        }

        public void setBdFromPageTitlePrefix(String bdFromPageTitlePrefix) {
            this.bdFromPageTitlePrefix = bdFromPageTitlePrefix;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
