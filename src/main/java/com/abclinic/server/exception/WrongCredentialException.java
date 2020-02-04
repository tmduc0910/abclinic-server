package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class WrongCredentialException extends BaseRuntimeException {
    public WrongCredentialException() {
        super("Đăng nhập lỗi: Sai tài khoản hoặc mật khẩu", HttpStatus.NOT_FOUND);
    }
}
