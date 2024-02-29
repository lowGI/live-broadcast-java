package entity;

public class Chat extends Comment{
    private String content;

    public Chat(){
        super("",0,false);
    }

    public Chat(String username, long sendTime, boolean isAdmin, String content){
        super(username, sendTime, isAdmin);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString(){
        return super.toString() + " : " + content + "\n";
    }
}
