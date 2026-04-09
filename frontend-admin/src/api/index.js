import request from "./request";

export const login = (data) => request.post("/auth/login", data);

export const getUserList = (params) => request.get("/admin/users", { params });
export const updateUserStatus = (id, status) =>
  request.put(`/admin/user/${id}/status`, null, { params: { status } });

export const getPendingCreators = (params) =>
  request.get("/admin/creators", { params });
export const auditCreator = (id, status) =>
  request.put(`/admin/creator/${id}/audit`, null, { params: { status } });

export const getContentList = (params) =>
  request.get("/admin/contents", { params });
export const getContentDetail = (id) => request.get(`/admin/content/${id}`);
export const getAdminContentDetail = (id) =>
  request.get(`/admin/content/${id}`);
export const updateContentStatus = (id, status) =>
  request.put(`/admin/content/${id}/status`, null, { params: { status } });
export const deleteContent = (id) => request.delete(`/admin/content/${id}`);

export const getStatistics = () => request.get("/admin/statistics");
export const getOperationLogs = (params) =>
  request.get("/admin/logs", { params });
