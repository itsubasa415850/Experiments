package cn.com.nttdata.batchserver.errors;

public class XBRLComponentExecutionError extends ServiceError {
    private static final long serialVersionUID = 9142909370114057309L;
    private static final String command = "执行XBRL组件时发生异常:";
    public XBRLComponentExecutionError(String message, Throwable cause) {
        super(command + message, cause);
    }

}
