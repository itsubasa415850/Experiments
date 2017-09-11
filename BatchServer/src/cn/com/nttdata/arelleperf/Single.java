package cn.com.nttdata.arelleperf;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import cn.com.nttdata.arelleperf.dts.DtsWrapper;
import cn.com.nttdata.arelleperf.threads.TestClass;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.IDTSNode;
import ubmatrix.xbrl.common.src.IDTSResultSet;
import ubmatrix.xbrl.common.src.IXbrlDomain;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSPath;
import ubmatrix.xbrl.domain.xbrl21Domain.src.XbrlDomainUri;

public final class Single {
    private static final String host = "http://localhost:8080/rest/xbrl/";
    private static final String act = "/open";
    private static String fliename = File.separator + "test60M.xml";
    
//    public static void main(String[] args) {
//        int cnt = 1;
//        if(args.length != 1) {
//            throw new IllegalArgumentException("请输入测试文件所在目录。");
//        }
//        do {
//            long startTime = System.currentTimeMillis();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
//            TestClass c = new TestClass(startTime, host + args[0] + fliename + act, null);
//            c.setFileName(fliename);
//            System.out.println("现在开始第" + cnt + "次进行" + fliename + "的读取，开始时间为：" + sdf.format(new Date(startTime)));
//            c.run();
//            cnt++;
//        } while (cnt <= 5);
//    }
    public static void main(String[] args) throws Exception {
        DtsWrapper wrapper = DtsWrapper.getInstance("E:\\中国银行专用VSS\\中央结算公司\\02.概要设计\\分类标准编制\\征求意见稿\\理财信息XBRL扩展分类标准\\chinawealth_20160430\\chinawealth\\wei\\wei_chinawealth_2016-04-30.xsd",
                1);
        IDTS dts = wrapper.getDts();
        IDTSResultSet resultSet = dts.getAllDtsNodes();
        Iterator itr = resultSet.getEnumerator();
        while(itr.hasNext()) {
            IDTSNode dtsNode = (IDTSNode) itr.next();
            if(dtsNode.getLocationHandle().getFileName().endsWith("2016-04-30.xsd")) {
                IXbrlDomain domain = (IXbrlDomain) dtsNode.findSingle(new DTSPath("/'"
                        + XbrlDomainUri.c_Taxonomy + "'"));
                System.out.println();
                break;
            }
        }
    }
}
