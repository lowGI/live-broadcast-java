package adt;

import java.util.Iterator;

public interface StackInterface<Comment>{
    public void push(Comment newComment);
    public Comment peek();
    public Comment pop();
    public boolean isEmpty();
    public void clear();
    public Iterator<Comment> getIterator();
}
