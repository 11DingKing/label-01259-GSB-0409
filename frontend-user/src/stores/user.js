import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { login as loginApi, getUserInfo } from "@/api/auth";

export const useUserStore = defineStore("user", () => {
  const token = ref(localStorage.getItem("token") || "");
  const userInfo = ref(JSON.parse(localStorage.getItem("userInfo") || "null"));

  const isLoggedIn = computed(() => !!token.value);
  const isCreator = computed(() => userInfo.value?.role === 1);
  const isAdmin = computed(() => userInfo.value?.role === 2);

  const login = async (data) => {
    const res = await loginApi(data);
    token.value = res.data.token;
    userInfo.value = res.data;
    localStorage.setItem("token", res.data.token);
    localStorage.setItem("userInfo", JSON.stringify(res.data));
    return res.data;
  };

  const fetchUserInfo = async () => {
    if (!token.value) return;
    try {
      const res = await getUserInfo();
      // 确保完全更新用户信息，特别是role字段
      userInfo.value = {
        ...userInfo.value,
        ...res.data,
        role: res.data.role, // 显式更新role
      };
      localStorage.setItem("userInfo", JSON.stringify(userInfo.value));
    } catch (error) {
      console.error("获取用户信息失败:", error);
    }
  };

  const logout = () => {
    token.value = "";
    userInfo.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
  };

  const updateBalance = (newBalance) => {
    if (userInfo.value) {
      userInfo.value.balance = newBalance;
      localStorage.setItem("userInfo", JSON.stringify(userInfo.value));
    }
  };

  return {
    token,
    userInfo,
    isLoggedIn,
    isCreator,
    isAdmin,
    login,
    fetchUserInfo,
    logout,
    updateBalance,
  };
});
