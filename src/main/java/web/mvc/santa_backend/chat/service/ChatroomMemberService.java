package web.mvc.santa_backend.chat.service;

import org.springframework.web.socket.WebSocketSession;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberResDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

public interface ChatroomMemberService {

    /**
     * 채팅방 id로 그 채팅방에 참여하고 있는 user를 반환하는 메서드
     * isBanned로 참여중인지, 강퇴당했는지를 판단.
     * @param chatroomId 조회할 채팅방번호
     * @param isBanned 강퇴여부
     * @return 조회된 사용자 목록
     */
    public List<ChatroomMemberResDTO> getChatroomMembers(Long chatroomId, boolean isBanned, Long userId);

    /**
     * 유저의 채팅방 입장메서드
     * @param chatroomMemberDTO
     */
    void enterChatroom(ChatroomMemberDTO chatroomMemberDTO, WebSocketSession session);
    
    /**
     * 참여멤버를 DB에 추가하는 메서드
     * 필수 파라미터
     * Long userId
     * Long chatroomId
     * 선택 파라미터
     * boolean noteoff(알림 on/off 설정, default false(0))
     * UserRole role(역할, admin/user, default user)
     * @param chatroomMemberDTO 필수 파라미터 Long userId, Long chatroomId, 선택 파라미터 boolean noteOff, UserRole role
     */
    ChatroomMemberResDTO createChatroomMember(ChatroomMemberDTO chatroomMemberDTO);

    /**
     * 자신의 상태를 변경하는 메서드
     * 1. 알림끄기
     * 2. 마지막 읽은 메시지id 변경
     * 3. 강퇴여부
     * 4. role 변경
     * @param userId 현재 이 요청을 보낸 userId
     * @param chatroomMemberDTO 선택파라미터 boolean noteOff, UserRole role, boolean isBanned, Long lastRead
     */
    void updateChatroomMember(Long userId, ChatroomMemberDTO chatroomMemberDTO);

    /**
     * 채팅방 퇴장, 혹은 강퇴한 사람이 강퇴여부를 삭제하는데 사용
     * 강퇴와 다른 점은 chatroomMember의 레코드가 실제로 삭제된다는 것.
     * 강퇴는 chatroomMembers의 isBanned의 값을 true로 바꾼다...
     * @param userId
     * @param chatroomId
     */
    void deleteChatroomMember(Long userId, String username, Long chatroomId);

    /**
     * 채팅방에 현재 참여중인 유저인지, 처음 참여하는 유저인지를 확인하기 위한 메서드
     * @param userId
     * @param chatroomId
     * @return
     */
    boolean checkChatroomMember(Long userId, Long chatroomId);

    Long countChatroomMember(Long chatroomId);

    /**
     * 첫 입장시 입장 메시지 보낸 이후, 입장메시지 보냈는지의 여부를 true로 바꿈
     */
    void updateNoticeSent(Long chatroomMemberId);

    UserRole getUserRole(Long userId, Long chatroomId);
}
