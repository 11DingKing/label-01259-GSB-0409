import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/",
    component: () => import("@/layouts/AdminLayout.vue"),
    meta: { requiresAuth: true },
    children: [
      {
        path: "",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
      },
      {
        path: "users",
        name: "Users",
        component: () => import("@/views/Users.vue"),
      },
      {
        path: "creators",
        name: "Creators",
        component: () => import("@/views/Creators.vue"),
      },
      {
        path: "contents",
        name: "Contents",
        component: () => import("@/views/Contents.vue"),
      },
      {
        path: "logs",
        name: "Logs",
        component: () => import("@/views/Logs.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: "Login" });
    return;
  }

  if (to.name === "Login" && userStore.isLoggedIn) {
    next({ name: "Dashboard" });
    return;
  }

  next();
});

export default router;
