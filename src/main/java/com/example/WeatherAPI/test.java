package com.example.WeatherAPI;

import java.util.*;

public class test {

    public static void main(String[] args) {
        //nInput: set[] = {3, 34, 4, 12, 5, 2}
        Set<Integer> set = new LinkedHashSet<>();  // Use LinkedHashSet to maintain insertion order
        set.add(3);
        set.add(34);
        set.add(4);
        set.add(12);
        set.add(5);
        set.add(2);

        List<Integer> list = new ArrayList<>(set);  // List will maintain the insertion order
        int sum = 9;

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            int initial = list.get(i);
            if (initial == sum) {
                found = true;
                break;
            }
            for (int j = i + 1; j < list.size(); j++) {
                initial += list.get(j);
                if (initial == sum) {
                    found = true;
                    break;
                } else if (initial > sum) {
                    initial -= list.get(j);
                }
            }
            if (found) {
                break;
            }
        }

        System.out.println(found);
    }


}
