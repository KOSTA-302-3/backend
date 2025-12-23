package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.LikeType;
import web.mvc.santa_backend.post.dto.LikeDTO;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.LikesRepository;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.user.entity.Likes;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RepliesRepository repliesRepository;
    @Autowired
    PostResository postResository;

    @Transactional
    @Override
    public String likeReplies(Long targetId, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Replies reply = repliesRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        Optional<Likes> existingLike = likesRepository.findByTargetIdAndUser(targetId, user);

        if (existingLike.isEmpty()) {

            Likes newLike = Likes.builder()
                    .user(user)
                    .likeType(LikeType.REPLY)
                    .createdAt(LocalDateTime.now())
                    .targetId(targetId)
                    .build();
            likesRepository.save(newLike);

            reply.increaseLikeCount();

            return "add Like";

        } else {

            likesRepository.delete(existingLike.get()); // 이미 조회한 객체 삭제

            reply.decreaseLikeCount();

            return "cancel like";
        }
    }
    @Transactional
    @Override
    public String postReplies(Long targetId, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Posts post = postResository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        Optional<Likes> existingLike = likesRepository.findByTargetIdAndUser(targetId, user);

        if (existingLike.isEmpty()) {

            Likes newLike = Likes.builder()
                    .user(user)
                    .likeType(LikeType.POST)
                    .createdAt(LocalDateTime.now())
                    .targetId(targetId)
                    .build();
            likesRepository.save(newLike);

            post.increaseLikeCount();

            return "add Like";

        } else {

            likesRepository.delete(existingLike.get()); // 이미 조회한 객체 삭제

            post.decreaseLikeCount();

            return "cancel like";
        }
    }

    @Transactional
    @Override
    public boolean ckLike(LikeDTO likeDTO) {
        boolean ck = false;
        Users user = userRepository.findById(likeDTO.getUserId()).get();
        if (likesRepository.findByTargetIdAndUser(likeDTO.getTargetId(), user).isEmpty()) {
            return false;
        }
        else ck = true;
        System.out.println(ck);

        return true;
    }



}
