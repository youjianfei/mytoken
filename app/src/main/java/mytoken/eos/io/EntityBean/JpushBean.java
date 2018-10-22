package mytoken.eos.io.EntityBean;

public class JpushBean {

    /**
     * options : {"curType":"transfer","url":"https://www.baidu.com"}
     */

    private OptionsBean options;

    public OptionsBean getOptions() {
        return options;
    }

    public void setOptions(OptionsBean options) {
        this.options = options;
    }

    public static class OptionsBean {
        /**
         * curType : transfer
         * url : https://www.baidu.com
         */

        private String curType;
        private String url;

        public String getCurType() {
            return curType;
        }

        public void setCurType(String curType) {
            this.curType = curType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
