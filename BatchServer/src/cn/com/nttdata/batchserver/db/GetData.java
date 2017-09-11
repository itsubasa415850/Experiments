package cn.com.nttdata.batchserver.db;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.errors.DataAcquisitonError;
import cn.com.nttdata.xbrl.format.Decryption;
import cn.com.nttdata.xbrl.format.DecryptionForVAConfig;
import cn.com.nttdata.xbrl.mapping.AxisDataBean;
import cn.com.nttdata.xbrl.mapping.AxisDataCollection;
import cn.com.nttdata.xbrl.mapping.ContextDataBean;
import cn.com.nttdata.xbrl.mapping.ContextDataCollection;
import cn.com.nttdata.xbrl.mapping.FactDataBean;
import cn.com.nttdata.xbrl.mapping.FactDataCollection;
import cn.com.nttdata.xbrl.mapping.FactMappingBean;
import cn.com.nttdata.xbrl.mapping.FactMappingCollection;
import cn.com.nttdata.xbrl.mapping.FootnoteDataBean;
import cn.com.nttdata.xbrl.mapping.FootnoteDataCollection;
import cn.com.nttdata.xbrl.mapping.LabelDataBean;
import cn.com.nttdata.xbrl.mapping.LabelDataCollection;
import cn.com.nttdata.xbrl.mapping.NodeDataBean;
import cn.com.nttdata.xbrl.mapping.NodeDataCollection;
import cn.com.nttdata.xbrl.mapping.RoleTypeDataBean;
import cn.com.nttdata.xbrl.mapping.RoleTypeDataCollection;
import cn.com.nttdata.xbrl.mapping.UnitDataBean;
import cn.com.nttdata.xbrl.mapping.UnitDataCollection;

public final class GetData {

    private String inPath;
    private String outPath;
    private Logger logger;

    private GetData(String inPath, String outPath, Logger logger) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.logger = logger;
    }

    public static GetData getInstance(String inPath, String outPath, Logger logger) {
        return new GetData(inPath, outPath, logger);
    }

    public List<NodeDataBean> getNodedata() throws DataAcquisitonError {
        logger.info("ノードデータのロード開始。");
        logger.info("パス：" + inPath);
        logger.info("ファイル名：" + "NodeData.xml");

//        File fileNodeData = new File(inPath + File.separator + "NodeData.xml");
        NodeDataCollection ndeDataCollection = null;
        try {
            File fileNodeData = Decryption.decode(new File(inPath), "ndencrypted", "NodeData.xml");
            ndeDataCollection = NodeDataCollection.getInstance(new FileInputStream(fileNodeData));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ノードデータのロード終了。");
        return ndeDataCollection.getMappings();
    }

    public List<AxisDataBean> getAxisdata() throws DataAcquisitonError {
        logger.info("軸データのロード開始。");
        logger.info("パス：" + inPath);
        logger.info("ファイル名：" + "AxisData.xml");
//        File fileAxisData = new File(inPath + File.separator + "AxisData.xml");
        AxisDataCollection axisDataCollection;
        try {
            File fileAxisData = Decryption.decode(new File(inPath), "adencrypted", "AxisData.xml");
            axisDataCollection = AxisDataCollection.getInstance(new FileInputStream(fileAxisData));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("軸データのロード終了。");
        return axisDataCollection.getMappings();
    }

    public List<LabelDataBean> getLabeldata() throws DataAcquisitonError {
        logger.info("ラベルデータのロード開始。");
        logger.info("パス：" + inPath);
        logger.info("ファイル名：" + "LabelData.xml");
//        File fileLabelData = new File(inPath + File.separator + "LabelData.xml");
        LabelDataCollection labelDataCollection;
        try {
            File fileLabelData = DecryptionForVAConfig.decode(new File(inPath), "ldencrypted", "LabelData.xml");
            labelDataCollection = LabelDataCollection.getInstance(new FileInputStream(fileLabelData));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ラベルデータのロード終了。");
        return labelDataCollection.getMappings();
    }

    public List<RoleTypeDataBean> getRtdata() throws DataAcquisitonError {
        logger.info("ロールデータのロード開始。");
        logger.info("パス：" + inPath);
        logger.info("ファイル名：" + "RoleTypeData.xml");
        File fileRtlData = new File(inPath + File.separator + "RoleTypeData.xml");
        RoleTypeDataCollection roleTypeDataCollection;
        try {
//            File fileRtlData = Decryption.decode(new File(inPath), "rtencrypted", "RoleTypeData.xml");
            roleTypeDataCollection = RoleTypeDataCollection.getInstance(new FileInputStream(fileRtlData));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ロールデータのロード終了。");
        return roleTypeDataCollection.getMappings();
    }

    public List<FootnoteDataBean> getFndata() throws DataAcquisitonError {
        logger.info("フットノートデータのロード開始。");
        logger.info("パス：" + outPath);
        logger.info("ファイル名：" + "FootnoteData.xml");
        FootnoteDataCollection footnoteDataCollection;
        try {
            footnoteDataCollection = FootnoteDataCollection.getInstance(
                    new FileInputStream(new File(outPath + File.separator + "FootnoteData.xml")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("フットノートデータのロード終了。");
        return footnoteDataCollection.getMappings();
    }

    public List<ContextDataBean> getContextdata() throws DataAcquisitonError {
        logger.info("コンテキストデータのロード開始。");
        logger.info("パス：" + outPath);
        logger.info("ファイル名：" + "ContextData.xml");
        ContextDataCollection contextDataCollection;
        try {
            contextDataCollection = ContextDataCollection.getInstance(
                    new FileInputStream(new File(outPath + File.separator + "ContextData.xml")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("コンテキストデータのロード終了。");
        return contextDataCollection.getMappings();
    }

    public List<UnitDataBean> getUnitdata() throws DataAcquisitonError {
        logger.info("ユニットデータのロード開始。");
        logger.info("パス：" + outPath);
        logger.info("ファイル名：" + "UnitData.xml");
        UnitDataCollection unitDataCollection;
        try {
            unitDataCollection = UnitDataCollection.getInstance(
                    new FileInputStream(new File(outPath + File.separator + "UnitData.xml")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ユニットデータのロード終了。");
        return unitDataCollection.getMappings();
    }

    public List<FactDataBean> getFactdata() throws DataAcquisitonError {
        logger.info("ファクトデータのロード開始。");
        logger.info("パス：" + outPath);
        logger.info("ファイル名：" + "FactData.xml");
        FactDataCollection factDataCollection;
        try {
            factDataCollection = FactDataCollection.getInstance(
                    new FileInputStream(new File(outPath + File.separator + "FactData.xml")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ファクトデータのロード終了。");
        return factDataCollection.getMappings();
    }

    public List<FactMappingBean> getFmdata() throws DataAcquisitonError {
        logger.info("ファクトマッピングデータのロード開始。");
        logger.info("パス：" + outPath);
        logger.info("ファイル名：" + "FactData.xml");
        FactMappingCollection factMappingCollection;
        try {
            factMappingCollection = FactMappingCollection.getInstance(
                    new FileInputStream(new File(outPath + File.separator + "FactMapping.xml")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new DataAcquisitonError(e.toString() + e.getMessage(), e);
        }
        logger.info("ファクトマッピングデータのロード終了。");
        return factMappingCollection.getMappings();
    }
}
