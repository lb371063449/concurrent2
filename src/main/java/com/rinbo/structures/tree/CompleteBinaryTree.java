package com.rinbo.structures.tree;

public class CompleteBinaryTree<T extends Comparable> extends BinarySearchTree {

    public CompleteBinaryTree() {
        super();
    }

    public CompleteBinaryTree(T[] levelOrderArray) {
        if (levelOrderArray == null) {
            throw new RuntimeException("the param 'array' of create method can\'t be null !");
        }
        root = create(levelOrderArray,0);
    }

    public BinaryNode<T> create(T[] levelOrderArray, int i) {
        if (levelOrderArray == null) {
            throw new RuntimeException("the param 'array' of create method can\'t be null !");
        }
        BinaryNode<T> node = null;
        if (i < levelOrderArray.length) {
            node = new BinaryNode(levelOrderArray[i]);
            node.left = create(levelOrderArray,2*i+1);
            node.right = create(levelOrderArray,2*i+2);
        }
        return node;
    }
}
