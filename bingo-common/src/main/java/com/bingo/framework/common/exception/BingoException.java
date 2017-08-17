package com.bingo.framework.common.exception;

/**
 * Created by ZhangGe on 2017/7/12.
 */
public abstract class BingoException extends RuntimeException {

    protected static final long serialVersionUID = 7815426752583648689L;

    protected int code; // BingoException可以有子类，异常类型用ErrorCode表示，以便保持兼容。

    protected BingoException() {
        super();
    }

    protected BingoException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BingoException(String message) {
        super(message);
    }

    protected BingoException(Throwable cause) {
        super(cause);
    }

    protected BingoException(int code) {
        super();
        this.code = code;
    }

    protected BingoException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    protected BingoException(int code, String message) {
        super(message);
        this.code = code;
    }

    protected BingoException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
