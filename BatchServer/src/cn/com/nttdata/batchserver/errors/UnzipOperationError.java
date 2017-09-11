package cn.com.nttdata.batchserver.errors;

public class UnzipOperationError extends ServiceError {
    private static final long serialVersionUID = -7336460145310545669L;
    private static final String command = "执行解压缩时发生异常:";
    public UnzipOperationError(String message, Throwable cause) {
        super(command + message, cause);
    }
}
