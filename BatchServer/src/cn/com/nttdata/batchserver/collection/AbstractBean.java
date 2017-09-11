package cn.com.nttdata.batchserver.collection;

import java.io.Serializable;

abstract class AbstractBean implements IBean, Serializable {
    private static final long serialVersionUID = -6788536984292828292L;
    public abstract String getFinancialReportNo();
    public abstract void setFinancialReportNo(String financialReportNo);
}
