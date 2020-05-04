package com.abclinic.server.exception;

import com.abclinic.server.common.base.CustomRuntimeException;
import org.springframework.http.HttpStatus;

public class WrongCredentialException extends CustomRuntimeException {
    public WrongCredentialException() {
        super("Đăng nhập lỗi: Sai tài khoản hoặc mật khẩu", HttpStatus.NOT_FOUND);
    }
}
