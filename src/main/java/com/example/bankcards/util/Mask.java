package com.example.bankcards.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Mask {
    public static String mask(String target){
        return "**** **** **** " + target;
    }
}
