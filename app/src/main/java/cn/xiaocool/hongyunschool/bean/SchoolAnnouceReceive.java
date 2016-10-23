package cn.xiaocool.hongyunschool.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class SchoolAnnouceReceive {

    /**
     * id : 3
     * noticeid : 3
     * receiverid : 604
     * receivertype : 0
     * create_time : 0
     * notice_info : [{"name":"校长","photo":"weixiaotong.png","id":"3","userid":"599","title":"标题","type":"0","content":"内容","create_time":"1472609603"}]
     * receiv_list : [{"id":"3","noticeid":"3","receiverid":"604","receivertype":"0","create_time":"1472610368"}]
     * pic : [{"photo":"hyx9381472460032660.jpg"},{"photo":"hyx8221472460032673.jpg"}]
     */

    private String id;
    private String noticeid;
    private String receiverid;
    private String receivertype;
    private String create_time;
    /**
     * name : 校长
     * photo : weixiaotong.png
     * id : 3
     * userid : 599
     * title : 标题
     * type : 0
     * content : 内容
     * create_time : 1472609603
     */

    private List<NoticeInfoBean> notice_info;
    /**
     * id : 3
     * noticeid : 3
     * receiverid : 604
     * receivertype : 0
     * create_time : 1472610368
     */

    private List<ReceivListBean> receiv_list;
    /**
     * photo : hyx9381472460032660.jpg
     */

    private List<PicBean> pic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoticeid() {
        return noticeid;
    }

    public void setNoticeid(String noticeid) {
        this.noticeid = noticeid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getReceivertype() {
        return receivertype;
    }

    public void setReceivertype(String receivertype) {
        this.receivertype = receivertype;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public List<NoticeInfoBean> getNotice_info() {
        return notice_info;
    }

    public void setNotice_info(List<NoticeInfoBean> notice_info) {
        this.notice_info = notice_info;
    }

    public List<ReceivListBean> getReceiv_list() {
        return receiv_list;
    }

    public void setReceiv_list(List<ReceivListBean> receiv_list) {
        this.receiv_list = receiv_list;
    }

    public List<PicBean> getPic() {
        return pic;
    }

    public void setPic(List<PicBean> pic) {
        this.pic = pic;
    }

    public static class NoticeInfoBean{
        private String name;
        private String photo;
        private String id;
        private String userid;
        private String title;
        private String type;
        private String content;
        private String create_time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }


    }

    public static class ReceivListBean{
        private String id;
        private String noticeid;
        private String receiverid;
        private String receivertype;
        private String create_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNoticeid() {
            return noticeid;
        }

        public void setNoticeid(String noticeid) {
            this.noticeid = noticeid;
        }

        public String getReceiverid() {
            return receiverid;
        }

        public void setReceiverid(String receiverid) {
            this.receiverid = receiverid;
        }

        public String getReceivertype() {
            return receivertype;
        }

        public void setReceivertype(String receivertype) {
            this.receivertype = receivertype;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }

    public static class PicBean {
        private String photo;

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}
