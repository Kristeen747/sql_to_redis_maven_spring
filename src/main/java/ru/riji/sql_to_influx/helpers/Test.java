package ru.riji.sql_to_influx.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {

        List<String> list = new ArrayList<>();

        list.add("ci1923123-ids-appeal-data");
        list.add("ci1923123-iprom-appeal-data");
        list.add("ci1932423-idm-appeal-operation");
        list.add("ci1932423-ids-appeal-operation");
        list.add("ci1932423-idm-chat-x-207-deploy-2sd34324sdf");
        list.add("ufs-ids-appeal-2sdf213sdf");
        list.add("ufs-idm-appeal-2sdf215sdf");
        list.add("ci1932423-iprom-chat-x-207-deploy-2sdf216sdf");
        list.add("ci1932423-iprom-chat-x-207-deploy-2sdf254sdf");
        list.add("ci1932423-iprom-chat-x-207-deploy-2s234216sdf");


        transform(list);

    }


   static void transform(List<String> items){
        String[][] splitArr = new String[items.size()][];
        int[][] findVal = new int[items.size()][];
        int[][] counterVal = new int[items.size()][items.size()];

        for (int i = 0; i < items.size(); i++) {
           String[] arr = items.get(i).split("-");
           splitArr[i] = arr;
           findVal[i] = new int[arr.length];
        }

       for (int i = 0; i < items.size()-1; i++) {
           for (int j = i+1; j < items.size() ; j++) {
               if(splitArr[i].length == splitArr[j].length){
                   int counter = 0;
                   for (int k = 0; k < splitArr[i].length ; k++) {
                       if(!splitArr[i][0].equals(splitArr[j][0])) {
                            break;
                       }
                       String s1 = splitArr[i][k];
                       String s2 = splitArr[j][k];

                       System.out.println("s1: "+s1 + " s2: " + s2);

                       if(s1.equals(s2)){
                           findVal[i][k] = 1;
                           findVal[j][k] = 1;
                           counter++;
                       }
                   }
                       counterVal[i][j] = counter;
                       counterVal[j][i] = counter;
               }
           }
       }
       Map<String, String> map = new HashMap<>();

       for (int i = 0; i < counterVal.length ; i++) {
           int max = 0;
           for (int j = 0; j < counterVal[i].length; j++) {
               if (counterVal[i][j] > counterVal[i][max]) {
                   max = j;
               }
           }

               StringBuilder sb = new StringBuilder();
               for (int k = 0; k < findVal[i].length; k++) {
                   if (findVal[i][k] == 1) {
                       if (k > 0) {
                           sb.append("-");
                       }
                       sb.append(splitArr[i][k]);
                   }
               }

               map.put(items.get(i), sb.toString());
               map.put(items.get(max), sb.toString());

       }

       map.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}
