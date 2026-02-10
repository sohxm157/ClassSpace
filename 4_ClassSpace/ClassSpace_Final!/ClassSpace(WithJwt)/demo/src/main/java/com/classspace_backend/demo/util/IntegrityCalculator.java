package com.classspace_backend.demo.util;

import com.classspace_backend.demo.entity.ActualStatus;
import com.classspace_backend.demo.entity.DeclaredStatus;

public class IntegrityCalculator {

    public static boolean isHonest(DeclaredStatus d, ActualStatus a) {
        return (d == DeclaredStatus.YES && a == ActualStatus.PRESENT)
            || (d == DeclaredStatus.NO && a == ActualStatus.ABSENT);
    }
}
