package cn.com.nttdata.batchserver.errors;

public class MailServiceError extends ServiceError {

    private static final long serialVersionUID = 2081076836488238494L;
    private static final String command = "发送邮件时发生异常．";
    public MailServiceError(String message, Throwable cause) {
        super(command + message, cause);
    }

}
