package cn.com.nttdata.arelleperf.dts;

import java.util.ArrayList;
import java.util.List;

import ubmatrix.xbrl.common.exception.src.CoreException;
import ubmatrix.xbrl.common.formatter.src.Formatter;
import ubmatrix.xbrl.common.formatter.src.FormatterException;
import ubmatrix.xbrl.common.formatter.src.IFormatter;
import ubmatrix.xbrl.common.memo.src.IMemo;
import ubmatrix.xbrl.common.memo.uriResolver.src.IURIResolver;
import ubmatrix.xbrl.common.memo.uriResolver.src.URIResolver;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.ISimpleProgressNotify;

abstract class AbstractDtsWrapper implements ISimpleProgressNotify {
    private IDTS dtsFactory;
    protected List<IMemo> m_memoList = new ArrayList<IMemo>();
    private IFormatter m_formatter = new Formatter();
    private IURIResolver m_resolver = new URIResolver();
    private String m_language = "en";
    protected void setDTSFactory(IDTS dts) {
        dtsFactory = dts;
    }

    protected IDTS getDTSFactory() {
        return dtsFactory;
    }

    protected void lockDTSFactory() throws CoreException {
        dtsFactory.lock();
    }

    protected void unLockDTSFactory() throws CoreException {
        if(dtsFactory != null)
            dtsFactory.unlock();
    }

    @Deprecated
    protected void saveDTSFactory() throws CoreException {
        dtsFactory.save();
    }

    protected void closeDTSFactory() throws CoreException {
        if(dtsFactory != null)
            dtsFactory.close();
    }

    protected List<IMemo> getMemoList() {
        return m_memoList;
    }

    protected void setMemoList(List<IMemo> l) {
        m_memoList.addAll(l);
    }


    /**
     * This method is used to clear the arraylist of memos
     */
    protected void clearMemoList() {

        m_memoList.clear();
    }

    protected void displayMemos() throws FormatterException {
        if (getMemoList().size()> 0) {
            System.out.println("Following errors occurred:");
            // print any error memos
            for (int i = 0; i < getMemoList().size(); ++i) {
                IMemo memo = (IMemo)getMemoList().get(i);
                System.out.println(getSubstitutedLocalizedString(memo));
            }
        }
    }

    protected boolean hasErrors() {
        return m_memoList.size() > 0;
    }

    private String getSubstitutedLocalizedString(IMemo e) throws FormatterException {
        //Resolve the memo uri
        Object[] particles = e.getParticles();
        // Call the localizer with the memoUri and the language, and get back the localized string.
        // Note, the localized String might contain placeholders like {0}
        String localizedString = m_resolver.getUnsubstitutedLocalizedString(e.getMemoURI(), m_language);
        return (localizedString == e.getMemoURI()) ?
                e.getMemoURI() : m_formatter.getSubstitutedString(localizedString, particles);
    }

    protected void adoptDtsBases(IDTS dts) {
        dtsFactory.adoptDts(dts);
    }
}
