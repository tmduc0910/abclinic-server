package com.abclinic.server.constant;

import com.abclinic.server.model.entity.user.*;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    PRACTITIONER(Practitioner.class),
    SPECIALIST(Specialist.class),
    DIETITIAN(Dietitian.class),
    COORDINATOR(Coordinator.class),
    PATIENT(Patient.class);

    public static Optional<Role> valueOf(int value) {
        return Arrays.stream(values())
                .filter(role -> role.ordinal() == value)
                .findFirst();
    }

    private final Class roleClass;

    Role(Class roleClass) {
        this.roleClass = roleClass;
    }

    public Class getRoleClass() {
        return roleClass;
    }
}