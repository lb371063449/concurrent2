package com.rinbo.concurrent.chapter3;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolDemo {

    //RecursiveTask有返回值
    public static class CountTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private long start;
        private long end;

        public CountTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        protected Long compute() {
            long sum = 0;
            boolean canCompute = (end-start)<THRESHOLD;
            if (canCompute) {
                for (long i=start;i<=end;i++) {
                    sum +=i;
                }
            } else {
                long step = (end + start)/100;
                ArrayList<CountTask> list = new ArrayList<>();
                long pos = start;
                for (int i=0;i<100;i++) {
                    long lastOne = pos + step;
                    if (lastOne>end) {
                        lastOne = end;
                    }
                    CountTask task = new CountTask(pos,lastOne);
                    pos += step + 1;
                    list.add(task);
                    //用fork提交子任务到线程池
                    task.fork();
                }
                for (CountTask task:list) {
                    //所有的子任务执行完成后再求和
                    sum += task.join();
                }
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        //创建ForkJoinPool线程池
        ForkJoinPool pool = new ForkJoinPool();
        //创建ForkJoinTask
        CountTask task = new CountTask(0,200000l);
        //执行ForkJoinTask
        ForkJoinTask<Long> result = pool.submit(task);
        try {
            //获取最终结果
            Long resu = result.get();
            System.out.println(resu);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
