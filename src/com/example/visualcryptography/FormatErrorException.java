
package com.example.visualcryptography;

/**
 * ����ĳЩ���ҹ�����δ�ҵ�ʱ���׳����쳣.
 * 
 * @author weiyao
 */
public class FormatErrorException extends Exception {
    /**
     * ���캯��.
     */
    public FormatErrorException() {

    }

    /**
     * ���캯��.
     * 
     * @param message ���������
     */
    public FormatErrorException(final String message) {
        super(message);
    }
}
