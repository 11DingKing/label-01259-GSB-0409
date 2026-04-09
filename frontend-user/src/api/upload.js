import request from "./request";

export const uploadImage = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/upload/image", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const uploadVideo = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/upload/video", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 300000, // 5分钟超时
  });
};

export const uploadAudio = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/upload/audio", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 300000,
  });
};

export const uploadImages = (files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));
  return request.post("/upload/images", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 300000,
  });
};
