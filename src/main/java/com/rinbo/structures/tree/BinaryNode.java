package com.rinbo.structures.tree;

import java.io.Serializable;

public class BinaryNode<T extends Comparable> implements Serializable {

    public BinaryNode<T> left;

    public BinaryNode<T> right;

    public T data;

    public BinaryNode(T data, BinaryNode left, BinaryNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public BinaryNode(T data) {
        this(data, null, null);
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }
}
