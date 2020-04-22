package com.abclinic.server.common.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.constant
 * @created 12/30/2019 2:49 PM
 */
public class Status {
    public static class User {
        public static final int ACTIVATED = 0;
        public static final int DEACTIVATED = 1;
        public static final int UNASSIGNED = 2;
    }

    public static class Payload {
        public static final int UNREAD = 0;
        public static final int ON_HOLD = 1;
        public static final int PROCESSED = 2;
    }
}
