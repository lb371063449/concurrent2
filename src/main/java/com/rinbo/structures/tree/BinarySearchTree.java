package com.rinbo.structures.tree;

import com.rinbo.structures.queue.LinkedQueue;

public class BinarySearchTree<T extends Comparable> {

    protected BinaryNode root;

    public BinarySearchTree() {
        root = null;
    }

    public int size(BinaryNode<T> node) {
        if (node == null) {
            return 0;
        } else {
            return 1 + size(node.left) + size(node.right);
        }
    }

    public int height(BinaryNode<T> node) {
        if (node == null) {
            return 0;
        } else {
            int l = height(node.left);
            int r = height(node.right);
            return (l > r) ? l + 1 : r + 1;
        }
    }

    //先根
    public String preOrder(BinaryNode<T> node) {
        if (node == null) {
            return "";
        }
        return node.data + "#" + preOrder(node.left) + "#" + preOrder(node.right);
    }

    //中根
    public String inOrder(BinaryNode<T> node) {
        if (node == null) {
            return "";
        }
        return preOrder(node.left) + "#" + node.data + "#" + preOrder(node.right);
    }

    //后根
    public String postOrder(BinaryNode<T> node) {
        if (node == null) {
            return "";
        }
        return preOrder(node.left) + "#" + preOrder(node.right) + "#" + node.data;
    }

    //层级
    public void levelOrder(BinaryNode<T> node) {
        LinkedQueue<BinaryNode<T>> queue = new LinkedQueue<BinaryNode<T>>();
        if (node != null) {
            queue.add(node);
        }
        while (!queue.isEmpty()) {
            BinaryNode<T> current = queue.poll();
            System.out.print(current.data + "-");
            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }
        System.out.println();
    }

    public void insert(T data) {
        if (data == null) {
            throw new RuntimeException("data can\'Comparable be null !");
        }
        root = insert(data, root);
    }

    private BinaryNode<T> insert(T data, BinaryNode<T> p) {
        //如果当前节点为空，当前插入的值作为当前节点
        if (p == null) {
            p = new BinaryNode<>(data, null, null);
        }
        //比较插入结点的值，决定向左子树还是右子树搜索
        int compare = data.compareTo(p.data);
        //向左查找插入
        if (compare < 0) {
            p.left = insert(data, p.left);
            //向右查找插入
        } else if (compare > 0) {
            p.right = insert(data, p.right);
        } else {
            //已有元素就没必要重复插入了
        }
        //返回当前节点树
        return p;
    }

    /**
     * 分3种情况
     * 1.删除叶子结点(也就是没有孩子结点)
     * 2.删除拥有一个孩子结点的结点(可能是左孩子也可能是右孩子)
     * 3.删除拥有两个孩子结点的结点
     *
     * @param data
     * @return
     */
    public BinaryNode<T> remove(T data, BinaryNode<T> node) {
        if (node == null) {
            return node;
        }
        if (data.compareTo(node.data) < 0) {
            node.left = remove(data, node.left);
        } else if (data.compareTo(node.data) > 0) {
            node.right = remove(data, node.right);
        } else if (node.left != null && node.right != null) {
            //删除拥有两个孩子结点的结点
            //中继替换，找到右子树中最小的元素并替换需要删除的元素值
            node.data = findMin(node.right).data;
            //移除用于替换的结点
            node.right = remove(node.data, node.right);
        } else {
            //拥有一个孩子结点的结点和叶子结点的情况
            node = node.left != null ? node.left : node.right;
        }
        return node;
    }

    public BinaryNode<T> findMin(BinaryNode<T> node) {
        if (node == null) {
            throw new RuntimeException("树为空");
        }
        if (node.left == null) {
            return node;
        }
        return findMin(node.left);
    }

    public BinaryNode<T> findMax(BinaryNode<T> node) {
        if (node == null) {
            throw new RuntimeException("树为空");
        }
        if (node.right == null) {
            return node;
        }
        return findMax(node.right);
    }

    public boolean contains(T data, BinaryNode<T> node) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("argument error");
        }
        if (node != null) {
            if (data.equals(node.data)) {
                return true;
            } else if (data.compareTo(node.data) < 0) {
                return contains(data, node.left);
            } else {
                return contains(data, node.right);
            }
        }
        return false;
    }

    public void clear() {
        root = null;
    }

}
