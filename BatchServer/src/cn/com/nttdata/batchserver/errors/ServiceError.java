package cn.com.nttdata.batchserver.errors;

public class ServiceError extends Error {
    private static final String command = "Encountered a service error:";
    private static final long serialVersionUID = 6600091491263107225L;
    public ServiceError(String message, Throwable cause) {
        super(command + message, cause);
    }
}
