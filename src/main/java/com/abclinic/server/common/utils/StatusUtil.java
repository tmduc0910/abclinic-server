package com.abclinic.server.common.utils;

import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.model.entity.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.utils
 * @created 4/22/2020 9:18 AM
 */
public class StatusUtil {
    public static List<UserStatus> decode(User user) {
        List<UserStatus> list = new ArrayList<>();
        UserStatus.getList().forEach(s -> {
            if (containsStatus(user, s))
                list.add(s);
        });
        return list;
    }

    public static int encode(UserStatus... statuses) {
        return Arrays.stream(statuses)
                .mapToInt(UserStatus::getValue)
                .reduce((a, b) -> a & b)
                .orElse(UserStatus.NEW.getValue());
    }

    public static boolean containsStatus(User user, UserStatus status) {
        return BinaryUtils.and(user.getStatus(), status.getValue()) != 0;
    }
}
