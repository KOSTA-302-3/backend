package web.mvc.santa_backend.chat.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomRequestDTO;
import web.mvc.santa_backend.chat.dto.ChatroomResponseDTO;

public interface ChatroomService {
    /**
     * 채팅방 만들기
     * 필수 파라미터
     * String name(채팅방 이름)
     * 선택 파라미터
     * Boolean isPrivate(비공개방인지)
     * String password(비밀번호가 있는지)
     * String imageUrl(대표이미지가 있는지)
     * String description(상세 설명이 있는지)
     *
     * @param chatroomRequestDTO
     * @return
     */
    Long createChatroom(ChatroomRequestDTO chatroomRequestDTO);

    /**
     * userId, word(검색시 입력한 단어)로 chatrooms를 가지고 오는 메서드
     * userId, word 둘다 null일 경우 - 모든 채팅방
     * userId만 있는 경우 - user가 속해있는 채팅방
     * word만 있는 경우 - word로 제목 검색된 채팅방
     * 둘 다 있는 경우 - user가 속해있는 채팅방 중 word로 제목 검색된 채팅방
     * @param word
     * @param page
     * @return
     */
    public Page<ChatroomResponseDTO> getChatrooms(Long userId, String word, int page);

    /**
     * 채팅방의 제목, 비밀번호, 공개상태, 설명, 이미지를 바꾸는 메서드
     * 수정할 내용만 DTO에 입력
     * @param chatroomDTO
     */
    public void updateChatroom(ChatroomDTO chatroomDTO, Long userId);

    /**
     * 채팅방의 isDelete 상태를 true로 변경(소프트 딜리트)
     * @param id
     */
    public void deleteChatroom(Long id);
}
