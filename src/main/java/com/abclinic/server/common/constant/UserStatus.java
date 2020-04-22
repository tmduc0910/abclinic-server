package com.abclinic.server.common.constant;

import com.abclinic.server.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.constant
 * @created 4/22/2020 8:45 AM
 */
public enum UserStatus {
    NEW(1),
    ASSIGN_L1(2),
    ASSIGN_L2(4),
    ASSIGN_L3(8),
    SCHEDULED(16),

    DEACTIVATED(1024);

    private int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<UserStatus> getList() {
        return Arrays.asList(values());
    }
}
