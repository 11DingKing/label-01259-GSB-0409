import request from "./request";

export const getAnalyticsOverview = () => request.get("/analytics/overview");
