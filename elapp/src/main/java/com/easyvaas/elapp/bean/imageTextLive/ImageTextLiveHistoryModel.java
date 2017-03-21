package com.easyvaas.elapp.bean.imageTextLive;


import java.util.List;

public class ImageTextLiveHistoryModel {


    /**
     * start : 0
     * count : 7
     * next : 7
     * msgs : [{"type":"chatmessage","timestamp":1,"from":"1","to":"1","groupId":"1","chat_type":"groupchat","msg_id":"1","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"msg":"test","type":"txt"}]}},{"type":"chatmessage","timestamp":1,"from":"1","to":"1","groupId":"1","chat_type":"groupchat","msg_id":"1","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_1084bb1dc49ebe43701171710c961068.png","filename":"upload_1084bb1dc49ebe43701171710c961068.png","type":"img"}]}},{"type":"chatmessage","timestamp":1,"from":"1","to":"1","groupId":"1","chat_type":"groupchat","msg_id":"1","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_0fd293aa10896a771596acf8fec1a9cb.png","filename":"upload_0fd293aa10896a771596acf8fec1a9cb.png","type":"img"}]}},{"type":"chatmessage","timestamp":0,"from":"1","to":"1","groupId":"1","chat_type":"groupchat","msg_id":"1","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_bdf43b704c30446120c0782dd73eb194.png","filename":"upload_bdf43b704c30446120c0782dd73eb194.png","type":"img"}]}},{"type":"chatmessage","timestamp":0,"from":"1","to":"1","groupId":"1","chat_type":"groupchat","msg_id":"1","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_45520df89c312a97e02f5cc7d410bfde.png","filename":"upload_45520df89c312a97e02f5cc7d410bfde.png","type":"img"}]}},{"type":"chatmessage","timestamp":0,"from":"","to":"","groupId":"","chat_type":"groupchat","msg_id":"","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_5badd60ca0be4c0ac5b2f5f82bd3016a.png","filename":"upload_5badd60ca0be4c0ac5b2f5f82bd3016a.png","type":"img"}]}},{"type":"chatmessage","timestamp":0,"from":"","to":"","groupId":"","chat_type":"groupchat","msg_id":"","payload":{"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"url":"http://image-cdn.hooview.com/hooviewportal/upload_8fe33d872dfef97644031e69c039d8b0.png","filename":"upload_8fe33d872dfef97644031e69c039d8b0.png","type":"img"}]}}]
     */

    private int start;
    private int count;
    private int next;
    private List<MsgsBean> msgs;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public List<MsgsBean> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<MsgsBean> msgs) {
        this.msgs = msgs;
    }

    public static class MsgsBean {
        /**
         * type : chatmessage
         * timestamp : 1
         * from : 1
         * to : 1
         * groupId : 1
         * chat_type : groupchat
         * msg_id : 1
         * payload : {"ext":{"tp":"","nk":"","rct":"","rnk":""},"bodies":[{"msg":"test","type":"txt"}]}
         */

        private String type;
        private String timestamp;
        private String from;
        private String to;
        private String groupId;
        private String chat_type;
        private String msg_id;
        private PayloadBean payload;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getChat_type() {
            return chat_type;
        }

        public void setChat_type(String chat_type) {
            this.chat_type = chat_type;
        }

        public String getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(String msg_id) {
            this.msg_id = msg_id;
        }

        public PayloadBean getPayload() {
            return payload;
        }

        public void setPayload(PayloadBean payload) {
            this.payload = payload;
        }

        public static class PayloadBean {
            /**
             * ext : {"tp":"","nk":"","rct":"","rnk":""}
             * bodies : [{"msg":"test","type":"txt"}]
             */

            private ExtBean ext;
            private List<BodiesBean> bodies;

            public ExtBean getExt() {
                return ext;
            }

            public void setExt(ExtBean ext) {
                this.ext = ext;
            }

            public List<BodiesBean> getBodies() {
                return bodies;
            }

            public void setBodies(List<BodiesBean> bodies) {
                this.bodies = bodies;
            }

            public static class ExtBean {
                /**
                 * tp :
                 * nk :
                 * rct :
                 * rnk :
                 */

                private String tp;
                private String nk;
                private String rct;
                private String rnk;

                public String getTp() {
                    return tp;
                }

                public void setTp(String tp) {
                    this.tp = tp;
                }

                public String getNk() {
                    return nk;
                }

                public void setNk(String nk) {
                    this.nk = nk;
                }

                public String getRct() {
                    return rct;
                }

                public void setRct(String rct) {
                    this.rct = rct;
                }

                public String getRnk() {
                    return rnk;
                }

                public void setRnk(String rnk) {
                    this.rnk = rnk;
                }
            }

            public static class BodiesBean {
                /**
                 * msg : test
                 * type : txt
                 */

                private String msg;
                private String type;
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getMsg() {
                    return msg;
                }

                public void setMsg(String msg) {
                    this.msg = msg;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }
    }

}
