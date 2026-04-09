import request from "./request";

export const followCreator = (creatorId) =>
  request.post(`/follow/${creatorId}`);

export const unfollowCreator = (creatorId) =>
  request.delete(`/follow/${creatorId}`);

export const getFollowList = (params) =>
  request.get("/follow/list", { params });

export const reward = (data) => request.post("/reward", data);

export const getRewardList = (params) =>
  request.get("/reward/list", { params });

export const getComments = (contentId, params) =>
  request.get(`/comment/content/${contentId}`, { params });

export const addComment = (data) => request.post("/comment", data);

export const deleteComment = (id) => request.delete(`/comment/${id}`);
