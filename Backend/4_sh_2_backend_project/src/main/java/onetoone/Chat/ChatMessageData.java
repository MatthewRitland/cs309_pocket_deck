package onetoone.Chat;


// doesnt actually store data, right now just is used to recieve incoming messages
// from websocket (the JSON from the websocket)
// @Data //uses lambok that makes getters and setters -> seems to have been a maven dependency from the tutorials
public class ChatMessageData {
    private String sender; //who sent this message content?

    private MessageAction action; // will be SEND, LEAVE... is the action the user wants inside of a @OnMessage
    private String messageContent;
    private Long groupChatId; //what group does this message content belong to?

    public ChatMessageData() {}

    // =============================== Getters and Setters for each field ================================== //
    public String getSender() { return this.sender; }
    public void setSender(String sender) { this.sender = sender; }

    public MessageAction getAction() { return this.action; }
    public void setMessageAction(MessageAction action) { this.action = action; }

    public String getMessageContent() { return this.messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Long getGroupChatId() { return this.groupChatId; }
    public void setGroupChatId(Long groupChatId) { this.groupChatId = groupChatId; }
    // TODO
    // usually don't want to change ids, but this might be helpful since this class is
    // a DTO (data transfer object) so might need it later?


}
