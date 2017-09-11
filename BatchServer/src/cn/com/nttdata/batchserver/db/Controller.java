package cn.com.nttdata.batchserver.db;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.collection.MasterBean;
import cn.com.nttdata.batchserver.errors.ConnectionFailureError;
import cn.com.nttdata.batchserver.errors.SQLExecutionError;
import cn.com.nttdata.xbrl.mapping.AxisDataBean;
import cn.com.nttdata.xbrl.mapping.ContextDataBean;
import cn.com.nttdata.xbrl.mapping.ExplicitMemberBean;
import cn.com.nttdata.xbrl.mapping.FactDataBean;
import cn.com.nttdata.xbrl.mapping.FactMappingBean;
import cn.com.nttdata.xbrl.mapping.FootnoteDataBean;
import cn.com.nttdata.xbrl.mapping.HyperCubeBean;
import cn.com.nttdata.xbrl.mapping.LabelDataBean;
import cn.com.nttdata.xbrl.mapping.NodeDataBean;
import cn.com.nttdata.xbrl.mapping.RoleTypeDataBean;
import cn.com.nttdata.xbrl.mapping.UnitDataBean;

public class Controller {

    private Connection conn = null;
    private MasterBean mb = null;
    private String financialRNum = null;
    private Logger logger = null;

    public void flushConnection() {
        MakeDBConnection.flushConnection(conn);
    }

    public void commitSession() {
        MakeDBConnection.commitSession(conn);
    }

    public void rollbackSession() {
        MakeDBConnection.rollBackSession(conn);
    }

    @Deprecated
    public void makeConnection() throws ConnectionFailureError{
        conn = MakeDBConnection.getInstance();
    }

    public void makeConnection(String serverInfo,
                                                    String serviceInfo,
                                                    String user,
                                                    String password) throws ConnectionFailureError {
        conn = MakeDBConnection.connectTo(serverInfo, serviceInfo, user, password);
    }

    public Controller(Logger logger) {
        this.logger = logger;
    }

    public Connection getConn() {
        return conn;
    }
    
    
    
