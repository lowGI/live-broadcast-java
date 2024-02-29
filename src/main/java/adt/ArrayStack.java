package adt;

import java.util.Iterator;

public class ArrayStack<Comment> implements StackInterface<Comment>{
    private Comment[] array;
    private int top;
    private int size;

    //default array size
    public ArrayStack(){
        size = 5;
        array = (Comment[]) new Object[size];
        top = -1;
    }

    //self defined array size
    public ArrayStack(int size){
        this.size = size;
        array = (Comment[]) new Object[size];
        top = -1;
    }

    @Override
    public void push(Comment newComment) {
        top++;
        if(isFull()) doubleArray();
        array[top] = newComment;
    }

    @Override
    public Comment peek() {
        if(isEmpty()) return null;
        return array[top];
    }

    @Override
    public Comment pop() {
        if(isEmpty()) return null;
        array[top] = null;
        top--;
        return peek();
    }

    @Override
    public boolean isEmpty() {
        return top < 0;
    }

    @Override
    public void clear() {
        top = -1;
    }

    private boolean isFull() {
        return top == size;
    }

    //user cannot double it by themselves
    private void doubleArray(){
        //auto double the size of array
        Comment[] oldArray = array;
        array= (Comment[]) new Object[size * 2];
        System.arraycopy(oldArray, 0, array, 0, size);
        //update size
        size *= 2;
    }



    @Override
    public Iterator<Comment> getIterator() {
        return new ArrayStackIterator();
    }

    private class ArrayStackIterator implements Iterator<Comment> {
        private int bottom;

        private ArrayStackIterator() {
            bottom = 0;
        }

        @Override
        public boolean hasNext() {
            return bottom <= top;
        }

        @Override
        public Comment next() {
            if(!hasNext()) return null;
            Comment comment = array[bottom];
            bottom++;
            return comment;
        }
    }
}
