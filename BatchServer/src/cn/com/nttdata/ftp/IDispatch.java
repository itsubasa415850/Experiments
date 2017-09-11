package cn.com.nttdata.ftp;

/**
 * 上层回调接口。
 * @author guopeng02
 *
 */
public interface IDispatch {
    void recordProgress(int threadNo, String x);
}
