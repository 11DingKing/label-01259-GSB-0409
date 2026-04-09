import request from "./request";

export const applyCreator = (data) => request.post("/creator/apply", data);

export const getMyCreatorProfile = () => request.get("/creator/profile");

export const updateCreatorProfile = (data) =>
  request.put("/creator/profile", data);

export const getCreatorList = (params) =>
  request.get("/creator/list", { params });

export const getCreatorDetail = (id) => request.get(`/creator/${id}`);

export const getCategories = () => request.get("/creator/categories");
