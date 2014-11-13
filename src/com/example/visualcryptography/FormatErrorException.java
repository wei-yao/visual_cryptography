
package com.example.visualcryptography;

/**
 * 进行某些查找工作，未找到时，抛出此异常.
 * 
 * @author weiyao
 */
public class FormatErrorException extends Exception {
    /**
     * 构造函数.
     */
    public FormatErrorException() {

    }

    /**
     * 构造函数.
     * 
     * @param message 输出的文字
     */
    public FormatErrorException(final String message) {
        super(message);
    }
}
