package cn.com.nttdata.arelleperf.dts;

import java.io.File;

import ubmatrix.xbrl.common.exception.src.CoreException;
import ubmatrix.xbrl.common.formatter.src.FormatterException;
import ubmatrix.xbrl.common.memo.registry.src.DiscreteMemoRegistry;
import ubmatrix.xbrl.common.memo.src.IMemo;
import ubmatrix.xbrl.common.src.Configuration;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.ILocationHandle;
import ubmatrix.xbrl.common.src.IXbrlDomain;
import ubmatrix.xbrl.common.utility.src.CommonUtilities;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSDiscoverer;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSPath;
import ubmatrix.xbrl.domain.xbrl21Domain.src.XbrlDomainUri;
import ubmatrix.xbrl.locationController.factory.src.LocationHandleFactory;
import ubmatrix.xbrl.src.Xbrl;
import ubmatrix.xbrl.validation.formula.src.FormulaConfiguration;
import cn.com.nttdata.xbrl.common.XbrlProperties;

//@SuppressWarnings("unchecked")
public class DtsWrapper extends AbstractDtsWrapper {

    private String m_basePath = "";

    private DtsWrapper(String fileUri, int fileType) throws Exception {
        DiscreteMemoRegistry memoRegistry = new DiscreteMemoRegistry();
        long cookie = memoRegistry.attach(this);
        boolean success = Xbrl.initialize(memoRegistry);
        memoRegistry.detach(cookie);
        if (!success)
            throw new Exception("Failed to initialize");
        Configuration config = Configuration.getInstance();
        m_basePath = config.getCoreRoot();
        if (m_basePath == null )
            System.out.println("The environmental variable COREROOT is not defined. " +
                    "Please add COREROOT as environment variable and points to your XBRL Processor install directory.");
        // Add a trailing slash to the end if it doesn't exist
        if (!m_basePath.endsWith(File.separator))
            m_basePath += File.separator;

        XbrlProperties.loadProperties();
        if (getMemoList().size() > 0) {
            displayMemos();
            return;
        }
        clearMemoList();
        setDTSFactory(load(fileUri, fileType));
//        lockDTSFactory();
    }

    public void closeDts() throws CoreException {
        unLockDTSFactory();
        closeDTSFactory();
    }

    public String procFormula(String[] formulaParam) throws Exception {
        Xbrl xbrl = Xbrl.newInstance();
        String creatInstancePath = "";
        String traceFilePath ="";
        String outPath;
        try {
            IXbrlDomain instance = (IXbrlDomain) getDTSFactory()
                    .findSingle(new DTSPath(CommonUtilities.formatString("/'{0}'",
                            new Object[] { XbrlDomainUri.c_Instance })));
            outPath = instance.getPhysicalUri();
            String fileName = getDTSFactory().getLocationHandle().getPhysicalUri();
            String caseName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            xbrl.setDTS(getDTSFactory());
            xbrl.load(outPath);
            outPath = outPath.substring(0, outPath.lastIndexOf("/") + 1) + "FormulaResult/";
            File rDir = new File(outPath);
            if(!rDir.exists())
                rDir.mkdir();
            outPath = outPath.substring(0, outPath.lastIndexOf("/") + 1);
            caseName = (caseName.split("\\."))[0];
            creatInstancePath = outPath.concat("Formula-instance_" + caseName + ".xml");
            traceFilePath =outPath.concat("Formula-trace_" + caseName + ".xml");

            if(formulaParam == null || formulaParam.length < 4)
                xbrl.processFormulas(creatInstancePath, traceFilePath);
            else
                for(int idx = 0; idx < formulaParam.length / 4; idx ++) {
                    int index = idx * 4;
                    FormulaConfiguration conf = new FormulaConfiguration();
                    conf.setParameter(formulaParam[index], formulaParam[index + 2], formulaParam[index + 3], formulaParam[index + 1]);
                    xbrl.processFormulas(creatInstancePath, traceFilePath, conf);
                }

        }  catch (CoreException e) {
            throw e;
        }
        return outPath;
    }

    public static DtsWrapper getInstance(String fileUri, int fileType) throws Exception {
        return new DtsWrapper(fileUri, fileType);
    }

    /**
     * Accessor method - returns the base path, which is the COREROOT
     */
    public String getBasePath() {
        return m_basePath;
    }

    public IDTS getDts() {
        return getDTSFactory();
    }

    private IDTS load(String fileUri, int fileType) throws Exception {
        IDTS dts = null;
        DiscreteMemoRegistry discreteMemoRegistry = new DiscreteMemoRegistry();
        discreteMemoRegistry.attach(this);
        ILocationHandle handle = LocationHandleFactory.createNewLocationHandle(null, fileUri, fileType);
        handle.setLocationHandleType(fileType);
        handle.setMemoRegistry(discreteMemoRegistry);

        dts = DTSDiscoverer.establishEntryPoint(handle);

        return dts;
    }


    public void cancel() {
    }


    public boolean onProgress(Object type, long rate, Object[] parameters)
            throws FormatterException {
        IMemo memo = (IMemo) parameters[0];
        String action = (String) parameters[1];
        if (action.equals("Add")) {
            synchronized (m_memoList) {
                m_memoList.add(memo);
            }
        }
        return true;
    }

    public void onStart(Object paramObject, long paramLong1, long paramLong2,
            boolean paramBoolean, String paramString) {
    }
    public void onStop(Object paramObject, String paramString)
            throws FormatterException {

    }

    public void pause(boolean paramBoolean) {
    }

}
