import request from "./request";

export const createContent = (data) => request.post("/content", data);

export const updateContent = (id, data) => request.put(`/content/${id}`, data);

export const deleteContent = (id) => request.delete(`/content/${id}`);

export const getContentDetail = (id) => request.get(`/content/${id}`);

export const getContentList = (params) =>
  request.get("/content/list", { params });

export const getCreatorContents = (creatorId, params) =>
  request.get(`/content/creator/${creatorId}`, { params });

export const getMyContents = (params) => request.get("/content/my", { params });

export const likeContent = (id) => request.post(`/content/${id}/like`);

export const purchaseContent = (id) => request.post(`/content/${id}/purchase`);
