import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home.vue"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
  },
  {
    path: "/creators",
    name: "Creators",
    component: () => import("@/views/Creators.vue"),
  },
  {
    path: "/creator/:id",
    name: "CreatorDetail",
    component: () => import("@/views/CreatorDetail.vue"),
  },
  {
    path: "/content/:id",
    name: "ContentDetail",
    component: () => import("@/views/ContentDetail.vue"),
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("@/views/Profile.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/my-follows",
    name: "MyFollows",
    component: () => import("@/views/MyFollows.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/creator-center",
    name: "CreatorCenter",
    component: () => import("@/views/CreatorCenter.vue"),
    meta: { requiresAuth: true, requiresCreator: true },
  },
  {
    path: "/apply-creator",
    name: "ApplyCreator",
    component: () => import("@/views/ApplyCreator.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/publish",
    name: "Publish",
    component: () => import("@/views/Publish.vue"),
    meta: { requiresAuth: true, requiresCreator: true },
  },
  {
    path: "/edit/:id",
    name: "Edit",
    component: () => import("@/views/Publish.vue"),
    meta: { requiresAuth: true, requiresCreator: true },
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: () => import("@/views/Dashboard.vue"),
    meta: { requiresAuth: true, requiresCreator: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: "Login", query: { redirect: to.fullPath } });
    return;
  }

  // 如果需要创作者权限，先刷新用户信息确保角色是最新的
  if (to.meta.requiresCreator) {
    await userStore.fetchUserInfo();
    if (!userStore.isCreator) {
      next({ name: "ApplyCreator" });
      return;
    }
  }

  next();
});

export default router;
