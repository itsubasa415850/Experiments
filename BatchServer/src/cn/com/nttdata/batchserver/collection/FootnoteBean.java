package cn.com.nttdata.batchserver.collection;

public class FootnoteBean extends AbstractBean {

    private static final long serialVersionUID = -6991673793055259818L;
    private String factDataId;
    private String lang;
    private String order;
    private long value;

    private String financialReportNo;
    public String getFinancialReportNo() {
        return financialReportNo;
    }
    public void setFinancialReportNo(String financialReportNo) {
        this.financialReportNo = financialReportNo;
    }
    public String getFactDataId() {
        return factDataId;
    }
    public void setFactDataId(String factDataId) {
        this.factDataId = factDataId;
    }
    public String getLang() {
        return lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }

}
