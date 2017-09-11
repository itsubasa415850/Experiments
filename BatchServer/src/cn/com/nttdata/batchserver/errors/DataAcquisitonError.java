package cn.com.nttdata.batchserver.errors;

public class DataAcquisitonError extends ServiceError {
    private static final long serialVersionUID = -7594111635915620050L;
    private static final String command = "データの取得に失敗しました：";
    public DataAcquisitonError(String message, Throwable cause) {
        super(command + message, cause);
    }
}
