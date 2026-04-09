import request from "./request";

export const login = (data) => request.post("/auth/login", data);

export const register = (data) => request.post("/auth/register", data);

export const getUserInfo = () => request.get("/auth/info");

export const updateProfile = (data) => request.put("/auth/profile", data);

export const recharge = (data) => request.post("/auth/recharge", data);

export const resetPassword = (data) =>
  request.post("/auth/reset-password", data);
