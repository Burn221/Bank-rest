package com.example.bankcards.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Класс отвечающий за маскирование PAN */
@Data
@NoArgsConstructor
public class Mask {
    public static String mask(String target){
        return "**** **** **** " + target;
    }
}
