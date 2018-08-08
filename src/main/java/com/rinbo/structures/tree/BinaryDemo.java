package com.rinbo.structures.tree;

public class BinaryDemo {

    public static void main(String[] args) throws Exception {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(5);
        tree.insert(2);
        tree.insert(77);
        tree.insert(-5);
        tree.insert(8);
        tree.insert(33);
        tree.insert(24);
        tree.insert(1);
        System.out.println(tree.findMin(tree.root).data);
        System.out.println(tree.findMax(tree.root).data);
        System.out.println(tree.contains(1,tree.root));
        System.out.println(tree.height(tree.root));
        System.out.println(tree.size(tree.root));
        System.out.println(tree.preOrder(tree.root));
        System.out.println(tree.inOrder(tree.root));
        System.out.println(tree.postOrder(tree.root));
        tree.levelOrder(tree.root);
        tree.levelOrder(tree.remove(8,tree.root));
    }
}