    public void modify(String uuid, String okorng) {
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("update validation ")
        .append("set okorng= ?, updatedate=sysdate ")
        .append("where uuid = ?")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, okorng);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (Exception e) {
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }
    }
    
    public void record(String uuid, String result) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = new StringBuffer()
        .append("insert into VALIDATION (")
        .append("UUID, OKORNG, UPDATEDATE")
        .append(") values (?, ?, sysdate)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, uuid);
            ps.setString(2, result);
            ps.executeUpdate();
        } catch (Exception e) {
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {} finally {}
            }
        }
    }
    
    public String whichToInsert(String num) throws SQLExecutionError {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select uuid from validation where okorng='OK' and uuid like ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%_" + num);
            rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {} finally {}
            }
        }
        return null;
    }
    
    public boolean canInsert() throws SQLExecutionError {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = new StringBuffer()
            .append("select count(*) as a from master where ")
            .append("master.companyname = ? and ")
            .append("master.companyid = ? and ")
            .append("to_char(master.financialreportdate, 'yyyymmdd') = ? and ")
            .append("master.taxonomytype = ? ")
            .toString();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, mb.getCompanyName());
            ps.setString(2, mb.getCompanyId());
            ps.setString(3, mb.getFinancialReportDate());
            ps.setString(4, mb.getTaxonomyType());
            rs = ps.executeQuery();
            if(rs.next()) {
                if("1".equals(rs.getString("A"))) {
                    logger.info("指定したデータは既に存在しています：" + financialRNum);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error("指定しているキーワードでの照会に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {} finally {}
            }
        }
        return true;
    }

    public void performMasterSaveProcess(long random) throws SQLExecutionError {
        logger.info("performMasterSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
            .append("insert into master (")
            .append("select ? as signature, ")
            .append("? as companyname, ")
            .append("? as companyid, ")
            .append("to_date(?, 'yyyy/mm/dd') as financialreportdate, ")
            .append("? as taxonomytype, ")
            .append("? as financialreportno from dual)")
            .toString();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, random + "");
            ps.setString(2, mb.getCompanyName());
            ps.setString(3, mb.getCompanyId());
            ps.setString(4, mb.getFinancialReportDate());
            ps.setString(5, mb.getTaxonomyType());
            ps.setString(6, financialRNum);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }
    }

    public void initializeBean(MasterBean paraMb) {
        logger.info("initializeBean");
        mb = paraMb;
        financialRNum = mb.getCompanyName() + "_" +
                                mb.getCompanyId() + "_" +
                                mb.getFinancialReportDate() + "_" +
                                mb.getTaxonomyType();
    }

    public void performContextSaveProcess(List<ContextDataBean> cdList) throws SQLExecutionError {
        logger.info("performContextSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into contextdata (")
        .append("select (select dbms_random.value(100,500) value from dual) as signature, ")
        .append("? as CONTEXTDATAID, ")
        .append("? as IDENTIFIERURI, ")
        .append("? as IDDENTIFIERNAME, ")
        .append("to_date(nvl(?, ''), 'yyyy/mm/dd') as STARTDATE, ")
        .append("to_date(nvl(?, ''), 'yyyy/mm/dd') as ENDDATE, ")
        .append("to_date(nvl(?, ''), 'yyyy/mm/dd') as INSTANT, ")
        .append("? as ContextType, ")
        .append("? as financialreportno, ")
        .append("? as SEGMENT, ")
        .append("? as SCENARIO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(ContextDataBean cdb : cdList) {
                ps.setString(1, cdb.getContextDataId());
                ps.setString(2, cdb.getIdentifierURI());
                ps.setString(3, cdb.getIdentifierName());
                ps.setString(4, cdb.getStartDate());
                ps.setString(5, cdb.getEndDate());
                ps.setString(6, cdb.getInstant());
                ps.setString(7, cdb.getContextType());
                ps.setString(8, financialRNum);
                ps.setString(9, cdb.getSegment());
                ps.setString(10, cdb.getScenario());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }

    }

    public void performFactSaveProcess(List<FactDataBean> fList, String t) throws SQLExecutionError {
        logger.info("performFactSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into factdata")
        .append(t)
        .append(" (select (select dbms_random.value(100,500) value from dual) as signature, ")
        .append("? as factDATAID, ")
        .append("? as UNITDATAID, ")
        .append("? as CONTEXTDATAID, ")
        .append("? as DECIMALS, ")
        .append("? as PRECISION, ")
        .append("? as NIL, ")
        .append("? as TUPLEORDER, ")
        .append("? as CODE, ")
        .append("? as FINANCIALREPORTNO, ")
        .append("? as VALUE from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(FactDataBean fb : fList) {
                ps.setString(1, fb.getFactDataId());
                ps.setString(2, fb.getUnitDataId());
                ps.setString(3, fb.getContextDataId());
                ps.setString(4, fb.getDecimals());
                ps.setString(5, fb.getPrecision());
                ps.setString(6, fb.getNil());
                ps.setString(7, fb.getTupleOrder());
                ps.setString(8, fb.getCode());
                ps.setString(9, financialRNum);
                ps.setCharacterStream(10, new StringReader(fb.getValue()), fb.getValue().length());
//                ps.setString(10, " ");
                ps.addBatch();
//                ps.executeUpdate();
//                conn.commit();
//                conn.setAutoCommit(false);
//                insertClob(fb.getFactDataId(), financialRNum, fb.getValue());
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } catch(ArrayIndexOutOfBoundsException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }

    }

    public void performFactSaveProcess1(List<String> fList, String t) throws SQLExecutionError {
        logger.info("performFactSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into factdata")
        .append(t)
        .append("(SIGNATURE,FACTDATAID,UNITDATAID,CONTEXTDATAID,DECIMALS,PRECISION,NIL,TUPLEORDER,CODE,VALUE)")
        .append(" values ((select dbms_random.value(100,500) signature from dual), ")
        .append("?, ?, ?, ?, ?, ?, ?, ?, ?)")
        .toString();
        try {
//            logger.info(sql);
            ps = conn.prepareStatement(sql);
            for(String str : fList) {
                int idx = 0;
//                logger.info(str);
                String[] vals = str.split(",");
                ps.setString(++idx, "".equals(vals[0]) ? "123" : vals[0]);
                ps.setString(++idx, vals[1]);
                ps.setString(++idx, vals[2]);
                ps.setString(++idx, vals[3]);
                ps.setString(++idx, vals[4]);
                ps.setString(++idx, vals[5]);
                ps.setString(++idx, vals[6]);
                ps.setString(++idx, vals[7]);
                ps.setCharacterStream(++idx, new StringReader(vals[8]), vals[8].length());
                ps.executeUpdate();
//                ps.addBatch();
            }
//            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } catch(ArrayIndexOutOfBoundsException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }

    }

    public void performUnitSaveProcess(List<UnitDataBean> uList) throws SQLExecutionError {
        logger.info("performUnitSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into UNITDATA (")
        .append("select (select dbms_random.value(100,500) value from dual) as signature, ")
        .append("? as UNITDATAID, ")
        .append("? as NUMERATORMEASURE, ")
        .append("? as DENOMINATORMEASURE, ")
        .append("? as FINANCIALREPORTNO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(UnitDataBean ub : uList) {
                ps.setString(1, ub.getUnitDataId());
                ps.setString(2, ub.getNumeratorMeasure());
                ps.setString(3, ub.getDenominatorMeasure());
                ps.setString(4, financialRNum);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }

    }

    public void performFactMappingSaveProcess(List<FactMappingBean> fmList) throws SQLExecutionError {
        logger.info("performFactMappingSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into FACTMAPPING (")
        .append("select ? as NODEDATAID, ")
        .append("? as FACTDATAID, ")
        .append("? as FINANCIALREPORTNO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(FactMappingBean mpb : fmList) {
                ps.setString(1, mpb.getNodeDataId());
                ps.setString(2, mpb.getFactDataId());
                ps.setString(3, financialRNum);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }
    }

    public void performFootnoteSaveProcess(List<FootnoteDataBean> fnList)
            throws SQLExecutionError {
        logger.info("performFootnoteSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into FOOTNOTEDATA (")
        .append("select ? as FACTDATAID, ")
        .append("? as LANG, ")
        .append("? as FORDER, ")
        .append("? as VALUE, ")
        .append("? as FINANCIALREPORTNO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(FootnoteDataBean fn : fnList) {
                ps.setString(1, fn.getFactDataId());
                ps.setString(2, fn.getLang());
                ps.setString(3, fn.getOrder());
                ps.setString(4, fn.getValue());
                ps.setString(5, financialRNum);
                ps.addBatch();
              }
              ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally {}
            }
        }
    }

    public void performLabelSaveProcess(List<LabelDataBean> lList)
            throws SQLExecutionError {
        logger.info("performLabelSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into LABELDATA (")
        .append("select ? as ELEMENTID, ")
        .append("? as LABELROLE, ")
        .append("? as LANG, ")
        .append("? as VALUE, ")
        .append("? as FINANCIALREPORTNO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(LabelDataBean ld : lList) {
                int index = 0;
                ps.setString(++index, ld.getElementId().replace(":", "_"));
                ps.setString(++index, ld.getLabelRole());
                ps.setString(++index, ld.getLang());
                ps.setString(++index, ld.getValue());
                ps.setString(++index, financialRNum);
                ps.addBatch();
              }
              ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally{}
            }
        }
    }

    public void performRtSaveProcess(List<RoleTypeDataBean> rList)
            throws SQLExecutionError {
        logger.info("performRtSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
        .append("insert into ROLETYPEDATA (")
        .append("select ? as ROLEURI, ")
        .append("? as id, ")
        .append("? as DEFINITION, ")
        .append("? as LBTYPE, ")
        .append("? as FINANCIALREPORTNO from dual)")
        .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(RoleTypeDataBean rt : rList) {
                int index = 0;
                ps.setString(++index, rt.getRoleURI());
                ps.setString(++index, rt.getId());
                ps.setString(++index, rt.getDefinition());
                ps.setString(++index, rt.getLbType());
                ps.setString(++index, financialRNum);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {} finally{}
            }
        }
    }

    public void performNodeDataSaveProcess(List<NodeDataBean> nList) throws SQLExecutionError {
        logger.info("performNodeDataSaveProcess");
        PreparedStatement ps = null;
        String sql = new StringBuffer()
            .append("insert into nodedata (")
            .append("select ? as NODEDATAID, ")
            .append("nvl(?, '') as PARENTNODEDATAID, ")
            .append("? as NODEDATAHASH, ")
            .append("nvl(?, '') as PARENTNODEDATAHASH, ")
            .append("? as LBTYPE, ")
            .append("? as ELR, ")
            .append("? as ELEMENTID, ")
            .append("nvl(?, 'optional') as USE, ")
            .append("? as PRIORITY, ")
            .append("? as NORDER, ")
            .append("nvl(?, '') as PREFERREDLABEL, ")
            .append("nvl(?, '') as ABSTRACTFLAG, ")
            .append("nvl(?, '') as TUPLEFLAG, ")
            .append("nvl(?, '') as NILLABLEFLAG, ")
            .append("? as TYPE, ")
            .append("? as SUBSTITUTIONGROUP, ")
            .append("nvl(?, '') as PERIODTYPE, ")
            .append("nvl(?, '') as BALANCE, ")
            .append("? as HYPERCUBE, ")
            .append("? as financialreportno from dual)")
            .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(NodeDataBean n : nList) {
                HyperCubeBean hb = n.getHypercube();
                List<ExplicitMemberBean> exList = hb.getExplicitMember();
                int i = 0;
                ps.setString(++i, n.getNodeDataId());
                ps.setString(++i, n.getParentNodeDataId());
                ps.setString(++i, n.getNodeDataHash());
                ps.setString(++i, n.getParentNodeDataHash());
                ps.setString(++i, n.getLbType());
                ps.setString(++i, n.getElr());
                ps.setString(++i, n.getElementId());
                ps.setString(++i, n.getUse());
                ps.setString(++i, n.getPriority());
                ps.setString(++i, n.getOrder());
                ps.setString(++i, n.getPreferredLabel());
                ps.setString(++i, n.getAbstractFlag());
                ps.setString(++i, n.getTupleFlag());
                ps.setString(++i, n.getNillableFlag());
                ps.setString(++i, "");
                ps.setString(++i, n.getSubstitutionGroup());
                ps.setString(++i, n.getPeriodType());
                ps.setString(++i, n.getBalance());
                ps.setString(++i, makeExplicitMem(exList));
                ps.setString(++i, financialRNum);
                ps.addBatch();
              }
              ps.executeBatch();
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(Exception e) {}
            }
        }
    }

    public void performAxisDataSaveProcess(List<AxisDataBean> xList) throws SQLExecutionError {
        logger.info("performAxisDataSaveProcess");
        PreparedStatement ps = null;
        Random random = new Random();
        long hypercube = random.nextLong();
        ;
        String sql = new StringBuffer()
            .append("insert into AXISDATA (")
            .append("select ? as AXISID, ")
            .append("? as DIMENSION, ")
            .append("? as DOMAINMEMBER, ")
            .append("? as PARENTDOMAINITEMDATAID, ")
            .append("? as USE, ")
            .append("? as PRIORITY, ")
            .append("? as AORDER, ")
            .append("? as financialreportno from dual)")
            .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(AxisDataBean xb : xList) {
                int i = 0;
                ps.setString(++i, String.valueOf(hypercube < 0 ? (- hypercube) : hypercube));
                ps.setString(++i, xb.getDimension());
                ps.setString(++i, xb.getDomainItemDataId());
                ps.setString(++i, xb.getParentDomainItemDataId());
                ps.setString(++i, xb.getUse());
                ps.setString(++i, xb.getPriority());
                ps.setString(++i, xb.getOrder());
                ps.setString(++i, financialRNum);
                ps.executeUpdate();
                hypercube = random.nextLong();
            }
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(SQLException e) {} finally {}
            }
        }
    }

    private String makeExplicitMem(List<ExplicitMemberBean> exList) throws SQLExecutionError {
        long hypercube = 0;
        long returnValue;
        Random random = new Random();
        hypercube = random.nextLong();
        returnValue = hypercube < 0 ? (- hypercube) : hypercube;
        PreparedStatement ps = null;
        String sql = new StringBuffer()
            .append("insert into EXPLICITMEMBER (")
            .append("select ? as HYPERCUBE, ")
            .append("? as DIMENSION, ")
            .append("? as DOMAINMEMBER, ")
            .append("? as financialreportno from dual)")
            .toString();
        try {
            ps = conn.prepareStatement(sql);
            for(ExplicitMemberBean eb : exList) {
                int i = 0;
                ps.setString(++i, String.valueOf(returnValue));
                ps.setString(++i, eb.getDimension());
                ps.setString(++i, eb.getDomainMember());
                ps.setString(++i, financialRNum);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("データの挿入に失敗しました：" + e.toString());
            throw new SQLExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                } catch(SQLException e) {} finally {}
            }
        }
        return String.valueOf(returnValue);
    }
}