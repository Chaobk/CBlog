package com.chaobk.service;

import com.chaobk.model.vo.Friend;
import com.chaobk.model.vo.FriendInfo;

import java.util.List;

public interface FriendService {
	List<com.chaobk.entity.Friend> getFriendList();

	List<Friend> getFriendVOList();

	void updateFriendPublishedById(Long friendId, Boolean published);

	void saveFriend(com.chaobk.entity.Friend friend);

	void updateFriend(com.chaobk.model.dto.Friend friend);

	void deleteFriend(Long id);

	void updateViewsByNickname(String nickname);

	FriendInfo getFriendInfo(boolean cache, boolean md);

	void updateFriendInfoContent(String content);

	void updateFriendInfoCommentEnabled(Boolean commentEnabled);
}
