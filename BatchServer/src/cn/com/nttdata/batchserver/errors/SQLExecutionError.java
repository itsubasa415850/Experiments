package cn.com.nttdata.batchserver.errors;

public class SQLExecutionError extends ServiceError {
    private static final long serialVersionUID = 3828657232277805955L;
    private static final String command = "SQLの実行に失敗しました：";
    public SQLExecutionError(String message, Throwable cause) {
        super(command + message, cause);
    }
}
