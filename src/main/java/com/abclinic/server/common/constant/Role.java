package com.abclinic.server.common.constant;

import com.abclinic.server.model.entity.user.*;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    PRACTITIONER(Practitioner.class, 0),
    SPECIALIST(Specialist.class, 1),
    DIETITIAN(Dietitian.class, 2),
    COORDINATOR(Coordinator.class, 3),
    PATIENT(Patient.class, 4);

    public static Optional<Role> valueOf(int value) {
        return Arrays.stream(values())
                .filter(role -> role.ordinal() == value)
                .findFirst();
    }

    private final Class roleClass;
    private int value;

    Role(Class roleClass, int value) {
        this.roleClass = roleClass;
        this.value = value;
    }

    public Class getRoleClass() {
        return roleClass;
    }

    public int getValue() {
        return value;
    }
}