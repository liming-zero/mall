package com.atguigu.gulimall.seckill.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BathProcessData {

    private Integer BATCH_SIZE = 2000;

    public void main() {
        List<Object> dataList = new ArrayList<>();
        dataList.add(new Object());

        List<Object> tempList;
        int startIndex = 0;
        int endIndex = BATCH_SIZE;
        int listSize = dataList.size();
        endIndex = Math.min(endIndex, listSize);
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        while (true){
            tempList = dataList.subList(startIndex, endIndex);
            CompletableFuture<Integer> future = batchSave(tempList);
            futures.add(future);
            if (endIndex == listSize){
                break;
            }
            startIndex += BATCH_SIZE;
            endIndex += BATCH_SIZE;
            endIndex = Math.min(endIndex, listSize);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<Integer> resList = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new IllegalArgumentException();
            }
        }).collect(Collectors.toList());
    }

    private CompletableFuture<Integer> batchSave(List<Object> tempList) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            //执行业务
            batchSave(tempList);
            return 10;
        });
        return future;
    }
}
