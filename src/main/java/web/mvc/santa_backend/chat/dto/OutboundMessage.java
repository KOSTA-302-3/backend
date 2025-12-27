package web.mvc.santa_backend.chat.dto;

import web.mvc.santa_backend.common.enumtype.MessageType;

public interface OutboundMessage {
    public MessageType getMessageType();
}
