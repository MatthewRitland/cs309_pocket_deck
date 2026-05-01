package PocketDeck.Chat;



public class ChatMessageData {
    //private String sender; //who sent this message content?

    private ChatMessageAction action; // will be SEND, LEAVE... is the action the user wants inside of a @OnMessage
    private String messageContent;
    private Long groupChatId; //what group does this message content belong to?

    public ChatMessageData() {}

    // =============================== Getters and Setters for each field ================================== //
    //public String getSender() { return this.sender; }
    //public void setSender(String sender) { this.sender = sender; }

    public ChatMessageAction getMessageAction() { return this.action; }
    public void setMessageAction(ChatMessageAction action) { this.action = action; }

    public String getMessageContent() { return this.messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Long getGroupChatId() { return this.groupChatId; }
    public void setGroupChatId(Long groupChatId) { this.groupChatId = groupChatId; }
}
