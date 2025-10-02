package com.example.bankcards.util;

/** Класс генерирующий случайный PAN код для каждой новой карты */
public class PanGenerator {

     public static String generatePan(){
          long minValue = 1_000_000_000_000_000L;
          long maxValue = 9_999_999_999_999_999L;

          double randomDouble = Math.random() * (maxValue - minValue + 1);
          long panNumber = minValue + (long) randomDouble;

         return String.valueOf(panNumber);
     }




}
