package db;

import adt.ArrayStack;
import adt.StackInterface;
import com.mongodb.client.*;
import com.mongodb.client.MongoCollection;
import entity.Chat;
import entity.Comment;
import entity.Donation;
import org.bson.*;
import java.util.Iterator;

public class DBCommentConnection {
    private final MongoClient client = MongoClients.create("mongodb+srv://JATLvDB:JatlDA14@jatlvcluster.hkuyj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
    private final MongoDatabase db = client.getDatabase("Grocer");
    private final MongoCollection col = db.getCollection("Comment");

    public StackInterface<Comment> getCommentFromDB() {
        StackInterface<Comment> commentStack = new ArrayStack<Comment>();
        FindIterable<Document> iterDoc = col.find();
        MongoCursor<Document> cursor = iterDoc.iterator();

        //probably have to use some list or stuff...
        try {
            while (cursor.hasNext()) {
                Document comment = cursor.next();
                Object[] elements = comment.values().toArray();

                if (comment.get("type").equals("chat")) {
                    commentStack.push(new Chat(
                            elements[0].toString().split("/")[0],
                            Long.parseLong(elements[0].toString().split("/")[1]),
                            Boolean.parseBoolean(elements[2].toString()),
                            elements[3].toString()));
                } else if (comment.get("type").equals("donation")) {
                    commentStack.push(new Donation(
                            elements[0].toString().split("/")[0],
                            Long.parseLong(elements[0].toString().split("/")[1]),
                            Boolean.parseBoolean(elements[2].toString()),
                            Integer.parseInt(elements[3].toString())));
                } else {
                    System.out.println("Unexpected comment type found in database.");
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return commentStack;
    }

    public void saveCommentToDB(StackInterface<Comment> commentStack) {
        //drop all data in db
        col.drop();
        Iterator<Comment> cursor = commentStack.getIterator();

        try {
            while (cursor.hasNext()) {
                Comment comment = cursor.next();
                if (comment instanceof Chat) {
                    Document doc = new Document("_id", comment.getUsername() + "/" + comment.getSendTime()).append("type", "chat")
                            .append("isAdmin", comment.isAdmin()).append("content", ((Chat) comment).getContent());
                    col.insertOne(doc);
                }else if(comment instanceof Donation){
                    Document doc = new Document("_id", comment.getUsername() + "/" + comment.getSendTime()).append("type", "donation")
                            .append("isAdmin", comment.isAdmin()).append("amount", ((Donation) comment).getAmount());
                    col.insertOne(doc);
                }else{
                    System.out.println("Unrecognised comment type found in stack.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clearCommentInDB(StackInterface<Comment> commentStack){
        if(commentStack.peek() == null) col.drop();
    }
}