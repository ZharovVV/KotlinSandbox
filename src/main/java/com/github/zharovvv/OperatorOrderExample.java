package com.github.zharovvv;

import java.util.Arrays;

public class OperatorOrderExample {

    public static void main(String[] args) {
        int[] a = new int[3];
        int index = 0;
        a[index = index + 1] = 1;
        System.out.println(Arrays.toString(a)); //[0, 1, 0]
        System.out.println("index=" + index);   //index=1
        a[--index] = 2;
        System.out.println(Arrays.toString(a)); //[2, 1, 0]
        System.out.println("index=" + index);   //index=0
        a[index++] = 3;
        System.out.println(Arrays.toString(a)); //[3, 1, 0]
        System.out.println("index=" + index);   //index=1
    }
}
