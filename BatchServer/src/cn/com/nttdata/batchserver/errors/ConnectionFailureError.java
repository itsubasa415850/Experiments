package cn.com.nttdata.batchserver.errors;

public class ConnectionFailureError extends ServiceError {

    private static final long serialVersionUID = 5621496054403893447L;

    private static final String command = "连接ＯＲＡＣＬＥ发生异常．";

    public ConnectionFailureError(String message, Throwable cause) {
        super(command + message, cause);
    }
}
