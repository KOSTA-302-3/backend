package web.mvc.santa_backend.chat.service;

import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
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
    public List<UserSimpleDTO> getChatroomMembers(Long chatroomId, boolean isBanned);

    /**
     * user가 채팅방에 참여하는 메서드(채팅 멤버가 추가되는 메서드)
     * 필수 파라미터
     * Long userId
     * Long chatroomId
     * 선택 파라미터
     * boolean noteoff(알림 on/off 설정, default false(0))
     * UserRole role(역할, admin/user, default user)
     * @param chatroomMemberDTO 필수 파라미터 Long userId, Long chatroomId, 선택 파라미터 boolean noteOff, UserRole role
     */
    void createChatroomMember(ChatroomMemberDTO chatroomMemberDTO);

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
     * @param chatroomMemberId
     */
    void deleteChatroomMember(Long chatroomMemberId);
}
