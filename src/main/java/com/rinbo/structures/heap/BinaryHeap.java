package com.rinbo.structures.heap;


import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * 最小二叉堆
 * 特点：父节点小于等于子节点
 * 假设节点k(索引)，其左子节点的索引k*2+1，右子节点的索引(k+1)*2
 * 假设节点k(索引)，其父节点索引(k-1)/2
 */
public class BinaryHeap {

    private final static int DEFAULT_SIZE = 11;

    private int size = 0;
    private int[] array;

    public BinaryHeap() {
        array = new int[DEFAULT_SIZE];
    }

    //父节点索引
    public int parent(int i) {
        return (i - 1) >>> 1;
    }

    //左节点索引
    public int left(int i) {
        return (i << 1) + 1;
    }

    //右节点索引
    public int right(int i) {
        return (i + 1) << 1;
    }

    //是否有左节点
    public boolean hasLeft(int i) {
        return left(i) < size;
    }

    //是否有右节点
    public boolean hasRight(int i) {
        return right(i) < size;
    }

    //简单的扩容实现
    protected int[] resize() {
        return Arrays.copyOf(array, array.length * 2);
    }

    public boolean hasParent(int i) {
        return i > 0;
    }

    //插入操作
    //首先插入到数组的末尾，然后与其父节点比较，如果小于父节点，则和父节点交换
    public void add(int e) {
        if (size == array.length) {
            array = resize();
        }
        //i：节点插入到数组的下标位置，并且size+1
        int i = size++;

        //循环比较父节点
        while (hasParent(i)) {
            int p = parent(i);
            //如果e节点的值大于父节点的值
            if (e > array[p]) {
                break;
            }
            //e与父节点交换
            array[i] = array[p];
            //将父索引赋值给i
            i = p;
            //跳出循环
        }
        //最后将插入的值赋值给i
        array[i] = e;
    }

    /**
     * 将根节点从堆中移出
     * 将尾元素放到根节点，然后比较孩子节点：
     * ①如果存在孩子节点比新的节点小，将新节点和最小的孩子节点替换位置
     * ②重复步骤①直到到达叶子节点，或者孩子节点都比最后一个元素要大
     *
     * @return
     */
    public int poll() {
        if (size == 0) {
            return -1;
        }
        //1、取出根节点
        int f = array[0];
        //2、取出尾节点，并将size -1
        int t = array[--size];
        int i = 0;
        //如果有左节点
//        while (size !=0 && i<(size >>> 1) ) {
        while (hasLeft(i)){
            //首先得到左子节点的位置
            int child = left(i);
            int c = array[child];
            int right = child + 1;
            //如果存在右子节点，并且左子节点大于左子节点
            if (right < size && (c > array[right])) {
                //将孩子节点的下标设置为右子节点的坐标
                c = array[child = right];
            }
            //如果父节点小于子节点
            if (t <= c) {
                //跳出循环
                break;
            }
            //子节点的值赋值给父节点
            array[i] = array[child];
            //从子节点处开始，从新迭代
            i = child;
        }
        //最后将末节点赋值给合适的节点
        array[i] = t;
        return f;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(array[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        BinaryHeap heap = new BinaryHeap();
        Random r = new Random();
        int length = 100;
        int[] a = new int[length];
        for (int i = 0; i < a.length; i++) {
            a[i] = r.nextInt(length * 10);
        }
        for (int e : a) {
            heap.add(e);
        }

        PriorityQueue queue = new PriorityQueue(110);
        for (int e : a) {
            queue.add(e);
        }

        System.out.println(heap);
        System.out.println(queue);

        System.out.println("current : " + heap);
        for (int i = 0; i < 40; i++) {
            int e = heap.poll();
            System.out.println("poll :" + e + ",current : " + heap);
        }
        System.out.println("-------------------------------------------");
        for (int i = 0; i < 40; i++) {
            int e = (int) queue.poll();
            System.out.println("poll :" + e + ",current : " + heap);
        }
    }


}
