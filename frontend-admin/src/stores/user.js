import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { login as loginApi } from "@/api";

export const useUserStore = defineStore("user", () => {
  const token = ref(localStorage.getItem("admin_token") || "");
  const userInfo = ref(
    JSON.parse(localStorage.getItem("admin_userInfo") || "null"),
  );

  const isLoggedIn = computed(() => !!token.value);
  const isAdmin = computed(() => userInfo.value?.role === 2);

  const login = async (data) => {
    const res = await loginApi(data);
    if (res.data.role !== 2) {
      throw new Error("无管理员权限");
    }
    token.value = res.data.token;
    userInfo.value = res.data;
    localStorage.setItem("admin_token", res.data.token);
    localStorage.setItem("admin_userInfo", JSON.stringify(res.data));
    return res.data;
  };

  const logout = () => {
    token.value = "";
    userInfo.value = null;
    localStorage.removeItem("admin_token");
    localStorage.removeItem("admin_userInfo");
  };

  return { token, userInfo, isLoggedIn, isAdmin, login, logout };
});
