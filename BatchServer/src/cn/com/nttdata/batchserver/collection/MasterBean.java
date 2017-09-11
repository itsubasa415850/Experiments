package cn.com.nttdata.batchserver.collection;

public class MasterBean extends AbstractBean {

    private static final long serialVersionUID = 5602148817430857431L;
    private String companyName;
    private String companyId;
    private String financialReportDate;
    private String taxonomyType;
    private String financialReportNo;
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    public String getFinancialReportDate() {
        return financialReportDate;
    }
    public void setFinancialReportDate(String financialReportDate) {
        this.financialReportDate = financialReportDate;
    }
    public String getTaxonomyType() {
        return taxonomyType;
    }
    public void setTaxonomyType(String taxonomyType) {
        this.taxonomyType = taxonomyType;
    }
    public String getFinancialReportNo() {
        return financialReportNo;
    }
    public void setFinancialReportNo(String financialReportNo) {
        this.financialReportNo = financialReportNo;
    }


}
